package TtattaBackend.ttatta.web.dto;

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
}
