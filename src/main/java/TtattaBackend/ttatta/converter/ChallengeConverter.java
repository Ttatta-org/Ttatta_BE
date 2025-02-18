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
