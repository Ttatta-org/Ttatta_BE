package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryCategoryCommandServiceImpl implements DiaryCategoryCommandService {
    private final DiaryCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;

    @Override
    @Transactional
    public DiaryCategories createCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        Users user = userRepository.findById(request.getUserId()).orElseThrow (() -> new RuntimeException("User not found"));
        DiaryCategories newDiaryCategory = DiaryCategoryConverter.toDiaryCategory(request);
        newDiaryCategory.setUsers(user);
        return categoryRepository.save(newDiaryCategory);
    }

    @Override
    @Transactional
    public DiaryCategories modifyCategory(Long categoryId, DiaryCategoryRequestDTO.ModifyCategoryDTO request) {
        DiaryCategories diaryCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));

        request.getCategoryName().ifPresent(diaryCategory::modifyCategoryName);
        request.getCategoryColor().ifPresent(categoryColor -> diaryCategory.modifyCategoryColor(CategoryColor.fromString(categoryColor)));

        return diaryCategoryRepository.save(diaryCategory);
    }
}
