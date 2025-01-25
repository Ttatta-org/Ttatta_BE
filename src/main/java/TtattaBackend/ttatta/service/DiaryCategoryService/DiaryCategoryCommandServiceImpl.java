package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.apiPayload.exception.handler.TempHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
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
    private final DiaryRepository diaryRepository;


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
    public void deleteAllCategory(Long categoryId) {
        Long userId = SecurityUtil.getCurrentUserId();
        DiaryCategories diaryCategory = diaryCategoryRepository.findById(categoryId)
                .orElseThrow();
        if (diaryCategory.getUsers().getId().equals(userId)) {
            diaryCategoryRepository.delete(diaryCategory);
        } else {
            throw new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_DELETE_USER_NOT_FOUND); // 바꿀 필요 있음
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Long userId = SecurityUtil.getCurrentUserId();

        DiaryCategories diaryCategory = diaryCategoryRepository.findById(categoryId)
                .orElseThrow();

        if(diaryCategory.getUsers().getId().equals(userId)) {
            DiaryCategories diaryCategoryToDelete = diaryCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_NOT_FOUND));

            DiaryCategories defaultCategory = diaryCategoryRepository.findByName("일상")
                    .orElseThrow(() -> new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_DEFAULT_NOT_FOUND));

            diaryRepository.updateCategoryForDiaries(diaryCategoryToDelete.getId(), defaultCategory.getId());
            diaryCategoryRepository.delete(diaryCategoryToDelete);
        } else {
            throw new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_DELETE_USER_NOT_FOUND);
        }
    }

    private void verifyCategoryColor(String categoryColor) {
        boolean isValid = Arrays.stream(CategoryColor.values())
                .anyMatch(color -> color.name().equalsIgnoreCase(categoryColor));
        if (!isValid) {
            throw new TempHandler(ErrorStatus.DIARY_CATEGORY_COLOR_NOT_FOUND);
        }
    }
}
