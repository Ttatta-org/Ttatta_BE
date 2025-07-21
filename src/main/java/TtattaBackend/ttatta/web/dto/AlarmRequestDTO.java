package TtattaBackend.ttatta.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

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
        private LocalTime alarmTime;
    }
}
