package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Challenges;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenges, Long> {
}
