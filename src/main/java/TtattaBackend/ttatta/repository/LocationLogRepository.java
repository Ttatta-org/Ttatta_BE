package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.LocationLogs;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLogs, Long> {
    @Query(
            "SELECT l FROM LocationLogs l " +
                    "WHERE (:from IS NULL OR l.createdAt >= :from) " +
                    "AND (:to IS NULL OR l.createdAt < :to) " +
                    "AND (" +
                    "     :keyword IS NULL OR :keyword = '' OR " +
                    "     LOWER(COALESCE(l.provisionalService, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "     LOWER(COALESCE(l.target, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "     LOWER(COALESCE(l.recipient, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
                    ")"
    )
    Page<LocationLogs> searchWithKeywordAndDate(
            Pageable pageable,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("keyword") String keyword
    );
}
