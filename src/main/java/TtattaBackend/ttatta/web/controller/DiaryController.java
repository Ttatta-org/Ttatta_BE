package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.service.DiaryService.DiaryPhotoService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryPhotoService diaryPhotoService;

    @Operation(summary = "일기 작성 api(카테고리, 유저 헤더 반영 X)",
            description = """
                    # 사용자의 id, 일기 내용, 일기 사진(여러장), 작성 날짜, 위도, 경도, 위치이름을 작성할 수 있습니다.
                    저장된 일기의 id와 작성 날짜가 반환됩니다.
                    현재 카테고리와 유저 헤더는 미구현된 상태입니다.
                    """
    )

    @PostMapping(value = "/post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DiaryResponseDTO.DiaryPostResultDTO> diarySave(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                                          @RequestPart @Valid DiaryRequestDTO.DiaryPostDTO request,
                                                                      @RequestPart("image") List<MultipartFile> diaryPhotos){
        Diaries diaries = diaryPhotoService.save(request, diaryPhotos);

        return ApiResponse.onSuccess(
                DiaryConverter.toPostResultDTO(diaries)
        );
    }

}
