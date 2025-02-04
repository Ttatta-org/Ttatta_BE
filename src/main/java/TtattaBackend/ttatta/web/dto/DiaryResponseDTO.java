package TtattaBackend.ttatta.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        LocalDateTime date;
        String content;
        String image;
        String locationName;
    }
}