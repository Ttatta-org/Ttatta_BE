package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import TtattaBackend.ttatta.web.dto.ChallengeResponeseDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ChallengeResponeseDTO.FailChallengeResultDTO toFailChallengeResultDTO(Challenges challenge, int term) {
        return ChallengeResponeseDTO.FailChallengeResultDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .term(term)
                .build();
    }

    public static ChallengeResponeseDTO.FailChallengeListResultDTO toFailChallengeListResultDTO(List<Challenges> challengeList) {
        List<ChallengeResponeseDTO.FailChallengeResultDTO> challengeListDTO = challengeList.stream()
                .map(challenge -> ChallengeConverter.toFailChallengeResultDTO(challenge, (int) ChronoUnit.DAYS.between(challenge.getCreatedAt(), LocalDateTime.now()))).collect(Collectors.toList());

        return ChallengeResponeseDTO.FailChallengeListResultDTO.builder()
                .failChallengeList(challengeListDTO)
                .build();
    }
}
