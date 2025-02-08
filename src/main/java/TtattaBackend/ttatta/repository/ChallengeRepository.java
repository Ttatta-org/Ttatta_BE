package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenges, Long> {
    @Query("SELECT COUNT(d) FROM Challenges d WHERE DATE(d.createdAt) = DATE(:targetDate)")
    int countByCreatedAtOn(@Param("targetDate") LocalDateTime targetDate);
    List<Challenges> findTop5ByUsersAndIsCompletedFalseOrderByCreatedAtDesc(Users user);
}
