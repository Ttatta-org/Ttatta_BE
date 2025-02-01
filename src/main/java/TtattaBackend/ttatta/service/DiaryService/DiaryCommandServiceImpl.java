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

import java.util.Optional;
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
        Users user = userRepository.findById(request.getUserId()).get();
        DiaryCategories diaryCategories = diaryCategoryRepository.findById(request.getDiaryCategoryId()).get();

        // 일기
        Diaries diaries = DiaryConverter.toDiaries(request);

        diaries.setUsers(user);
        diaries.setDiaryCategories(diaryCategories);

        // 클러스터 Id 지정
        setClusterId(user, request, diaries);
        Diaries savedDiaries = diaryRepository.save(diaries);

        // 일기 사진
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        String pictureUrl = s3Manager.uploadFile(s3Manager.generateDiaryKeyName(savedUuid), diaryPhoto);
        DiaryPhotos diaryPhotos = DiaryConverter.toDiaryPhoto(pictureUrl);

        diaryPhotos.setDiaries(savedDiaries);
        diaryPhotosRepository.save(diaryPhotos);

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

   @Override
   public Diaries edit(DiaryRequestDTO.EditDTO request, Long diaryId) {
        Diaries diaries = diaryRepository.findById((diaryId))
                .orElseThrow(() -> new ExceptionHandler(DIARY_NOT_FOUND));

        request.getContent().ifPresent(diaries::updateContent);
        request.getDiaryCategoryId().ifPresent(diaryCategoryId -> {
            DiaryCategories diaryCategories = diaryCategoryRepository.findDiaryCategoriesById(diaryCategoryId);
            diaries.setDiaryCategories(diaryCategories);
        });

        return diaryRepository.save(diaries);
   }

   @Override
   public void setClusterId(Users user, DiaryRequestDTO.PostDTO request, Diaries diaries) {
       Optional<Long> existClusterId = diaryRepository.findFirstClusterIdByUsersAndLatitudeAndLongitude(user, request.getLatitude(), request.getLongitude());

       if(existClusterId.isPresent()) {
           // 장소 같은 경우
           diaries.setClusterId(existClusterId.get());
       } else { // 장소 다름
           // 가장 최근 클러스터 id
           Optional<Diaries> clusterDiary = diaryRepository.findTop1ClusterIdByUsersOrderByClusterIdDesc(user);

           if(clusterDiary.isPresent()) {
               Long newClusterId = clusterDiary.get().getClusterId();
               diaries.setClusterId(newClusterId + 1);
           } else {
               // 첫 일기
               diaries.setClusterId(0L);
           }
       }
   }
}
