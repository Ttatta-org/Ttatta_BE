package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryCategoryCommandServiceImpl implements DiaryCategoryCommandService {
    private final DiaryCategoryRepository categoryRepository;

    @Override
    @Transactional
    public DiaryCategories createCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request) {

        DiaryCategories newDiaryCategory = DiaryCategoryConverter.toDiaryCategory(request);
        return categoryRepository.save(newDiaryCategory);
    }
}
