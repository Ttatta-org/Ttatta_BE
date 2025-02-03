package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        private String name;
        private Long cost;
        private String itemImage;
        private CharacterType characterType;
    }
}
