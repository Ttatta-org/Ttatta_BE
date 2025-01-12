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

    @Operation(summary = "일기 작성",
            description = """
                    사용자의 id, 카테고리 id, 일기 내용, 일기 사진, 사진 찍은 날짜, 위도, 경도, 위치이름을 작성해주세요.
                    저장된 일기의 id와 사진 찍은 날짜가 반환됩니다.
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
