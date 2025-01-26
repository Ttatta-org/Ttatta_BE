package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;

public interface DiaryCategoryCommandService {
    DiaryCategories createCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request);
    DiaryCategories modifyCategory(Long categoryId, DiaryCategoryRequestDTO.ModifyCategoryDTO request);
    void deleteAllCategory(Long categoryId);
    void deleteCategory(Long categoryId);
}
