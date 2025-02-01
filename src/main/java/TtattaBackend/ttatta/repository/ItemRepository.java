package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Items, Long> {
}
