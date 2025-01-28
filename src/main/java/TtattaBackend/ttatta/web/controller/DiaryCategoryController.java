package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.service.DiaryCategoryService.DiaryCategoryCommandService;
import TtattaBackend.ttatta.service.DiaryCategoryService.DiaryCategoryQueryService;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryCategoryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/categories")
public class DiaryCategoryController {
    private final DiaryCategoryCommandService diaryCategoryCommandService;
    private final DiaryCategoryQueryService diaryCategoryQueryService;
    private final UserRepository userRepository;

    @Operation(
            summary = "카테고리 생성 api",
            description = "새로운 카테고리를 생성할 때 사용하는 api 입니다.\n카테고리 이름과 색상, 사용자의 id 데이터를 넣어주시면 됩니다.")

    @PostMapping("/")
    public ApiResponse<DiaryCategoryResponseDTO.CreateCategoryResultDTO> create(@RequestBody @Valid DiaryCategoryRequestDTO.CreateCategoryDTO request) {
        DiaryCategories diaryCategory = diaryCategoryCommandService.createCategory(request);
        return ApiResponse.onSuccess(DiaryCategoryConverter.toCreateCategoryResultDTO(diaryCategory));
    }

    @Operation(summary = "카테고리 수정 api",
            description = "카테고리를 수정할 a때 사용하는 api 입니다.\n카테고리 이름과 색상, 사용자의 Id 데이터를 넣어주시면 됩니다. 카테고리 Id는 path parameter로 전달받습니다."
    )

    @PatchMapping("/{categoryId}")
    public ApiResponse<DiaryCategoryResponseDTO.ModifyCategoryResultDTO> modify(@PathVariable @ExistDiaryCategory Long categoryId, @RequestBody @Valid DiaryCategoryRequestDTO.ModifyCategoryDTO request) {
       DiaryCategories diaryCategory = diaryCategoryCommandService.modifyCategory(categoryId, request);
       return ApiResponse.onSuccess(DiaryCategoryConverter.toModifyCategoryResultDTO(diaryCategory));
    }



    @Operation(summary = "모든 기록 삭제 api", description =
            "카테고리 및 모든 기록을 삭제할 때 사용하는 api 입니다.\n사용자의 Id 데이터를 넣어주시면 됩니다. 삭제할 카테고리 Id는 path parameter로 전달받습니다."
    )

    @DeleteMapping("/all/{categoryId}")
    public ApiResponse<Object> delete(@PathVariable @ExistDiaryCategory Long categoryId) {
        diaryCategoryCommandService.deleteAllCategory(categoryId);
        return ApiResponse.onSuccess("");
    }



    @Operation(summary = "카테고리 삭제 api", description =
            "카테고리를 삭제할 때 사용하는 api 입니다.\n사용자의 Id 데이터를 넣어주시면 됩니다. 삭제할 카테고리 Id는 path parameter로 전달받습니다."
    )

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Object> deleteAll(@PathVariable @ExistDiaryCategory Long categoryId) {
        diaryCategoryCommandService.deleteCategory(categoryId);
        return ApiResponse.onSuccess(new DiaryCategoryResponseDTO.DeleteCategoryResultDTO(categoryId));
    }



    @Operation(summary = "일기 개수 조회 api", description =
            "모든 카테고리의 각각의 일기 개수와 전체 일기 개수를 알려주는 api입니다.\n사용자의 Id데이터를 넣어주시면 됩니다."
    )

    @GetMapping("/diary-counts")
    public ApiResponse<DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO> getDiaryCount() {
        List<DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO.CategoryDetail> details = diaryCategoryQueryService.getCategoryDetails();
        Integer totalCount = diaryCategoryQueryService.getTotalDiaryCount();

        DiaryCategoryResponseDTO.GetAllCategoryCountResultDTO result = DiaryCategoryConverter.toGetAllCategoryCountResultDTO(details, totalCount);
        return ApiResponse.onSuccess(result);
    }
}