package TtattaBackend.ttatta.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AlarmRequestDTO {
    @Builder
    @Getter
    public static class GetFcmTokenRequestDTO {
        private String fcmToken;
    }
}
