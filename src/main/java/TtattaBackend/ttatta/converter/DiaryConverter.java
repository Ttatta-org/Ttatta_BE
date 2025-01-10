package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;

import java.time.LocalDateTime;

public class DiaryConverter {

    public static DiaryResponseDTO.DiaryPostResultDTO toPostResultDTO(Diaries diaries) {
        return DiaryResponseDTO.DiaryPostResultDTO.builder()
                .diaryId(diaries.getId())
                .createdAt(LocalDateTime.now())
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
}
