package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.aws.s3.AmazonS3Manager;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.converter.LocationLogConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.security.DecryptedLocation;
import TtattaBackend.ttatta.security.EncryptedLocation;
import TtattaBackend.ttatta.security.EnvelopeCryptoService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.*;
import static java.lang.Math.floor;
import static java.lang.Math.round;

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

    // 암호화 저장용
    private final EnvelopeCryptoService envelopeCryptoService;

    private final GeometryFactory geometryFactory;

    @Override
    @Transactional
    public Diaries save(DiaryRequestDTO.PostDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 원래는 .get()이었음. 잘 안되면 여기 수정!!!
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        DiaryCategories diaryCategories = diaryCategoryRepository.findById(request.getDiaryCategoryId())
                .orElseThrow(() -> new ExceptionHandler(DIARY_CATEGORY_NOT_FOUND));


        // 저정밀한 위도 경도 값을 POINT 형으로 저장하기
        // 소수점 3자리까지 나타냄. (약 100m의 오차)
        Point pt = geometryFactory.createPoint(
                new Coordinate(round(request.getLongitude(),3) ,round(request.getLatitude(),3)));

        pt.setSRID(4326);

        // 암호화 (AAD로 userId 등 고정 식별자를 얹음?? -> 확인 필요)
        EncryptedLocation enc = envelopeCryptoService.encryptLatLng(
                request.getLatitude(),request.getLongitude(),user.getId()
        );


        // 일기
        Diaries diaries = DiaryConverter.toDiaries(request, pt, enc);

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

        // 유저 검증
        Long userId = SecurityUtil.getCurrentUserId();
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 여기서 clusterId 중 가장 최신 일기만 조회
        List<Diaries> latestDiaries = diaryRepository.findLatestUniqueClusterIdDiary(currentUser);
        System.out.println("나오나??" + latestDiaries.toString());

        double newLatitude = floor((request.getLatitude()) * 100000.0);
        double newLongitude = floor((request.getLongitude()) * 100000.0);

        // clusterId중 가장 최신 일기의 위도 경도만 복호화 후 비교
        Optional<Long> matchedClusterId = Optional.empty();
        for(Diaries latest : latestDiaries) {
            DecryptedLocation decryptedLocation = envelopeCryptoService.decryptLatLng(latest.getLatCipher(), latest.getIvLat(), latest.getLngCipher(), latest.getIvLng(), latest.getDekWrapped(), latest.getKmsKeyId(), latest.getUsers().getId());
            double originLatitude = floor(decryptedLocation.lat() * 100000.0);
            double originLongitude = floor(decryptedLocation.lng() * 100000.0);

            if(originLatitude == newLatitude && originLongitude ==  newLongitude) {
                Long clusterId = latest.getClusterId();
                if(clusterId != null) {
                    matchedClusterId = Optional.of(clusterId);
                }
                break;
            }
        }

        if(matchedClusterId.isPresent()) {
            // 장소 같은 경우
            diaries.setClusterId(matchedClusterId.get());
        } else { // 장소 다름
            // 가장 최근 클러스터 id
            Optional<Diaries> clusterDiary = diaryRepository.findTop1ClusterIdByUsersOrderByClusterIdDesc(user);

            if(clusterDiary.isPresent()) {
                Long existingClusterId = clusterDiary.get().getClusterId();
                Long newClusterId = (existingClusterId != null) ? existingClusterId + 1 : 0L;
                diaries.setClusterId(newClusterId);
            } else {
                // 첫 일기
                diaries.setClusterId(0L);
            }
        }
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
