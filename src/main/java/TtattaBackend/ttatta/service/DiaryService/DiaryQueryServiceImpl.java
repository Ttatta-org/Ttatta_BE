package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.MemoryDiaryAlarmRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.service.AlarmService.AlarmCommandService;
import TtattaBackend.ttatta.security.DecryptedLocation;
import TtattaBackend.ttatta.security.EnvelopeCryptoService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.GeometryFactory;
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

    static final double M_PER_DEG_LAT = 111_320.0; // 검색 범위 설정
    private static final int SEARCH_RANGE = 100;    // 검색 범위 설정 (100m)
    private final GeometryFactory geometryFactory;
    private final EnvelopeCryptoService envelopeCryptoService;

    @Override
    public DiaryResponseDTO.FootprintDiaryListDTO getFootprintDiaryList(Long diaryCategoryId, DiaryRequestDTO.ViewOnMapDTO request){
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        // 해당 화면 안에 있는 일기 후보들 조회 (저정밀 POINT)
        List<Diaries> diariesListCandidates = getMapDiaryList(request);
        List<Object[]> countList;

        // 사용자의 네 꼭지점을 Polygon 형태로 변환
        Polygon viewPort = buildPolygonFromUserScreen(request);
        var prep = org.locationtech.jts.geom.prep.PreparedGeometryFactory.prepare(viewPort);

        // 디코딩을 해서 실제로 포함하는지 확인
        List<Diaries> insideDiaries = diariesListCandidates.parallelStream()
                .map(d -> tryDecode(d, user.getId()))
                .filter(Decoded::ok)
                .filter(dd -> {
                    Point p = geometryFactory.createPoint(new Coordinate(dd.lng(), dd.lat()));
                    p.setSRID(4326);
                    return prep.contains(p);
                })
                .map(Decoded::diary)
                .toList();


        if(diaryCategoryId == null) {
            insideDiaries = filterLatestByClusterId(insideDiaries);
            countList = diaryRepository.countDiariesGroupByClusterId(user);
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();

            insideDiaries = insideDiaries.stream()
                    .filter(d -> d.getDiaryCategories().equals(diaryCategories))
                    .toList();

            insideDiaries = filterLatestByClusterId(insideDiaries);
            countList = diaryRepository.countDiariesGroupByClusterIdAndCategory(user, diaryCategories);
        }

        Map<Long, Long> countMap = countList.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return DiaryConverter.toFootprintDiaryListDTO(insideDiaries, countMap);
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
                () -> new ExceptionHandler(USER_NOT_FOUND));

        // 위치 기반 추억 회상 알림이 꺼져있는 경우
        MemoryDiaryAlarm memoryDiaryAlarm = memoryDiaryAlarmRepository.findByUsers(user)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMORY_DIARY_ALARM_NOT_FOUND));
        if (memoryDiaryAlarm.getIsActive() == IsActive.OFF) {
            return;
        }

        // 범위 내 좌표들을 얻어서 POINT를 사용하여 정사각형 내 후보 일기들을 뽑는다.
        double currentLatitude = request.getLatitude();
        double currentLongitude = request.getLongitude();
        double sideMeters = 200.0;

        List<Pt> square = buildSquare(currentLatitude, currentLongitude, sideMeters);
        String wkt = toPolygonWKT(square);
        List<Diaries> nearDiariesCandidates = diaryRepository.findNearDiariesCandidates(wkt, user.getId());
        for (Diaries diary : nearDiariesCandidates) {
            System.out.println("nearDiariesCandidates id: " + diary.getId());
        }


        if (nearDiariesCandidates.isEmpty()) {    // 검색 범위 내에 일기가 없는 경우
            return;
        }

        // 검색 범위(정사각형) 내 복호화를 진행하고, 실제로 일기 위도 경도에서 100m 원 안에 있는 일기 중 가장 최신 일기 반환
        // 영 이상하면 getDate -> getCreatedAt으로 수정 (마지막줄)
        Optional<Diaries> nearestDiary = nearDiariesCandidates
                .parallelStream()
                .map(d -> tryDecode(d, user.getId()))   // Decoded(diaries, lat, lng, ok)
                .filter(Decoded::ok)
                .filter(dd -> haversineMeters(currentLatitude, currentLongitude, dd.lat(), dd.lng()) <= SEARCH_RANGE)
                .map(Decoded::diary)
                .max(Comparator.comparing(Diaries::getDate, Comparator.nullsLast(Comparator.naturalOrder())
                ));
        System.out.println("nearestDiaries real Candidates: " + nearestDiary.get().getId());


        // 시간 계산
        long days = ChronoUnit.DAYS.between(nearestDiary.get().getDate().toLocalDate(), LocalDate.now());
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
        alarmCommandService.sendMemoryDiaryAlarm(user, timeMessage, nearestDiary.get().getId());
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


    // 지도상의 후보군 일기들을 나타냄
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

    // 화면상의 네 꼭지점 받아옴
    private Polygon buildPolygonFromUserScreen(DiaryRequestDTO.ViewOnMapDTO req) {
        // 네 꼭지점이 시계/반시계 순서로 들어온다고 가정. (lng, lat) 순서 유의
        Coordinate[] ring = new Coordinate[] {
                new Coordinate(req.getLng1(), req.getLat1()),
                new Coordinate(req.getLng2(), req.getLat2()),
                new Coordinate(req.getLng3(), req.getLat3()),
                new Coordinate(req.getLng4(), req.getLat4()),
                new Coordinate(req.getLng1(), req.getLat1()) // close
        };
        LinearRing shell = geometryFactory.createLinearRing(ring);
        Polygon poly = geometryFactory.createPolygon(shell, null);
        poly.setSRID(4326);
        return poly;
    }

    // 복호화 래퍼 (실패 안전)
    private Decoded tryDecode(Diaries d, Long userId) {
        try {
            log.info("▶ decode start: diaryId={}, kmsKeyId='{}', dekWrappedLen={}",
                    d.getId(),
                    d.getKmsKeyId(),
                    d.getDekWrapped() == null ? null : d.getDekWrapped().length);
            // 구현에 맞는 decrypt 메서드 사용
            DecryptedLocation loc = envelopeCryptoService.decryptLatLng(
                    d.getLatCipher(), d.getIvLat(),
                    d.getLngCipher(), d.getIvLng(),
                    d.getDekWrapped(), d.getKmsKeyId(),
                    userId // AAD seed
            );
            return new Decoded(d, loc.lat(), loc.lng(), true);
        } catch (Exception e) {
            log.warn("decode failed for diary id={}", d.getId(), e);
            return new Decoded(d, 0, 0, false);
        }
    }

    // 복호화를 위함
    private record Decoded(Diaries diary, double lat, double lng, boolean ok) {}

    record Pt(double lat, double lng) {}

    static List<Pt> buildSquare(double centerLat, double centerLng, double sideMeters) {
        double half = sideMeters / 2.0; // 100m
        double metersPerDegLng = M_PER_DEG_LAT * Math.cos(Math.toRadians(centerLat));

        // 위도/경도 차이
        double dLat = half / M_PER_DEG_LAT;
        double dLng = half / metersPerDegLng;

        List<Pt> pts = new ArrayList<>(5);
        pts.add(new Pt(centerLat + dLat, centerLng + dLng)); // NE
        pts.add(new Pt(centerLat + dLat, centerLng - dLng)); // NW
        pts.add(new Pt(centerLat - dLat, centerLng - dLng)); // SW
        pts.add(new Pt(centerLat - dLat, centerLng + dLng)); // SE
        pts.add(new Pt(centerLat + dLat, centerLng + dLng)); // 다시 닫기
        return pts;
    }

    static String toPolygonWKT(List<Pt> pts) {
        // ⚠️ WKT: "lng lat" 순서
        String coords = pts.stream()
                .map(p -> p.lat + " " + p.lng)
                .collect(java.util.stream.Collectors.joining(", "));
        return "POLYGON((" + coords + "))";
    }

    static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6_371_000; // 지구 반지름 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }
}
