package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenges, Long> {
    @Query("SELECT COUNT(c) FROM Challenges c WHERE c.users = :user AND DATE(c.createdAt) = DATE(:targetDate)")
    int countByCreatedAtOn(@Param("user") Users user, @Param("targetDate") LocalDateTime targetDate);
    Challenges findByIdAndUsers(Long challengeId, Users user);
    @Query("SELECT c FROM Challenges c WHERE c.users = :user AND DATE(c.createdAt) = :targetDate ORDER BY c.createdAt ASC")
    List<Challenges> findByUsersAndCreatedAtDateOrderByCreatedAtAsc(@Param("user") Users user, @Param("targetDate") LocalDate targetDate);
    Challenges findByUsers(Users getUser);
    boolean existsByUsersAndIsCompletedFalseAndCreatedAtBetween(Users users, LocalDateTime start, LocalDateTime end);
    List<Challenges> findAllByUsersAndCreatedAtBefore(Users getUser, LocalDateTime startOfToday);
}
