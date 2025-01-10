package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DiaryCategoryConverter {
    public static DiaryCategoryResponseDTO.CreateCategoryResultDTO toCreateCategoryResultDTO(DiaryCategories diaryCategory) {
        return DiaryCategoryResponseDTO.CreateCategoryResultDTO.builder()
                .categoryId(diaryCategory.getId())
                .categoryColor(diaryCategory.getColor())
                .categoryName(diaryCategory.getName())
                .createdAt(LocalDateTime.now())
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
        }


        return DiaryCategories.builder()
                .name(request.getCategoryName())
                .color(diaryCategoryColor)
                .date(LocalDateTime.now())
                .diariesList(new ArrayList<>())
                .build();
    }
}
