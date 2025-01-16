package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import lombok.Getter;

public class DiaryCategoryRequestDTO {

    @Getter
    public static class CreateCategoryDTO {
        String categoryName;
        CategoryColor categoryColor;
        @ExistUser
        Long userId;
    }

    @Getter
    public static class ModifyCategoryDTO {
        String categoryName;
        CategoryColor categoryColor;
        Long userId;
    }

    @Getter
    public static class DeleteCategoryDTO {
        Long targetCategoryId; // "일상" 카테고리의 id
        Long userId;
    }

    @Getter
    public static class DeleteAllCategoryDTO {
        Long userId;
    }

    @Getter
    public static class GetAllCategoryCountDTO {
        Long userId;
    }

}
