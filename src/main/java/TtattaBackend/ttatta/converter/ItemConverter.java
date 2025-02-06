package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

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

    public static ItemResponseDTO.ItemEquipResultDTO toItemEquipDTO(OwnedItems ownedItems) {
        return ItemResponseDTO.ItemEquipResultDTO.builder()
                .itemId(ownedItems.getId())
                .isEquipped(true)
                .build();
    }

    public static ItemResponseDTO.ItemShopDTO toItemShopDTO(Items items) {
        return ItemResponseDTO.ItemShopDTO.builder()
                .itemId(items.getId())
                .name(items.getName())
                .cost(items.getCost())
                .itemImage(items.getItemImg())
                .characterType(items.getCharacterType())
                .bodyPart(items.getBodyPart())
                .build();
    }

    public static ItemResponseDTO.ItemShopListDTO toItemShopListDTO(List<Items> itemsList, Long point) {
        List<ItemResponseDTO.ItemShopDTO> toItemShopListDTO = itemsList.stream()
                .map(ItemConverter::toItemShopDTO).collect(Collectors.toList());

        return ItemResponseDTO.ItemShopListDTO.builder()
                .point(point)
                .itemShopList(toItemShopListDTO)
                .build();
    }
}
