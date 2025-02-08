package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Items, Long> {
    @Query("SELECT i FROM Items i " +
            "WHERE NOT EXISTS (SELECT o FROM OwnedItems o WHERE o.users = :user AND o.items.id = i.id)")
    List<Items> getShopItem(@Param("user") Users user);

}
