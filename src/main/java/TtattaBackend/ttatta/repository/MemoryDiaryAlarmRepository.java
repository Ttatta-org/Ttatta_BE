package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.MemoryDiaryAlarm;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoryDiaryAlarmRepository extends JpaRepository<MemoryDiaryAlarm, Long> {
    Optional<MemoryDiaryAlarm> findByUsers(Users getUser);
}
