package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.ChallengeRemindAlarm;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.WrittingDiaryAlarm;
import TtattaBackend.ttatta.domain.enums.IsActive;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRemindAlarmRepository extends JpaRepository<ChallengeRemindAlarm, Long> {
    Optional<ChallengeRemindAlarm> findByUsers(Users getUser);
    @Query("SELECT a FROM ChallengeRemindAlarm a JOIN FETCH a.users WHERE a.isActive = :isActive")
    List<ChallengeRemindAlarm> findAllByIsActiveUsingFetchJoin(@Param("isActive")IsActive isActive);
}
