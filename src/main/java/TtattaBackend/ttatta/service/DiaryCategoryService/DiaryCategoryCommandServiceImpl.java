package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.apiPayload.exception.handler.TempHandler;
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

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DiaryCategoryCommandServiceImpl implements DiaryCategoryCommandService {
    private final DiaryCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;


    @Override
    @Transactional
    public DiaryCategories createCategory(DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        Users user = userRepository.findById(request.getUserId()).orElseThrow ();
        DiaryCategories newDiaryCategory = DiaryCategoryConverter.toDiaryCategory(request);
        newDiaryCategory.setUsers(user);
        return categoryRepository.save(newDiaryCategory);
    }

    @Override
    @Transactional
    public DiaryCategories modifyCategory(Long categoryId, DiaryCategoryRequestDTO.ModifyCategoryDTO request) {
        DiaryCategories diaryCategory = categoryRepository.findById(categoryId)
                .orElseThrow();

        request.getCategoryName().ifPresent(diaryCategory::modifyCategoryName);
        request.getCategoryColor().ifPresent(categoryColor -> {
            verifyCategoryColor(categoryColor);
            diaryCategory.modifyCategoryColor(CategoryColor.valueOf(categoryColor.toUpperCase()));
        });

        return diaryCategoryRepository.save(diaryCategory);
    }

    @Override
    public void deleteAllCategory(Long categoryId, DiaryCategoryRequestDTO.DeleteCategoryDTO request) {
        DiaryCategories diaryCategory = diaryCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_NOT_FOUND));

        diaryCategoryRepository.delete(diaryCategory);
    }

    private void verifyCategoryColor(String categoryColor) {
        boolean isValid = Arrays.stream(CategoryColor.values())
                .anyMatch(color -> color.name().equalsIgnoreCase(categoryColor));
        if (!isValid) {
            throw new TempHandler(ErrorStatus.DIARY_CATEGORY_COLOR_NOT_FOUND);
        }
    }
}
