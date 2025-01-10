package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.service.DiaryCategoryService.DiaryCategoryCommandService;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class DiaryCategoryController {
    private final DiaryCategoryCommandService diaryCategoryCommandService;

    @PostMapping("/")
    public ApiResponse<DiaryCategoryResponseDTO.CreateCategoryResultDTO> create (@RequestBody @Valid DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        DiaryCategories diaryCategory = diaryCategoryCommandService.createCategory(request);
        return ApiResponse.onSuccess(DiaryCategoryConverter.toCreateCategoryResultDTO(diaryCategory));
    }
}
