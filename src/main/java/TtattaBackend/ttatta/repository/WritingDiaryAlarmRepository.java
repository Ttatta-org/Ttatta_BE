package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.WrittingDiaryAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingDiaryAlarmRepository extends JpaRepository<WrittingDiaryAlarm, Long> {
    WrittingDiaryAlarm findByUsers(Users users);
}
