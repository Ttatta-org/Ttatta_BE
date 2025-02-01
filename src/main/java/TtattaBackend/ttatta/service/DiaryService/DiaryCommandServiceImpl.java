package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.DIARY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryCommandServiceImpl implements DiaryCommandService {
    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final DiaryCategoryRepository diaryCategoryRepository;

    private final AmazonS3Manager s3Manager;

    private final UuidRepository uuidRepository;

    private final DiaryPhotosRepository diaryPhotosRepository;

    @Override
    public Diaries save(DiaryRequestDTO.PostDTO request, MultipartFile diaryPhoto) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();
        DiaryCategories diaryCategories = diaryCategoryRepository.findById(request.getDiaryCategoryId()).get();

        // 일기
        Diaries diaries = DiaryConverter.toDiaries(request);

        diaries.setUsers(user);
        diaries.setDiaryCategories(diaryCategories);
        Diaries savedDiaries = diaryRepository.save(diaries);

        // 일기 사진
        DiaryPhotos diaryPhotos = savePhoto(diaryPhoto);

        diaryPhotos.setDiaries(savedDiaries);
        diaryPhotosRepository.save(diaryPhotos);

        return savedDiaries;
   }

   // s3 객체 사진 저장
   @Override
   public DiaryPhotos savePhoto(MultipartFile diaryPhoto) {
       String uuid = UUID.randomUUID().toString();
       Uuid savedUuid = uuidRepository.save(Uuid.builder()
               .uuid(uuid).build());
       String pictureUrl = s3Manager.uploadFile(s3Manager.generateDiaryKeyName(savedUuid), diaryPhoto);
       DiaryPhotos diaryPhotos = DiaryConverter.toDiaryPhoto(pictureUrl);

       return diaryPhotos;
   }

   @Override
   @Transactional
    public void delete(Long diaryId) {
        Diaries diaries = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ExceptionHandler(DIARY_NOT_FOUND));

        DiaryPhotos diaryPhoto = diaryPhotosRepository.findByDiaries_Id(diaries.getId());

        deletePhoto(diaryPhoto);

        diaryRepository.delete(diaries);

   }

   // s3에서 객체 삭제
   @Override
   public void deletePhoto(DiaryPhotos diaryPhoto) {
       String savedUuid = s3Manager.getUuidByUrl(diaryPhoto.getImageUrl());

       Uuid uuid = uuidRepository.findByUuid(savedUuid);
       uuidRepository.delete(uuid);

       s3Manager.deleteFile(s3Manager.generateDiaryKeyName(uuid));

       // db에서 삭제
       diaryPhotosRepository.delete(diaryPhoto);
   }


   @Override
   public Diaries edit(DiaryRequestDTO.EditDTO request, Long diaryId, MultipartFile editPhoto) {
        Diaries diaries = diaryRepository.findById((diaryId))
                .orElseThrow(() -> new ExceptionHandler(DIARY_NOT_FOUND));
        DiaryPhotos diaryPhoto = diaryPhotosRepository.findByDiaries_Id(diaries.getId());

        // 카테고리 수정
        request.getContent().ifPresent(diaries::updateContent);
        request.getDiaryCategoryId().ifPresent(diaryCategoryId -> {
            DiaryCategories diaryCategories = diaryCategoryRepository.findDiaryCategoriesById(diaryCategoryId);
            diaries.setDiaryCategories(diaryCategories);
        });

        // 사진 수정
        if(editPhoto != null) {
            deletePhoto(diaryPhoto);
            DiaryPhotos diaryPhotos = savePhoto(editPhoto);
            diaryPhotos.setDiaries(diaries);
        }

        return diaryRepository.save(diaries);
   }

}
