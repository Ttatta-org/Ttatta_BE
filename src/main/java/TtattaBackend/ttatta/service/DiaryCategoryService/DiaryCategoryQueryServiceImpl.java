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
        return diaryRepository.countDiariesByUsersId(currentUserId);
    }

    @Override
    public List<DiaryCategoryResponseDTO.CategoryDetailDTO> getCategoryDetails() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return diaryCategoryRepository.findCategoriesByUsersId(currentUserId).stream()
                .map(category -> DiaryCategoryResponseDTO.CategoryDetailDTO.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .categoryColor(category.getColor())
                        .diaryCount(diaryRepository.countDiariesByDiaryCategoriesId(category.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
