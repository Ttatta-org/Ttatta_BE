package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;

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

    public static DiaryPhotos toDiaryPhoto(String pictureUrl) {
        return DiaryPhotos.builder()
                .imageUrl(pictureUrl)
                .build();
    }

    public static DiaryResponseDTO.FootprintDiaryDTO footprintDiaryDTO(Diaries diaries) {
        return DiaryResponseDTO.FootprintDiaryDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .categoryColor(diaries.getDiaryCategories().getColor().toString())
                .latitude(diaries.getLatitude())
                .longitude(diaries.getLongitude())
                .build();
    }

    public static DiaryResponseDTO.FootprintDiaryListDTO footprintDiaryListDTO(List<Diaries> diariesList) {
       List<DiaryResponseDTO.FootprintDiaryDTO> footprintDiaryDTOList = diariesList.stream()
               .map(DiaryConverter::footprintDiaryDTO).collect(Collectors.toList());

       return DiaryResponseDTO.FootprintDiaryListDTO.builder()
               .footprintList(footprintDiaryDTOList)
               .build();
    }

    public static DiaryResponseDTO.KeepResultDTO toKeepResultDTO(Diaries diaries) {
        return null;
    }

    public static DiaryResponseDTO.MapResultDTO toMapResultDTO(Diaries diaries) {
        return DiaryResponseDTO.MapResultDTO.builder()
                .diaryId(diaries.getId())
                .diaryCategoryId(diaries.getDiaryCategories().getId())
                .date(diaries.getDate())
                .content(diaries.getContent())
                .image(diaries.getDiaryPhotosList().toString())
                .build();

    }

    public static DiaryResponseDTO.SearchResultDTO toSearchResultDTO(Diaries diaries) {
        return null;
    }
}
