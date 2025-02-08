package TtattaBackend.ttatta.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    public static class FailChallengeResultDTO {
        Long challengeId;
        String title;
        String content;
        int term;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailChallengeListResultDTO {
        List<ChallengeResponeseDTO.FailChallengeResultDTO> failChallengeList;
    }
}
