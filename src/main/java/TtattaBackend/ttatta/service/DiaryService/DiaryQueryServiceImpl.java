package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.converter.LocationLogConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.service.AlarmService.AlaramType;
import TtattaBackend.ttatta.service.AlarmService.AlarmCommandService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final AmazonS3Manager s3Manager;
    private final AlarmCommandService alarmCommandService;
    private final MemoryDiaryAlarmRepository memoryDiaryAlarmRepository;
    private final LocationLogRepository locationLogRepository;

    private static final int SEARCH_RANGE = 100;    // 검색 범위 설정 (100m)

    @Override
    public DiaryResponseDTO.FootprintDiaryListDTO getFootprintDiaryList(Long diaryCategoryId, DiaryRequestDTO.ViewOnMapDTO request){
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        // 해당 화면 안에 있는 일기들만 조회
        List<Diaries> diariesList = getMapDiaryList(request);
        List<Object[]> countList;

        if(diaryCategoryId == null) {
            diariesList = filterLatestByClusterId(diariesList);
            countList = diaryRepository.countDiariesGroupByClusterId(user);
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();

            diariesList = diariesList.stream()
                    .filter(d -> d.getDiaryCategories().equals(diaryCategories))
                    .toList();

            diariesList = filterLatestByClusterId(diariesList);
            countList = diaryRepository.countDiariesGroupByClusterIdAndCategory(user, diaryCategories);
        }

        Map<Long, Long> countMap = countList.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        return DiaryConverter.toFootprintDiaryListDTO(diariesList, countMap);
    }

    public List<Diaries> filterLatestByClusterId(List<Diaries> diaries) {
        return diaries.stream()
                .filter(d -> d.getClusterId() != null)
                .collect(Collectors.toMap(
                        Diaries::getClusterId,
                        Function.identity(),
                        (d1, d2) -> d1.getDate().isAfter(d2.getDate()) ? d1 : d2
                ))
                .values()
                .stream()
                .toList();
    }

    @Override
    public DiaryResponseDTO.KeepDiaryListDTO getDiaryList(LocalDateTime date, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        Page<Diaries> diariesPage;

        if(date == null) {
            diariesPage = diaryRepository.findAllByUsersOrderByDateDesc(user, PageRequest.of(requestNum, 5));
        } else {
            diariesPage = diaryRepository.findAllByUsersAndDateOrderByDateDesc(user, date, PageRequest.of(requestNum, 5));
        }

        List<DiaryResponseDTO.KeepDiaryDTO> diaryDTOList = diariesPage.getContent().stream()
                .map(diary -> {
                    String presignedUrl = null;
                    List<DiaryPhotos> photoList = diary.getDiaryPhotosList();

                    if (photoList != null && !photoList.isEmpty()) {
                        String objectKey = photoList.get(0).getImageUrl();
                        presignedUrl = s3Manager.generatePresignedUrlForView(objectKey);
                    }

                    return DiaryConverter.toKeepDiaryDTO(diary, presignedUrl);
                })
                .collect(Collectors.toList());

        return DiaryResponseDTO.KeepDiaryListDTO.builder()
                .diaryList(diaryDTOList)
                .build();
    }

    @Override
    public DiaryResponseDTO.SearchDiaryListDTO getSearchDiaryList(String content, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndContent(user, content, PageRequest.of(requestNum, 5));

        List<DiaryResponseDTO.SearchDiaryDTO> diaryDTOList = diariesPage.getContent().stream()
                .map(diary -> {
                    String presignedUrl = null;
                    List<DiaryPhotos> photoList = diary.getDiaryPhotosList();

                    if (photoList != null && !photoList.isEmpty()) {
                        String objectKey = photoList.get(0).getImageUrl();
                        presignedUrl = s3Manager.generatePresignedUrlForView(objectKey);
                    }

                    return DiaryConverter.toSearchDiaryDTO(diary, presignedUrl);
                })
                .collect(Collectors.toList());

        return DiaryResponseDTO.SearchDiaryListDTO.builder()
                .searchDiaryList(diaryDTOList)
                .build();
    }

    @Override
    public DiaryResponseDTO.MapResultDTO getMapDiaryList(Long clusterId, Long diaryCategoryId, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage;

        if(diaryCategoryId == null) {
            diariesPage = diaryRepository.findAllByUsersAndClusterId(user, clusterId, PageRequest.of(requestNum, 1));
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();
            diariesPage = diaryRepository.findAllByUsersAndClusterIdAndCategories(user, clusterId, diaryCategories, PageRequest.of(requestNum, 1));
        }

        Diaries diary = diariesPage.getContent().get(0);

        String presignedUrl = null;
        List<DiaryPhotos> photoList = diary.getDiaryPhotosList();

        if (photoList != null && !photoList.isEmpty()) {
            String objectKey = photoList.get(0).getImageUrl();
            presignedUrl = s3Manager.generatePresignedUrlForView(objectKey);
        }
        saveLocationLog(user, "지도 일기 페이징 조회 서비스", user.getId().toString());
        return DiaryConverter.toMapDiaryDTO(diariesPage, presignedUrl);
    }

    @Override
    public List<LocalDateTime> getDiaryDateList() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return diaryRepository.findDistinctDatesByUser(user);
    }

    @Override
    public void findRemindDiary(DiaryRequestDTO.RemindDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(
                () -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        // 위치 기반 추억 회상 알림이 꺼져있는 경우
        MemoryDiaryAlarm memoryDiaryAlarm = memoryDiaryAlarmRepository.findByUsers(user)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMORY_DIARY_ALARM_NOT_FOUND));
        if (memoryDiaryAlarm.getIsActive() == IsActive.OFF) {
            return;
        }

        // 검색 범위 내 일기 검색
        List<Diaries> nearDiaries = diaryRepository.findNearByDiaries(user, request.getLatitude(), request.getLongitude(), SEARCH_RANGE);

        if (nearDiaries.isEmpty()) {    // 검색 범위 내에 일기가 없는 경우
            return;
        }

        // 가장 가까운 일기 선택
        Diaries nearestDiary = nearDiaries.get(0);

        // 페이징 정보 계산
        Page<Diaries> diaryPage = diaryRepository.findAllByUsersAndClusterId(user, nearestDiary.getClusterId(), PageRequest.of(0, 1));

        // 시간 계산
        long days = ChronoUnit.DAYS.between(nearestDiary.getDate().toLocalDate(), LocalDate.now());
        String timeMessage;
        if (days < 7) {
            timeMessage = days + "일";
        } else if (days < 30) {
            long weeks = days / 7;
            timeMessage = weeks + "주";
        } else if (days < 365) {
            long months = days / 30;
            timeMessage = months + "개월";
        } else {
            long years = days / 365;
            timeMessage = years + "년";
        }

        // 알림 보내기
        alarmCommandService.sendMemoryDiaryAlarm(user, timeMessage, nearestDiary.getId());

        // 위치 정보 이용 로그 저장
        locationLogRepository.save(LocationLogConverter.toLocationsLogs(user, "위치기반리마인드 알림 서비스", user.getId().toString()));
    }

    @Override
    public List<String> getPresignedForPost(String imageType) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return s3Manager.getPresignedUrlAndKey(imageType, user.getId());
    }

    @Override
    public String getPresignedUrlForEdit(Long diaryId, String imageType) {
        Long userId = SecurityUtil.getCurrentUserId();
        userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        diaryRepository.findById(diaryId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DIARY_NOT_FOUND));
        return s3Manager.getPresignedUrl(diaryId, imageType);
    }


    public List<Diaries> getMapDiaryList(DiaryRequestDTO.ViewOnMapDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        // ▶▶ WKT 문자열 생성 (경도(lng) 먼저, 위도(lat) 나중)
        String wkt = String.format(
                "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                request.getLat1(), request.getLng1(),  // NE
                request.getLat2(), request.getLng2(),  // SE
                request.getLat3(), request.getLng3(),  // SW
                request.getLat4(), request.getLng4(),  // NW
                request.getLat1(), request.getLng1()   // 닫기 (NE)
        );
        log.info("▶▶ Passing WKT to repo = {}", wkt);


        long start = System.currentTimeMillis();
        // 쿼리 파라미터로 바로 넘깁니다
        List<Diaries> viewOnMapDiaries = diaryRepository.findAllByUserIdAndCoordinates(
                wkt, user.getId()
        );

//        Point getLocation = viewOnMapDiaries.get(0).getLocation();
//        log.info("▶▶ Passing location Point : " + getLocation.toString());

        long end = System.currentTimeMillis();
//        System.out.println("쿼리 실행 시간: " + (end - start) + "ms");

        return viewOnMapDiaries;
    }

    private void saveLocationLog(Users user, String provisionalService, String Receipient) {
        locationLogRepository.save(LocationLogConverter.toLocationsLogs(user, provisionalService, Receipient));
    }
}
