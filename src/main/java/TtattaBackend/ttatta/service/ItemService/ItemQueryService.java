package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;

import java.util.List;
import java.util.Optional;

public interface ItemQueryService {
    List<Items> getShopItem(Optional<BodyPart> bodyPart);
    List<OwnedItems> getMyItem();
    List<OwnedItems> getEquippedItem();
}
