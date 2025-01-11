package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;

import java.time.LocalDateTime;

public class DiaryConverter {

    public static DiaryResponseDTO.DiaryPostResultDTO toPostResultDTO(Diaries diaries) {
        return DiaryResponseDTO.DiaryPostResultDTO.builder()
                .diaryId(diaries.getId())
                .date(diaries.getDate())
                .build();
    }

    public static Diaries toDiaries(DiaryRequestDTO.DiaryPostDTO request) {
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
}
