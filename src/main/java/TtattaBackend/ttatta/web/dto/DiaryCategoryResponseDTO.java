package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.CategoryColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class DiaryCategoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCategoryResultDTO {
        Long categoryId;
        String categoryName;
        CategoryColor categoryColor;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModifyCategoryResultDTO {
        Long categoryId;
        String categoryName;
        CategoryColor categoryColor;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteCategoryResultDTO {
        Long categoryId; // 삭제한 카테고리 Id
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteAllCategoryResultDTO {
        Long categoryId; // 삭제한 카테고리 Id
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllCategoryCountResultDTO {
        List<CategoryDetail> categoryDetails;
        Integer totalDiaryCount;
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CategoryDetail {
            Long categoryId;
            String categoryName;
            Integer diaryCount;
            CategoryColor categoryColor;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryCategoryExceptionDTO{
        Long categoryId;
    }
}
