package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import lombok.Getter;

import java.util.Optional;

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
        private Optional<String> categoryName = Optional.empty();
        private Optional<CategoryColor> categoryColor = Optional.empty();
        private Optional<Long> userId = Optional.empty();
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
