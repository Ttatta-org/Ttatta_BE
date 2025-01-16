package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, Long> {
    Uuid findByUuid(String uuid);
}
