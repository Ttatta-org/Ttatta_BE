package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.ChallengeRemindAlarm;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRemindAlarmRepository extends JpaRepository<ChallengeRemindAlarm, Long> {
    ChallengeRemindAlarm findByUsers(Users getUser);
}
