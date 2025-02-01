package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedItemRepository extends JpaRepository<OwnedItems, Long>{
}
