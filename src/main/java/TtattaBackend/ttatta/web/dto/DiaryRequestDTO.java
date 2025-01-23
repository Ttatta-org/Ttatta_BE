package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

public class DiaryRequestDTO {
    @Getter
    public static class PostDTO {
        @ExistUser
        private Long userId;

        @ExistDiaryCategory
        private Long diaryCategoryId;
        private String content;
        private LocalDateTime date;

        private double latitude;
        private double longitude;
        private String locationName;

    }

    @Getter
    public static class DeleteDTO {
        @ExistUser
        private Long userId;
    }

    @Getter
    public static class EditDTO {
        private Long userId;
        private String content;
    }

    @Getter
    public static class FootprintDTO {
        @ExistUser
        private Long userId;
    }

    @Getter
    public static class KeepDTO {
        private Long userId;
        private Optional<LocalDateTime> date = Optional.empty();
    }

    @Getter
    public static class MapDTO {
        private Long userId;
        private double latitude;
        private double longitude;
    }

    @Getter
    public static class SearchDTO {
        private Long userId;
        private String searchContent;
    }
}
