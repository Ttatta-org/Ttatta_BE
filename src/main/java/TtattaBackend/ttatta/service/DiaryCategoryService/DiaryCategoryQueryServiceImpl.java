package TtattaBackend.ttatta.service.DiaryCategoryService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryCategoryQueryServiceImpl implements DiaryCategoryQueryService {

    private final DiaryRepository diaryRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;


    @Override
    public Integer getTotalDiaryCount() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId != null) {
            return diaryRepository.countDiariesByUsersId(currentUserId);
        } else {
            throw new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_GET_USER_NOT_FOUND);
        }
    }

    @Override
    public List<DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO.CategoryDetail> getCategoryDetails() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId != null) {
            return diaryCategoryRepository.findCategoriesByUsersId(currentUserId).stream()
                    .map(category -> DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO.CategoryDetail.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getName())
                            .categoryColor(category.getColor())
                            .diaryCount(diaryRepository.countDiariesByDiaryCategoriesId(category.getId()))
                            .build())
                    .collect(Collectors.toList());
        } else {
            throw new ExceptionHandler(ErrorStatus.DIARY_CATEGORY_GET_USER_NOT_FOUND);
        }
    }
}
