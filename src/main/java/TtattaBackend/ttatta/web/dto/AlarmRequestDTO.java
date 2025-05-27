package TtattaBackend.ttatta.web.dto;

import lombok.Builder;
import lombok.Getter;

public class AlarmRequestDTO {
    @Builder
    @Getter
    public static class GetFcmTokenRequestDTO {
        private String fcmToken;
    }
}
