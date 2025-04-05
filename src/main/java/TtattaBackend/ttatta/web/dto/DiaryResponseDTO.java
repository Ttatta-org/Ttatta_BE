package TtattaBackend.ttatta.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DiaryResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResultDTO {
        Long diaryId;
        LocalDateTime date;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FootprintDiaryListDTO {
        List<FootprintDiaryDTO> footprintList;
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FootprintDiaryDTO {
        Long diaryId;
        Long diaryCategoryId;
        String categoryColor;
        double latitude;
        double longitude;
        Long clusterId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditResultDTO {
        Long diaryId;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeepDiaryListDTO {
        List<KeepDiaryDTO> diaryList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeepDiaryDTO {
        Long diaryId;
        Long diaryCategoryId;
        LocalDateTime date;
        String content;
        String image;
        String locationName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapResultDTO {
        Long diaryId;
        Long diaryCategoryId;
        LocalDateTime date;
        String content;
        String image;
        boolean firstDiary;
        boolean lastDiary;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDiaryListDTO {
        List<SearchDiaryDTO> searchDiaryList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDiaryDTO {
        Long diaryId;
        Long diaryCategoryId;
        LocalDateTime date;
        String content;
        String image;
        String locationName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DairyDateListResultDTO {
        List<DiaryDateDTO> diaryDateList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryDateDTO {
        LocalDate date;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignedResultDTO {
        String presignedUrl;
        String objectKey;
    }
}