package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryConverter {

    public static DiaryResponseDTO.PostResultDTO toPostResultDTO(Diaries diaries) {
        return DiaryResponseDTO.PostResultDTO.builder()
                .diaryId(diaries.getId())
                .date(diaries.getDate())
                .build();
    }

    public static Diaries toDiaries(DiaryRequestDTO.PostDTO request) {
        return Diaries.builder()
                .content(request.getContent())
                .date(request.getDate())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .diaryPhotosList(new ArrayList<>()) // 여기서 diaryPhotosList명시적으로 초기화!
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

    public static DiaryResponseDTO.KeepDiaryDTO toKeepDiaryDTO(Diaries diaries) {
        String imageUrl = diaries.getDiaryPhotosList().stream()
                .map(DiaryPhotos::getImageUrl).collect(Collectors.toList()).get(0);

        return DiaryResponseDTO.KeepDiaryDTO.builder()
                .diaryId(diaries.getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(imageUrl)
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
                .build();

    }

    public static DiaryResponseDTO.SearchDiaryDTO toSearchDiaryDTO(Diaries diaries) {
        String imageUrl = diaries.getDiaryPhotosList().stream()
                .map(DiaryPhotos::getImageUrl).collect(Collectors.toList()).get(0);

        return DiaryResponseDTO.SearchDiaryDTO.builder()
                .diaryId(diaries.getId())
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
}
