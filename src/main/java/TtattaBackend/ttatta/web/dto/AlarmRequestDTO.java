package TtattaBackend.ttatta.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AlarmRequestDTO {
    @Builder
    @Getter
    public static class GetFcmTokenRequestDTO {
        private String fcmToken;
    }

    @Builder
    @Getter
    public static class UpdateWritingAlarmRequestDTO {
        @Schema(type = "string", pattern = "HH:mm:ss", example = "09:30:00", description = "시:분:초 형식")
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime alarmTime;
    }
}
