package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

public class DiaryRequestDTO {
    @Getter
    public static class PostDTO {
        @ExistDiaryCategory
        private Long diaryCategoryId;
        private String content;
        private LocalDateTime date;

        private double latitude;
        private double longitude;
        private String locationName;

    }

    @Getter
    public static class EditDTO {
        private Optional<String> content = Optional.empty();
        private Optional<Long> diaryCategoryId = Optional.empty();
    }

    @Getter
    public static class KeepDTO {
        private Optional<LocalDateTime> date = Optional.empty();
    }

    @Getter
    public static class MapDTO {
        private double latitude;
        private double longitude;
    }

    @Getter
    public static class SearchDTO {
        private String searchContent;
    }
}
