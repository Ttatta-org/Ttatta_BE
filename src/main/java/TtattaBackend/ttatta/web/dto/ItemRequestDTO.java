package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CharacterType;
import lombok.Getter;

public class ItemRequestDTO {
    @Getter
    public static class MakeItemDTO {
        private String name;
        private Long cost;
        private String itemImage;
        private CharacterType characterType;
    }
}