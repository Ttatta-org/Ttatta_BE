package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.security.DecryptedLocation;
import TtattaBackend.ttatta.security.EncryptedLocation;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiaryConverter {

    public static DiaryResponseDTO.PostResultDTO toPostResultDTO(Diaries diaries) {
        return DiaryResponseDTO.PostResultDTO.builder()
                .diaryId(diaries.getId())
                .date(diaries.getDate())
                .build();
    }

    public static Diaries toDiaries(DiaryRequestDTO.PostDTO request, Point pt, EncryptedLocation enc) {
        return Diaries.builder()
                .content(request.getContent())
                .date(request.getDate())
//                .latitude(request.getLatitude())
//                .longitude(request.getLongitude())
                .location(pt)
                .locationName(request.getLocationName())
                .diaryPhotosList(new ArrayList<>()) // 여기서 diaryPhotosList명시적으로 초기화!
                // 암호화 필드
                .latCipher(enc.getLatCipher())
                .lngCipher(enc.getLngCipher())
                .ivLat(enc.getIvLat())
                .ivLng(enc.getIvLng())
                .dekWrapped(enc.getDekWrapped())
                .kmsKeyId(enc.getKmsKeyId())
                .encVer(enc.getEncVer())
                .build();
    }

    public static DiaryResponseDTO.EditResultDTO toEditResultDTO(Diaries diaries) {
        return DiaryResponseDTO.EditResultDTO.builder()
                .diaryId(diaries.getId())
                .updatedAt(diaries.getUpdatedAt())
                .build();
    }

    public static DiaryPhotos toDiaryPhoto(String pictureUrl) {
        return DiaryPhotos.builder()
                .imageUrl(pictureUrl)
                .build();
    }

    public static DiaryResponseDTO.FootprintDiaryDTO toFootprintDiaryDTO(Diaries diaries, Map<Long, Long> count, Map<Long, DecryptedLocation> location) {
        Long clusterId = diaries.getClusterId();
        Long clusterCount = count.getOrDefault(clusterId, 1L);
        DecryptedLocation diaryLocation = location.get(diaries.getId());

        return DiaryResponseDTO.FootprintDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .categoryColor(diaries.getDiaryCategories().getColor().toString())
                .latitude(diaryLocation.lat())
                .longitude(diaryLocation.lng())
//                .latitude(diaries.getLatitude())
//                .longitude(diaries.getLongitude())
                .clusterId(diaries.getClusterId())
                .isSingle(clusterCount == 1)
                .build();
    }

    public static DiaryResponseDTO.FootprintDiaryListDTO toFootprintDiaryListDTO(List<Diaries> diariesList, Map<Long,Long> count, Map<Long, DecryptedLocation> location) {
        List<DiaryResponseDTO.FootprintDiaryDTO> footprintDiaryDTOList = diariesList.stream()
                .map(diary -> toFootprintDiaryDTO(diary, count, location))
                .collect(Collectors.toList());

        return DiaryResponseDTO.FootprintDiaryListDTO.builder()
                .footprintList(footprintDiaryDTOList)
                .build();
    }

    public static DiaryResponseDTO.KeepDiaryDTO toKeepDiaryDTO(Diaries diaries) {
        String presignedUrl = diaries.getDiaryPhotosList().stream()
                .map(DiaryPhotos::getImageUrl).collect(Collectors.toList()).get(0);

        return DiaryResponseDTO.KeepDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(presignedUrl)
                .locationName(diaries.getLocationName())
                .build();
    }

    public static DiaryResponseDTO.KeepDiaryDTO toKeepDiaryDTO(Diaries diaries, String presignedUrl) {
        return DiaryResponseDTO.KeepDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(presignedUrl)
                .locationName(diaries.getLocationName())
                .build();
    }

    public static DiaryResponseDTO.KeepDiaryListDTO toKeepDiaryListDTO(Page<Diaries> diaryList) {
        List<DiaryResponseDTO.KeepDiaryDTO> keepDiaryDTOList = diaryList.stream()
                .map(DiaryConverter::toKeepDiaryDTO).collect(Collectors.toList());

        return DiaryResponseDTO.KeepDiaryListDTO.builder()
                .diaryList(keepDiaryDTOList)
                .build();
    }

    public static DiaryResponseDTO.MapResultDTO toMapDiaryDTO(Page<Diaries> diaryList) {
        Diaries diaries = diaryList.getContent().stream().findFirst().get();

        String imageUrl = diaries.getDiaryPhotosList().stream()
                .map(DiaryPhotos::getImageUrl).collect(Collectors.toList()).get(0);

        return DiaryResponseDTO.MapResultDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(imageUrl)
                .color(diaries.getDiaryCategories().getColor())
                .firstDiary(diaryList.isFirst())
                .lastDiary(diaryList.isLast())
                .build();

    }

    public static DiaryResponseDTO.MapResultDTO toMapDiaryDTO(Page<Diaries> diaryList, String presignedUrl) {
        Diaries diaries = diaryList.getContent().stream().findFirst().get();

        return DiaryResponseDTO.MapResultDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(presignedUrl)
                .firstDiary(diaryList.isFirst())
                .lastDiary(diaryList.isLast())
                .build();

    }

    public static DiaryResponseDTO.SearchDiaryDTO toSearchDiaryDTO(Diaries diaries, String presignedUrl) {
        return DiaryResponseDTO.SearchDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .content(diaries.getContent())
                .date(diaries.getDate())
                .image(presignedUrl)
                .locationName(diaries.getLocationName())
                .build();
    }


    public static DiaryResponseDTO.SearchDiaryDTO toSearchDiaryDTO(Diaries diaries) {
        String imageUrl = diaries.getDiaryPhotosList().stream()
                .map(DiaryPhotos::getImageUrl).collect(Collectors.toList()).get(0);

        return DiaryResponseDTO.SearchDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .content(diaries.getContent())
                .date(diaries.getDate())
                .image(imageUrl)
                .locationName(diaries.getLocationName())
                .build();
    }

    public static DiaryResponseDTO.SearchDiaryListDTO toSearchDiaryListDTO(Page<Diaries> diaryList) {
        List<DiaryResponseDTO.SearchDiaryDTO> searchDiaryList = diaryList.stream()
                .map(DiaryConverter::toSearchDiaryDTO).collect(Collectors.toList());


        return DiaryResponseDTO.SearchDiaryListDTO.builder()
                .searchDiaryList(searchDiaryList)
                .build();
    }

    public static DiaryResponseDTO.DiaryDateDTO toDiaryDateDTO(LocalDateTime diaryDate) {
        return DiaryResponseDTO.DiaryDateDTO.builder()
                .date(diaryDate.toLocalDate())
                .build();
    }

    public static DiaryResponseDTO.DairyDateListResultDTO toDairyDateListResultDTO(List<LocalDateTime> diaryDateList) {
        List<DiaryResponseDTO.DiaryDateDTO> dairyDateListResultDTOList = diaryDateList.stream()
                .map(DiaryConverter::toDiaryDateDTO).collect(Collectors.toList());

        return DiaryResponseDTO.DairyDateListResultDTO.builder()
                .diaryDateList(dairyDateListResultDTOList)
                .build();
    }

    public static DiaryResponseDTO.PresignedResultDTO toPresignedUrlResultDTO(List<String> urlList) {
        return DiaryResponseDTO.PresignedResultDTO.builder()
                .presignedUrl(urlList.get(0))
                .objectKey(urlList.get(1))
                .build();
    }

    public static DiaryResponseDTO.EditPresignedResultDTO toPresignedUrlResultDTO(String url) {
        return DiaryResponseDTO.EditPresignedResultDTO.builder()
                .presignedUrl(url)
                .build();
    }

    public static DiaryResponseDTO.MapResultDTO toMapResultDTO(Diaries diary, String presignedUrl) {
        return DiaryResponseDTO.MapResultDTO.builder()
                .diaryId(diary.getId())
                .diaryCategoryId(diary.getDiaryCategories().getId())
                .date(diary.getDate())
                .content(diary.getContent())
                .image(presignedUrl)
                .firstDiary(false) // 사용 안 하거나 false 고정
                .lastDiary(false)
                .build();
    }

    public static DiaryResponseDTO.ViewOnMapResultDTO toViewOnMapResultDTO(List<Diaries> diaryList, List<String> presignedUrlList) {
        List<DiaryResponseDTO.MapResultDTO> resultList = IntStream.range(0, diaryList.size())
                .mapToObj(i -> DiaryConverter.toMapResultDTO(diaryList.get(i), presignedUrlList.get(i)))
                .toList();

        return DiaryResponseDTO.ViewOnMapResultDTO.builder()
                .viewOnMapList(resultList)
                .build();
    }
}