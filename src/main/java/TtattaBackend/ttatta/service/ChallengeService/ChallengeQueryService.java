package TtattaBackend.ttatta.service.ChallengeService;

import TtattaBackend.ttatta.domain.Challenges;

import java.util.List;

public interface ChallengeQueryService {
    List<Challenges> getFailChallenges();
}
