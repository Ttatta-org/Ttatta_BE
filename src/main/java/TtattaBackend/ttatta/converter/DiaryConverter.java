package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;

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
                .build();
    }

    public static DiaryPhotos toDiaryPhoto(String pictureUrl, Diaries diaries) {
        return DiaryPhotos.builder()
                .imageUrl(pictureUrl)
                .diaries(diaries)
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
