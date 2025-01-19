package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
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
    public ApiResponse<DiaryCategoryResponseDTO.CreateCategoryResultDTO> create(@RequestBody @Valid DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        DiaryCategories diaryCategory = diaryCategoryCommandService.createCategory(request);
        return ApiResponse.onSuccess(DiaryCategoryConverter.toCreateCategoryResultDTO(diaryCategory));
    }



    @Operation(summary = "카테고리 수정 api", description =
            "카테고리를 수정할 때 사용하는 api 입니다.\n카테고리 이름과 색상, 사용자의 Id 데이터를 넣어주시면 됩니다. 카테고리 Id는 path parameter로 전달받습니다."
    )

    @PatchMapping("/{categoryId}")
    public ApiResponse<DiaryCategoryResponseDTO.ModifyCategoryResultDTO> modify(@PathVariable Long categoryId, @RequestBody @Valid DiaryCategoryRequestDTO.ModifyCategoryDTO request) {
       DiaryCategories diaryCategory = diaryCategoryCommandService.modifyCategory(categoryId, request);
       return ApiResponse.onSuccess(DiaryCategoryConverter.toModifyCategoryResultDTO(diaryCategory));
    }

    @PatchMapping("/exception")
    public ApiResponse<DiaryCategoryResponseDTO.DiaryCategoryExceptionDTO> exceptionAPI (@RequestParam Long categoryId) {
        return ApiResponse.onSuccess(DiaryCategoryConverter.toDiaryCategoryExceptionDTO(categoryId));
    }

    @PatchMapping("/color/exception")
    public ApiResponse<DiaryCategoryResponseDTO.DiaryCategoryColorExceptionDTO> colorExceptionAPI (@RequestParam String categoryColor) {
        diaryCategoryCommandService.checkCategoryColor(categoryColor);
        return ApiResponse.onSuccess(DiaryCategoryConverter.toDiaryCategoryColorExceptionDTO(categoryColor));
    }


    @Operation(summary = "카테고리 삭제 api", description =
            "카테고리를 삭제할 때 사용하는 api 입니다.\n일상 카테고리의 Id와 사용자의 Id 데이터를 넣어주시면 됩니다. 삭제할 카테고리 Id는 path parameter로 전달받습니다."
    )

    @DeleteMapping("/{categoryId}")
    public ApiResponse<DiaryCategoryResponseDTO.DeleteCategoryResultDTO> delete(@PathVariable Long categoryId, @RequestBody @Valid DiaryCategoryRequestDTO.DeleteCategoryDTO request) {
        return null;
    }



    @Operation(summary = "모든 기록 삭제 api", description =
            "카테고리 및 모든 기록을 삭제할 때 사용하는 api 입니다.\n사용자의 Id 데이터를 넣어주시면 됩니다. 삭제할 카테고리 Id는 path parameter로 전달받습니다."
    )

    @DeleteMapping("/all/{categoryId}")
    public ApiResponse<DiaryCategoryResponseDTO.DeleteAllCategoryResultDTO> deleteAll(@PathVariable Long categoryId, @RequestBody @Valid DiaryCategoryRequestDTO.DeleteAllCategoryDTO request) {
        return null;
    }



    @Operation(summary = "일기 개수 조회 api", description =
            "모든 카테고리의 각각의 일기 개수와 전체 일기 개수를 알려주는 api입니다.\n사용자의 Id데이터를 넣어주시면 됩니다."
    )
    @GetMapping("/diary-counts")
    public ApiResponse<DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO> count(@RequestBody @Valid DiaryCategoryRequestDTO.GetAllCategoryCountDTO request) {
        return null;
    }
}
