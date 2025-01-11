package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CategoryColor;
import lombok.Getter;

public class DiaryCategoryRequestDTO {
    @Getter
    public static class CreateCategoryDTO {
        String categoryName;
        CategoryColor categoryColor;
        Long userId;
    }
}
