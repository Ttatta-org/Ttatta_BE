package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.validation.annotation.ExistUser;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;

import java.util.List;

public interface DiaryCategoryQueryService {
    Integer getTotalDiaryCount(@ExistUser Long userId);
    List<DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO.CategoryDetail> getCategoryDetails(@ExistUser Long userId);
}