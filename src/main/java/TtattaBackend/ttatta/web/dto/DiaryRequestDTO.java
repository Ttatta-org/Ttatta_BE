package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        private String objectKey;

    }

    @Getter
    public static class EditDTO {
        private Optional<String> content = Optional.empty();
        private Optional<Long> diaryCategoryId = Optional.empty();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ViewOnMapDTO {
        private double lng1;
        private double lat1;
        private double lng2;
        private double lat2;
        private double lng3;
        private double lat3;
        private double lng4;
        private double lat4;
    }

}
