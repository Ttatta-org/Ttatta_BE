package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategoryColor;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import lombok.Getter;

import java.util.Optional;

public class DiaryCategoryRequestDTO {

    @Getter
    public static class CreateCategoryDTO {
        String categoryName;
        CategoryColor categoryColor;
//        @ExistUser
//        Long userId;
    }

    @Getter
    public static class ModifyCategoryDTO {
        private Optional<String> categoryName = Optional.empty();
        @ExistDiaryCategoryColor
        private Optional<String> categoryColor = Optional.empty();
        @ExistUser
        private Long userId;
    }

}
