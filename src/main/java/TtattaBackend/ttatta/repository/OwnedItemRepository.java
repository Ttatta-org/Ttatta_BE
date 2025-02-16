package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OwnedItemRepository extends JpaRepository<OwnedItems, Long>{
    Optional<OwnedItems> findByItems(Items items);
    Optional<OwnedItems> findByUsersAndItems(Users users, Items items);

    List<OwnedItems> findByUsers(Users user);
    List<OwnedItems> findByUsersAndIsEquipped(Users user, Boolean isEquipped);
}
