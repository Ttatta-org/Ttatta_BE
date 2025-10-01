package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.converter.LocationLogConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.DIARY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryCommandServiceImpl implements DiaryCommandService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final AmazonS3Manager s3Manager;
    private final DiaryPhotosRepository diaryPhotosRepository;
    private final LocationLogRepository locationLogRepository;

    @Override
    public Diaries save(DiaryRequestDTO.PostDTO request, GeometryFactory geometryFactory) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();
        DiaryCategories diaryCategories = diaryCategoryRepository.findById(request.getDiaryCategoryId()).get();

        Point pt = geometryFactory.createPoint(
                new Coordinate(request.getLongitude(),request.getLatitude()));

        pt.setSRID(4326);

        // 일기
        Diaries diaries = DiaryConverter.toDiaries(request, pt);

        diaries.setUsers(user);
        diaries.setDiaryCategories(diaryCategories);

        // 클러스터 Id 지정
        setClusterId(user, request, diaries);
        Diaries savedDiaries = diaryRepository.save(diaries);

        // 일기 사진
        DiaryPhotos diaryPhotos = savePhoto(request.getObjectKey());

        diaryPhotos.setDiaries(savedDiaries);
        diaryPhotosRepository.save(diaryPhotos);

        // 위치정보 활용 로그 저장
        locationLogRepository.save(LocationLogConverter.toLocationsLogs(user, "위치기반 일기 저장 서비스", null));
        return savedDiaries;
    }

    @Override
    public DiaryPhotos savePhoto(String objectKey) {
        DiaryPhotos diaryPhotos = DiaryConverter.toDiaryPhoto(objectKey);

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
        s3Manager.deleteFile(diaryPhoto.getImageUrl());

        // db에서 삭제
        diaryPhotosRepository.delete(diaryPhoto);
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