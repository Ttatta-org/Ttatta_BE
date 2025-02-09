package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenges, Long> {
    @Query("SELECT COUNT(d) FROM Challenges d WHERE DATE(d.createdAt) = DATE(:targetDate)")
    int countByCreatedAtOn(@Param("targetDate") LocalDateTime targetDate);
    List<Challenges> findTop5ByUsersAndIsCompletedFalseOrderByCreatedAtDesc(Users user);
    @Query("SELECT c FROM Challenges c " +
            "WHERE c.users = :user " +
            "AND c.isCompleted = false " +
            "AND DATE(c.createdAt) <> CURRENT_DATE " +
            "ORDER BY c.createdAt DESC " +
            "LIMIT 5")
    List<Challenges> findTop5ByUserAndIsCompletedFalseExcludeTodayOrderByCreatedAtDesc(@Param("user") Users user);
}
