package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import TtattaBackend.ttatta.web.dto.ChallengeResponeseDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import org.springframework.data.domain.Page;

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

    public static ChallengeResponeseDTO.ChallengeResultDTO toChallengeResultDTO(Challenges challenge) {
        return ChallengeResponeseDTO.ChallengeResultDTO.builder()
                .challengId(challenge.getId())
                .title(challenge.getTitle())
                .isCompleted(challenge.getIsCompleted())
                .build();
    }

    public static ChallengeResponeseDTO.ChallengeListResultDTO toChallengeListResultDTO(List<Challenges> ChallengesList) {
        List<ChallengeResponeseDTO.ChallengeResultDTO> challengeList = ChallengesList.stream()
                .map(ChallengeConverter::toChallengeResultDTO).collect(Collectors.toList());

        return ChallengeResponeseDTO.ChallengeListResultDTO.builder()
                .challengeList(challengeList)
                .build();
    }
}
