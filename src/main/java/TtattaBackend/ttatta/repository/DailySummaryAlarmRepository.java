package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DailySummaryAlarm;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailySummaryAlarmRepository extends JpaRepository<DailySummaryAlarm, Long> {
    Optional<DailySummaryAlarm> findByUsers(Users getUser);
}
