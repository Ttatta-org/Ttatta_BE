package TtattaBackend.ttatta.service.ChallengeService;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;

public interface ChallengeCommandService {
    Challenges createChallenge(ChallengeRequestDTO.CreateChallengeRequestDTO request);
    Challenges successChallenge(Long challengeId);
}
