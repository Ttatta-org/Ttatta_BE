package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnedItemRepository extends JpaRepository<OwnedItems, Long>{
    Optional<OwnedItems> findByItems(Items items);
}
