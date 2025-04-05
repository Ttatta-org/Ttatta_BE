package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.service.DiaryService.DiaryCommandService;
import TtattaBackend.ttatta.service.DiaryService.DiaryQueryService;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryCommandService diaryCommandService;
    private final DiaryQueryService diaryQueryService;

    @Operation(summary = "일기 작성",
            description = """
                    카테고리 ID, 일기 내용, 일기 사진, 사진 찍은 날짜, 위도, 경도, 위치이름을 작성해주세요.
                    저장된 일기의 ID와 사진 찍은 날짜가 반환됩니다.
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
                    일기 id를 작성해주세요.
                    해당 일기가 삭제됩니다.
                    """
    )
    @DeleteMapping("/delete/{diaryId}")
    public ApiResponse<Object> deleteDiary(@PathVariable  Long diaryId) {
        diaryCommandService.delete(diaryId);

        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "일기 수정",
            description = """
                    일기 id -> Path Variable \n
                    수정할 일기의 내용, 수정할 카테고리 id, 수정할 사진 파일 -> body 를 작성해주세요.\n
                    ⭐️ 수정 하는 항목만 보내주세요. ⭐️\n
                    ⭐️ body에 들어오는 -> 내용/카테고리 id/파일 중 최소 하나는 필수로 들어와야 합니다. ⭐️
                    """
    )
    @PatchMapping(value = "/edit/{diaryId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DiaryResponseDTO.EditResultDTO> editDiary(@PathVariable Long diaryId,
                                                                 @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                                 @RequestPart @Valid DiaryRequestDTO.EditDTO request,
                                                                 @RequestPart(required = false) MultipartFile editPhoto) {

        Diaries diaries = diaryCommandService.edit(request, diaryId, editPhoto);

        return ApiResponse.onSuccess(
                DiaryConverter.toEditResultDTO(diaries)
        );
    }

    @Operation(summary = "일기 지도(발자국) 전체 조회",
            description = """
                    지도 화면에서 발자국 표현을 위한 일기 전체 조회입니다.\n
                    카테고리 id를 같이 요청(선택)하면 그 카테고리에 포함된 일기의 발자국 표시가 가능합니다.
                    """
    )
    @GetMapping("/footprint")
    public ApiResponse<DiaryResponseDTO.FootprintDiaryListDTO> getFootprintDiaryList(@RequestParam(required = false) Long diaryCategoryId) {
        List<Diaries> diariesList = diaryQueryService.getFootprintDiaryList(diaryCategoryId);

        return ApiResponse.onSuccess(
                DiaryConverter.toFootprintDiaryListDTO(diariesList)
        );
    }


    @Operation(summary = "일기 보관함 화면에서 전체 일기 반환, 캘린더 클릭 시 날짜에 해당하는 일기 반환",
        description = """
                페이징 번호(필수) -> Path variable, 날짜(선택) -> Query String를 작성해주세요.\n
                날짜 없으면 전체, 있으면 해당 날짜의 일기 반환\n
                일기는 최신순으로 반환됩니다.
                """
    )
    @GetMapping("/keep/{requestNum}")
    public ApiResponse<DiaryResponseDTO.KeepDiaryListDTO> getKeepDiaryList(@PathVariable int requestNum,
                                                                           @RequestParam(required = false) LocalDateTime date) {
        Page<Diaries> diaryList = diaryQueryService.getDiaryList(date,requestNum);

        return ApiResponse.onSuccess(
                DiaryConverter.toKeepDiaryListDTO(diaryList)
        );
    }

    @Operation(summary = "일기 지도",
        description = """
                clusterId와 페이징 번호(0번 부터 시작)를 작성해주세요.\n
                일기 지도 화면에서 발자국 컴포넌트 클릭 시 그 위치의 일기가 1개씩 반환됩니다.\n
                카테고리 Id가 같이 요청(선택) 될 시 해당 카테고리의 일기만 1개씩 반환됩니다.
                반환 값에 firstDiary와 lastDiary로 boolean 값을 추가했습니다 ! 해당 범위 내에 있는 일기만 페이징 요청 부탁드립니다 :)
                """
    )
    @GetMapping("/map/{requestNum}")
    public ApiResponse<DiaryResponseDTO.MapResultDTO> getMapDiary (@RequestParam Long clusterId,
                                                                   @RequestParam(required = false) Long diaryCategoryId,
                                                                   @PathVariable int requestNum) {

        Page<Diaries> diaryList = diaryQueryService.getMapDiaryList(clusterId,diaryCategoryId, requestNum);

        return ApiResponse.onSuccess(
                DiaryConverter.toMapDiaryDTO(diaryList)
        );
    }

    @Operation(summary = "일기 검색",
        description = """
                검색 내용(필수), 페이징 번호(필수)를 작성해주세요.\n
                검색한 내용이 들어가 있는 일기가 최신순으로 5개씩 반환됩니다.
                """
    )
    @GetMapping("/search/{requestNum}")
    public ApiResponse<DiaryResponseDTO.SearchDiaryListDTO> getSearchDiary(@PathVariable int requestNum,
                                                                           @RequestParam String searchContent) {

        Page<Diaries> searchDiaryList = diaryQueryService.getSearchDiaryList(searchContent, requestNum);

        return ApiResponse.onSuccess(
                DiaryConverter.toSearchDiaryListDTO(searchDiaryList)
        );
    }

    @Operation(summary = "전체 일기 날짜 조회",
            description = """
                    캘린더에 표시하기 위해 전체 일기의 날짜를 중복없이 조회하는 API입니다.\n
                    header에 access token을 포함시켜 주세요.
                    """
    )
    @GetMapping("/date")
    public ApiResponse<DiaryResponseDTO.DairyDateListResultDTO> getDiariesDate() {
        List<LocalDateTime> diaryDateList = diaryQueryService.getDiaryDateList();

        return ApiResponse.onSuccess(
                DiaryConverter.toDairyDateListResultDTO(diaryDateList)
        );
    }

    @Operation(summary = "Presigned Url",
            description = """
                    파일명을 작성해주세요. \n
                    Presigned Url을 반환됩니다.
                    """
    )
    @GetMapping(value = "/getUrl/{fileName}")
    public ApiResponse<DiaryResponseDTO.PresignedResultDTO> getPresignedUrl(@PathVariable String fileName) {
        List<String> urlList = diaryQueryService.getPresignedUrl(fileName);

        return ApiResponse.onSuccess(
                DiaryConverter.toPresignedUrlResultDTO(urlList)
        );
    }
}
