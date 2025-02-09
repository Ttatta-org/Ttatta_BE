package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ItemResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemBuyResultDTO {
        private Boolean isBought;
        private Long itemId;
        private Long point;
        private Long cost;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MakeItemResultDTO {
        private Long itemId;
        private String itemUniqueId;
        private String name;
        private Long cost;
        private CharacterType characterType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEquipResultDTO {
        private Long itemId;
        private Boolean isEquipped;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDisrobeResultDTO {
        private Long itemId;
        private Boolean isEquipped;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemShopListDTO {
        private Long point;
        private List<ItemShopDTO> itemShopList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemShopDTO {
        private Long itemId;
        private String itemUniqueId;
        private String name;
        private Long cost;
        private CharacterType characterType;
        private BodyPart bodyPart;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdListDTO {
        List<IdDTO> idList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdDTO {
        private Long itemId;
        private String itemUniqueId;
    }
}
