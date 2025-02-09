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
                .itemUniqueId(item.getItemUniqueId())
                .name(item.getName())
                .cost(item.getCost())
                .characterType(item.getCharacterType())
                .build();
    }

    public static Items toItem(ItemRequestDTO.MakeItemDTO request) {
        CharacterType characterType = request.getCharacterType();

        return Items.builder()
                .itemUniqueId(request.getItemUniqueId())
                .name(request.getName())
                .cost(request.getCost())
                .characterType(characterType)
                .bodyPart(request.getBodyPart())
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

    public static ItemResponseDTO.ItemDisrobeResultDTO toItemDisrobeDTO(OwnedItems ownedItems) {
        return ItemResponseDTO.ItemDisrobeResultDTO.builder()
                .itemId(ownedItems.getId())
                .isEquipped(ownedItems.getIsEquipped())
                .build();
    }

    public static ItemResponseDTO.ItemShopDTO toItemShopDTO(Items items) {
        return ItemResponseDTO.ItemShopDTO.builder()
                .itemId(items.getId())
                .itemUniqueId(items.getItemUniqueId())
                .name(items.getName())
                .cost(items.getCost())
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

    public static ItemResponseDTO.ItemMyItemDTO toItemMyItemDTO(Items items, Boolean isEquipped) {
        return ItemResponseDTO.ItemMyItemDTO.builder()
                .itemId(items.getId())
                .itemUniqueId(items.getItemUniqueId())
                .name(items.getName())
                .isEquipped(isEquipped)
                .characterType(items.getCharacterType())
                .bodyPart(items.getBodyPart())
                .build();
    }

    public static ItemResponseDTO.ItemMyItemListDTO toItemMyItemListDTO(List<Object[]> itemsList, Long point) {
        List<ItemResponseDTO.ItemMyItemDTO> toItemMyItemListDTO = itemsList.stream()
                .map(itemArray -> {
                    Items item = (Items) itemArray[0];
                    Boolean isEquipped = (Boolean) itemArray[1];
                    return ItemConverter.toItemMyItemDTO(item, isEquipped);
                }).collect(Collectors.toList());

        return ItemResponseDTO.ItemMyItemListDTO.builder()
                .point(point)
                .myItemList(toItemMyItemListDTO)
                .build();
    }

    public static ItemResponseDTO.IdDTO toIdDTO(Items items) {
        return ItemResponseDTO.IdDTO.builder()
                .itemId(items.getId())
                .itemUniqueId(items.getItemUniqueId())
                .build();
    }

    public static ItemResponseDTO.IdListDTO toIdListDTO(List<Items> itemsList) {
        List<ItemResponseDTO.IdDTO> toIdListDTO = itemsList.stream()
                .map(ItemConverter::toIdDTO).collect(Collectors.toList());

        return ItemResponseDTO.IdListDTO.builder()
                .idList(toIdListDTO)
                .build();
    }
}
