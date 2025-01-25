package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.service.DiaryService.DiaryCommandService;
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

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryCommandService diaryCommandService;

    @Operation(summary = "일기 작성",
            description = """
                    사용자의 id, 카테고리 id, 일기 내용, 일기 사진, 사진 찍은 날짜, 위도, 경도, 위치이름을 작성해주세요.
                    저장된 일기의 id와 사진 찍은 날짜가 반환됩니다.
                    """
    )

    @PostMapping(value = "/post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DiaryResponseDTO.PostResultDTO> diarySave(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                                          @RequestPart @Valid DiaryRequestDTO.PostDTO request,
                                                                 @RequestPart("image") MultipartFile diaryPhotos){
        Diaries diaries = diaryCommandService.save(request, diaryPhotos);

        return ApiResponse.onSuccess(
                DiaryConverter.toPostResultDTO(diaries)
        );
    }

    @Operation(summary = "일기 삭제",
            description = """
                    사용자의 id, 일기 id를 작성해주세요.
                    해당 일기가 삭제됩니다.
                    """
    )
    @DeleteMapping("/delete/{diaryId}")
    public ApiResponse<Object> deleteDiary(@PathVariable  Long diaryId,
                                           @RequestBody @Valid DiaryRequestDTO.DeleteDTO request) {
        diaryCommandService.delete(diaryId);

        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "일기 수정",
            description = """
                    사용자 id, 일기 id, 수정할 일기의 내용을 작성해주세요.
                    현재는 내용만 수정 가능합니다.
                    """
    )
    @PatchMapping("/edit/{diaryId}")
    public ApiResponse<DiaryResponseDTO.EditResultDTO> editDiary(@PathVariable Long diaryId,
                                         @RequestBody @Valid DiaryRequestDTO.EditDTO request) {

        Diaries diaries = diaryCommandService.edit(request, diaryId);

        return ApiResponse.onSuccess(
                DiaryConverter.toEditResultDTO(diaries)
        );
    }

    @Operation(summary = "일기 보관함",
        description = """
                사용자 id, 날짜, 페이징 번호를 작성해주세요.
                일기 보관함 화면에서 조회할 수 있는 일기가 최신순으로 반환됩니다.
                """
    )
    @GetMapping("/keep/{requestNum}")
    public ApiResponse<DiaryResponseDTO.KeepResultDTO> getKeepDiary(@PathVariable int requestNum,
                                                                    @RequestBody @Valid DiaryRequestDTO.KeepDTO request) {
        return null;
    }

    @Operation(summary = "일기 지도",
        description = """
                사용자 id, 위도, 경도, 페이징 번호를 작성해주세요.
                일기 지도 화면에서 발자국 컴포넌트 클릭 시 그 위치의 일기가 반환됩니다.
                """
    )
    @GetMapping("/map/{requestNum}")
    public ApiResponse<DiaryResponseDTO.MapResultDTO> getMapDiary (@PathVariable int requestNum,
                                                                   @RequestBody @Valid DiaryRequestDTO.MapDTO request) {
        return null;
    }

    @Operation(summary = "일기 검색",
        description = """
                사용자 id, 검색 내용, 페이징 번호를 작성해주세요.
                검색한 내용이 들어가 있는 일기가 반환됩니다.
                """
    )
    @GetMapping("/search/{requestNum}")
    public ApiResponse<DiaryResponseDTO.SearchResultDTO> getSearchDiary(@PathVariable int requestNum,
                                                                        @RequestBody @Valid DiaryRequestDTO.SearchDTO request) {
        return null;
    }

}
