package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;

public class ItemConverter {

    public static ItemResponseDTO.ItemBuyResultDTO toItemBuyResultDTO(Users user, Items item) {
        return ItemResponseDTO.ItemBuyResultDTO.builder()
                .isBought(true)
                .itemId(item.getId())
                .point(user.getPoint())
                .cost(item.getCost())
                .build();
    }
}
