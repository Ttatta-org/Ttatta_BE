package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.LocationLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLogs, Long> {
    List<LocationLogs> findAllByOrderByCreatedAtDesc();
}
