package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AdminResponseDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationLogResponseDto {
        private String target;              // 대상
        private String acquisitionPath;      // 취득 경로
        private String provisionalService;  // 제공 서비스
        private String Recipient;           // 제공받는 자
        private LocalDateTime createdAt;    // 이용일시
    }
}
