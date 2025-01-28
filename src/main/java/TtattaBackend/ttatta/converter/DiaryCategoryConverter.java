package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;

import java.util.List;


public class DiaryCategoryConverter {

    public static DiaryCategoryResponseDTO.CreateCategoryResultDTO toCreateCategoryResultDTO(DiaryCategories diaryCategory) {
        return DiaryCategoryResponseDTO.CreateCategoryResultDTO.builder()
                .categoryId(diaryCategory.getId())
                .categoryColor(diaryCategory.getColor())
                .categoryName(diaryCategory.getName())
                .createdAt(diaryCategory.getCreatedAt())
                .build();
    }

    public static DiaryCategories toDiaryCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        CategoryColor diaryCategoryColor = null;

        switch (request.getCategoryColor()) {
            case RED:
                diaryCategoryColor = CategoryColor.RED;
                break;
            case ORANGE:
                diaryCategoryColor = CategoryColor.ORANGE;
                break;
            case YELLOW:
                diaryCategoryColor = CategoryColor.YELLOW;
                break;
            case GREEN:
                diaryCategoryColor = CategoryColor.GREEN;
                break;
            case SKYBLUE:
                diaryCategoryColor = CategoryColor.SKYBLUE;
                break;
            case BLUE:
                diaryCategoryColor = CategoryColor.BLUE;
                break;
            case INDIGO:
                diaryCategoryColor = CategoryColor.INDIGO;
                break;
            case VIOLET:
                diaryCategoryColor = CategoryColor.VIOLET;
                break;
            case BROWN:
                diaryCategoryColor = CategoryColor.BROWN;
                break;
            case WHITE:
                diaryCategoryColor = CategoryColor.WHITE;
                break;
            case PINK:
                diaryCategoryColor = CategoryColor.PINK;
                break;
            case BLACK:
                diaryCategoryColor = CategoryColor.BLACK;
                break;
        }


        return DiaryCategories.builder()
                .name(request.getCategoryName())
                .color(diaryCategoryColor)
                .build();
    }

    public static DiaryCategoryResponseDTO.ModifyCategoryResultDTO toModifyCategoryResultDTO(DiaryCategories diaryCategory) {
        return DiaryCategoryResponseDTO.ModifyCategoryResultDTO.builder()
                .categoryId(diaryCategory.getId())
                .categoryColor(diaryCategory.getColor())
                .categoryName(diaryCategory.getName())
                .updatedAt(diaryCategory.getUpdatedAt())
                .build();
    }



    public static DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO toGetAllCategoryCountResultDTO(
            List<DiaryCategoryResponseDTO.CategoryDetailDTO> categoryDetails,
            Integer totalDiaryCount) {
        return DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO.builder()
                .categoryDetails(categoryDetails)
                .totalDiaryCount(totalDiaryCount)
                .build();
    }
}