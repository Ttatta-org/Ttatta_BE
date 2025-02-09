package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Items, Long> {
    @Query("SELECT i FROM Items i " +
            "WHERE NOT EXISTS (SELECT o FROM OwnedItems o WHERE o.users = :user AND o.items.id = i.id)")
    List<Items> getShopItem(@Param("user") Users user);

    @Query("SELECT i FROM Items i " +
            "INNER JOIN OwnedItems o ON o.items.id = i.id " +
            "WHERE i.characterType = :characterType AND i.bodyPart = :bodyPart " +
            "AND o.isEquipped = true ")
    Optional<Items> findByBodyPartAndCharacterType(@Param("characterType") CharacterType characterType, @Param("bodyPart") BodyPart bodyPart);

}
