package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

        // 임시 추가

    }

    @Getter
    public static class EditDTO {
        private Optional<String> content = Optional.empty();
        private Optional<Long> diaryCategoryId = Optional.empty();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemindDTO {
        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        private Double latitude;

        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        private Double longitude;

        private Double angleDeg;
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
