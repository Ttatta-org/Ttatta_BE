package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DailySummaryAlarm;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.WrittingDiaryAlarm;
import TtattaBackend.ttatta.domain.enums.IsActive;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryAlarmRepository extends JpaRepository<DailySummaryAlarm, Long> {
    Optional<DailySummaryAlarm> findByUsers(Users getUser);
    @Query("SELECT a FROM DailySummaryAlarm a JOIN FETCH a.users WHERE a.isActive = :isActive")
    List<DailySummaryAlarm> findAllByIsActiveUsingFetchJoin(@Param("isActive")IsActive isActive);

    @Query("SELECT d FROM DailySummaryAlarm d WHERE d.alarmTime = :alarmTime")
    List<DailySummaryAlarm> findByAlarmTime(@Param("alarmTime") LocalTime alarmTime);
}
