package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;

    private static final int SEARCH_RANGE = 100;    // 검색 범위 설정 (100m)

    @Override
    public List<Diaries> getFootprintDiaryList(Long diaryCategoryId){
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        List<Diaries> diariesList;

        if(diaryCategoryId == null) {
            diariesList = diaryRepository.findAllByUsers(user);
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();
            diariesList = diaryRepository.findDiariesByUsersAndCategories(user, diaryCategories);
        }

        return diariesList;

    }

    @Override
    public Page<Diaries> getDiaryList(LocalDateTime date, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        Page<Diaries> diariesPage;

        if(date == null) {
            diariesPage = diaryRepository.findAllByUsersOrderByDateDesc(user, PageRequest.of(requestNum, 5));
        } else {
            diariesPage = diaryRepository.findAllByUsersAndDateOrderByDateDesc(user, date, PageRequest.of(requestNum, 5));
        }

        return diariesPage;
    }

    @Override
    public Page<Diaries> getSearchDiaryList(String content, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndContent(user, content, PageRequest.of(requestNum, 5));

        return diariesPage;
    }

    @Override
    public Page<Diaries> getMapDiaryList(Long clusterId, Long diaryCategoryId, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage;

        if(diaryCategoryId == null) {
            diariesPage = diaryRepository.findAllByUsersAndClusterId(user, clusterId, PageRequest.of(requestNum, 1));
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();
            diariesPage = diaryRepository.findAllByUsersAndClusterIdAndCategories(user, clusterId, diaryCategories, PageRequest.of(requestNum, 1));
        }

        return diariesPage;
    }

    @Override
    public List<LocalDateTime> getDiaryDateList() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return diaryRepository.findDistinctDatesByUser(user);
    }

    @Override
    public DiaryResponseDTO.RemindResultDTO findRemindDiary(DiaryRequestDTO.RemindDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(
                () -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        // 검색 범위 내 일기 검색
        List<Diaries> nearDiaries = diaryRepository.findNearByDiaries(user, request.getLatitude(), request.getLongitude(), SEARCH_RANGE);

        if (nearDiaries.isEmpty()) {    // 검색 범위 내에 일기가 없는 경우
            return DiaryResponseDTO.RemindResultDTO.builder()
                    .isRemind(false)
                    .message(SEARCH_RANGE + "m 이내에 일기가 없습니다.")
                    .build();
        }

        // 가장 가까운 일기 선택
        Diaries nearestDiary = nearDiaries.get(0);

        // 페이징 정보 계산
        Page<Diaries> diaryPage = diaryRepository.findAllByUsersAndClusterId(user, nearestDiary.getClusterId(), PageRequest.of(0, 1));

        // 시간 계산
        long days = ChronoUnit.DAYS.between(nearestDiary.getDate().toLocalDate(), LocalDate.now());
        String timeMessage;
        if (days < 7) {
            timeMessage = days + "일 전 이곳을 방문해 기록을 남겼어요";
        } else if (days < 30) {
            long weeks = days / 7;
            timeMessage = weeks + "주 전 이곳을 방문해 기록을 남겼어요";
        } else if (days < 365) {
            long months = days / 30;
            timeMessage = months + "개월 전 이곳을 방문해 기록을 남겼어요";
        } else {
            long years = days / 365;
            timeMessage = years + "년 전 이곳을 방문해 기록을 남겼어요";
        }

        return DiaryResponseDTO.RemindResultDTO.builder()
                .isRemind(true)
                .diary(DiaryConverter.toMapDiaryDTO(diaryPage))
                .message(timeMessage)
                .build();
    }

}
