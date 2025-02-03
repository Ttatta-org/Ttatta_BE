package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;

public class ItemConverter {

    public static ItemResponseDTO.MakeItemResultDTO toMakeItemResultDTO(Items item) {
        return ItemResponseDTO.MakeItemResultDTO.builder()
                .itemId(item.getId())
                .name(item.getName())
                .cost(item.getCost())
                .itemImage(item.getItemImg())
                .characterType(item.getCharacterType())
                .build();
    }

    public static Items toItem(ItemRequestDTO.MakeItemDTO request) {
        CharacterType characterType = request.getCharacterType();

        return Items.builder()
                .name(request.getName())
                .cost(request.getCost())
                .itemImg(request.getItemImage())
                .characterType(characterType)
                .build();
    }

    public static ItemResponseDTO.ItemBuyResultDTO toItemBuyResultDTO(Users user, Items item) {
        return ItemResponseDTO.ItemBuyResultDTO.builder()
                .isBought(true)
                .itemId(item.getId())
                .point(user.getPoint())
                .cost(item.getCost())
                .build();
    }
}
