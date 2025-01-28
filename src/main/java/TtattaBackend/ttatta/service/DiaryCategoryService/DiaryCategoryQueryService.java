package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.validation.annotation.ExistUser;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;

import java.util.List;

public interface DiaryCategoryQueryService {
    Integer getTotalDiaryCount();
    List<DiaryCategoryResponseDTO.CategoryDetailDTO> getCategoryDetails();
}