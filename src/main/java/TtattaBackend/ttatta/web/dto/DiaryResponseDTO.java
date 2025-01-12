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
    public static class DiaryPostResultDTO {
        Long diaryId;
        LocalDateTime date;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeepResultDTO {
        List<KeepDiary> diaryList;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KeepDiary {
            Long diaryId;
            LocalDateTime date;
            String content;
            String image;
        }
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
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        List<SearchDiary> diaryList;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SearchDiary {
            Long diaryId;
            LocalDateTime date;
            String content;
            String image;
        }
    }
}
