package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Uuid;
import TtattaBackend.ttatta.repository.DiaryPhotosRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UuidRepository;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiaryPhotoServiceImpl implements DiaryPhotoService{
    private final DiaryRepository diaryRepository;

    private final AmazonS3Manager s3Manager;

    private final UuidRepository uuidRepository;

    private final DiaryPhotosRepository diaryPhotosRepository;

    @Override
    public Diaries save(DiaryRequestDTO.DiaryPostDTO request, List<MultipartFile> diaryPhotos) {
        Diaries diaries = DiaryConverter.toDiaries(request);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        Diaries savedDiaries = diaryRepository.save(diaries);

        for(MultipartFile diaryPhoto : diaryPhotos) {
            String pictureUrl = s3Manager.uploadFile(s3Manager.generateDiaryKeyName(savedUuid), diaryPhoto);
            diaryPhotosRepository.save(DiaryConverter.toDiaryPhoto(pictureUrl, diaries));
        }

        return savedDiaries;
   }
}
