package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.domain.Items;

import java.util.List;

public interface ItemQueryService {
    List<Items> getShopItem();
    List<Object[]> getMyItem();
    List<Items> getEquippedItem();

}
