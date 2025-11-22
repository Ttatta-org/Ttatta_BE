package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import TtattaBackend.ttatta.web.dto.ChallengeResponeseDTO;

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
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .isCompleted(challenge.getIsCompleted())
                .createdAt(challenge.getCreatedAt())
                .build();
    }

    public static ChallengeResponeseDTO.SuccessChallengeResultDTO toSuccessChallengeResultDTO(Challenges challenge) {
        return ChallengeResponeseDTO.SuccessChallengeResultDTO.builder()
                .challengeId(challenge.getId())
                .isCompleted(challenge.getIsCompleted())
                .updatedAt(challenge.getUpdatedAt())
                .build();
    }
  
    public static ChallengeResponeseDTO.ChallengeResultDTO toChallengeResultDTO(Challenges challenge) {
        return ChallengeResponeseDTO.ChallengeResultDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
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
  
    public static ChallengeResponeseDTO.GetAllPastChallengeResultDTO toGetAllPastChallengeResultDTO(Challenges challenge) {
        return ChallengeResponeseDTO.GetAllPastChallengeResultDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .isCompleted(challenge.getIsCompleted())
                .build();
    }

    public static ChallengeResponeseDTO.GetAllPastChallengeListResultDTO toGetAllPastChallengeListResultDTO(List<Challenges> challengeList) {
        List<ChallengeResponeseDTO.GetAllPastChallengeResultDTO> challengeListDTO = challengeList.stream()
                .map(challenge -> ChallengeConverter.toGetAllPastChallengeResultDTO(challenge)).collect(Collectors.toList());

        return ChallengeResponeseDTO.GetAllPastChallengeListResultDTO.builder()
                .getAllPastChallengeResultDTOList(challengeListDTO)
                .build();
    }
}
