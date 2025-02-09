package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChallengeResponeseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChallengeResultDTO {
        Long challengId;
        String title;
        String content;
        Boolean isCompleted;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessChallengeResultDTO {
        Long challengeId;
        Boolean isCompleted;
        LocalDateTime updatedAt;
    }
}
