package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final DiaryCategoryRepository diaryCategoryRepository;

    private final AmazonS3Manager s3Manager;


    @Override
    public DiaryResponseDTO.FootprintDiaryListDTO getFootprintDiaryList(Long diaryCategoryId){
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        List<Diaries> diariesList;
        List<Object[]> countList;

        if(diaryCategoryId == null) {
            diariesList = diaryRepository.findAllByUsers(user);
            countList = diaryRepository.countDiariesGroupByClusterId(user);
        } else {
            DiaryCategories diaryCategories = diaryCategoryRepository.findById(diaryCategoryId).get();
            diariesList = diaryRepository.findDiariesByUsersAndCategories(user, diaryCategories);
            countList = diaryRepository.countDiariesGroupByClusterIdAndCategory(user, diaryCategories);
        }

        Map<Long, Long> countMap = countList.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return DiaryConverter.toFootprintDiaryListDTO(diariesList, countMap);

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

}