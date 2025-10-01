package TtattaBackend.ttatta.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SuperAdminResponseDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminLogResponseDto {
        private String handler;         // 취급자
        private String requester;       // 요청자
        private String purpose;         // 목적
        private LocalDateTime createdAt; // 일시
    }
}
