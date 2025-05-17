package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final DiaryCategoryRepository diaryCategoryRepository;

    private final AmazonS3Manager s3Manager;


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
    public List<String> getPresignedUrlAndKey(String imageType) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return s3Manager.getPresignedUrlAndKey(imageType, user.getId());
    }

    @Override
    public String getPresignedUrl(Long diaryId, String imageType) {
        Long userId = SecurityUtil.getCurrentUserId();
        userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        diaryRepository.findById(diaryId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DIARY_NOT_FOUND));
        return s3Manager.getPresignedUrl(diaryId, imageType);
    }

}
