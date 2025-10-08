package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.LocationAccessLogs;
import TtattaBackend.ttatta.domain.LocationLogs;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository

public interface LocationAccessLogRepository  extends JpaRepository<LocationAccessLogs, Long> {
    @Query(
            "SELECT la FROM LocationAccessLogs la " +
                    "WHERE (:from IS NULL OR la.updatedAt >= :from) " +
                    "AND (:to IS NULL OR la.updatedAt < :to) " +
                    "AND (" +
                    "     :keyword IS NULL OR :keyword = '' OR " +
                    "     LOWER(COALESCE(la.adminId, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
                    ")"
    )
    Page<LocationAccessLogs> searchWithKeywordAndDate(
            Pageable pageable,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("keyword") String keyword
    );
}
