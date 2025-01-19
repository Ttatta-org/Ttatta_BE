package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;

public interface DiaryCategoryCommandService {
    DiaryCategories createCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request);
    DiaryCategories modifyCategory(Long categoryId, DiaryCategoryRequestDTO.ModifyCategoryDTO request);
    void checkCategoryColor(String categoryColor);
//    void checkCategory(Long categoryId);
}
