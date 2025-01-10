package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.service.DiaryService.DiaryPhotoService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryPhotoService diaryPhotoService;

    @Operation(summary = "일기 작성 api(카테고리, 유저 헤더 반영 X)",
            description = """
                    # 사용자의 id, 일기 내용, 작성 날짜, 위도, 경도, 위치이름을 작성할 수 있습니다.
                    저장된 일기의 id와 작성 날짜가 반환됩니다.
                    현재 카테고리와 유저 헤더는 미구현된 상태입니다.
                    """
    )

    @PostMapping("/post")
    public ApiResponse<DiaryResponseDTO.DiaryPostResultDTO> diarySave(@RequestBody @Valid DiaryRequestDTO.DiaryPostDTO request){
        Diaries diaries = diaryPhotoService.save(request);

        return ApiResponse.onSuccess(
                DiaryConverter.toPostResultDTO(diaries)
        );
    }

}
