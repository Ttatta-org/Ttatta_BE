package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;

import java.util.List;

public interface ItemQueryService {
    List<Items> getShopItem();
    List<OwnedItems> getMyItem();
    List<Items> getEquippedItem();

}
