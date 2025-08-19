package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SummaryDiaryRepository extends JpaRepository<SummaryDiary, Long> {
    Optional<SummaryDiary> findByDateAndUsers(LocalDate date, Users users);
}
