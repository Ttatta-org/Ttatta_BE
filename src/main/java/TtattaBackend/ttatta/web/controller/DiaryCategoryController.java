package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.service.DiaryCategoryService.DiaryCategoryCommandService;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class DiaryCategoryController {
    private final DiaryCategoryCommandService diaryCategoryCommandService;

    @Operation(summary = "카테고리 생성 api", description =
            "새로운 카테고리를 생성할 때 사용하는 api 입니다.\n카테고리 이름과 색상, 사용자의 id 데이터를 넣어주시면 됩니다."
    )

    @PostMapping("/")
    public ApiResponse<DiaryCategoryResponseDTO.CreateCategoryResultDTO> create (@RequestBody @Valid DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        DiaryCategories diaryCategory = diaryCategoryCommandService.createCategory(request);
        return ApiResponse.onSuccess(DiaryCategoryConverter.toCreateCategoryResultDTO(diaryCategory));
    }

    @Operation(summary = "카테고리 수정 api", description =
            "카테고리를 수정할 때 사용하는 api 입니다.\n카테고리 이름과 색상, 사용자의 Id 데이터를 넣어주시면 됩니다. 카테고리 Id는 path parameter로 전달받습니다."
    )

    @PatchMapping("/{categoryId}")
    public ApiResponse<DiaryCategoryResponseDTO.ModifyCategoryResultDTO> modify(@PathVariable Long categoryId, @RequestBody @Valid DiaryCategoryRequestDTO.ModifyCategoryDTO request) {
        return null;
    }
}
