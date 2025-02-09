package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import TtattaBackend.ttatta.web.dto.ChallengeResponeseDTO;

public class ChallengeConverter {

    public static Challenges toChallenge(ChallengeRequestDTO.CreateChallengeRequestDTO request) {
        return Challenges.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isCompleted(false)
                .build();
    }

    public static ChallengeResponeseDTO.CreateChallengeResultDTO toCreateChallengeResultDTO(Challenges challenge) {
        return ChallengeResponeseDTO.CreateChallengeResultDTO.builder()
                .challengId(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .isCompleted(challenge.getIsCompleted())
                .createdAt(challenge.getCreatedAt())
                .build();
    }
}
