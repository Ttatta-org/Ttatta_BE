package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import lombok.Getter;

public class ItemRequestDTO {
    @Getter
    public static class MakeItemDTO {
        private String itemUniqueId;
        private String name;
        private Long cost;
        private CharacterType characterType;
        private BodyPart bodyPart;
    }
}