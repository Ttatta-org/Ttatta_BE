package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryPhotoServiceImpl implements DiaryPhotoService{
    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final DiaryCategoryRepository diaryCategoryRepository;

    private final AmazonS3Manager s3Manager;

    private final UuidRepository uuidRepository;

    private final DiaryPhotosRepository diaryPhotosRepository;

    @Override
    public Diaries save(DiaryRequestDTO.PostDTO request, MultipartFile diaryPhotos) {
        Users user = userRepository.findById(request.getUserId()).get();
        DiaryCategories diaryCategories = diaryCategoryRepository.findById(request.getDiaryCategoryId()).get();

        Diaries diaries = DiaryConverter.toDiaries(request);

        diaries.setUsers(user);
        diaries.setDiaryCategories(diaryCategories);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        Diaries savedDiaries = diaryRepository.save(diaries);

        String pictureUrl = s3Manager.uploadFile(s3Manager.generateDiaryKeyName(savedUuid), diaryPhotos);
        diaryPhotosRepository.save(DiaryConverter.toDiaryPhoto(pictureUrl, diaries));

        return savedDiaries;
   }

   @Override
   @Transactional
    public void delete(Long diaryId) {
        Diaries diaries = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ExceptionHandler(DIARY_NOT_FOUND));

        DiaryPhotos diaryPhoto = diaryPhotosRepository.findByDiaries_Id(diaries.getId());

        String savedUuid = s3Manager.getUuidByUrl(diaryPhoto.getImageUrl());

        Uuid uuid = uuidRepository.findByUuid(savedUuid);
        uuidRepository.delete(uuid);

        s3Manager.deleteFile(s3Manager.generateDiaryKeyName(uuid));

        diaryRepository.delete(diaries);

   }

}
