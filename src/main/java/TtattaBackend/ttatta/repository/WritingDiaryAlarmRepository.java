package TtattaBackend.ttatta.repository;

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
public interface WritingDiaryAlarmRepository extends JpaRepository<WrittingDiaryAlarm, Long> {
    Optional<WrittingDiaryAlarm> findByUsers(Users users);
    List<WrittingDiaryAlarm> findAllByIsActive(IsActive isActive);
    @Query("SELECT a FROM WrittingDiaryAlarm a JOIN FETCH a.users WHERE a.isActive = :isActive")
    List<WrittingDiaryAlarm> findAllByIsActiveUsingFetchJoin(@Param("isActive") IsActive isActive);
}
