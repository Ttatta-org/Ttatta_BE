package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.LocationLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLogs, Long> {
    List<LocationLogs> findAllByOrderByCreatedAtDesc();
    @Query("SELECT l FROM LocationLogs l WHERE l.createdAt BETWEEN :from AND :to "
            + "AND (:keyword IS NULL OR :keyword = '' OR "
            + "LOWER(l.provisionalService) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(l.target) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(l.recipient) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<LocationLogs> searchWithKeywordAndDate(Pageable pageable, LocalDateTime from, LocalDateTime to, String keyword);
}
