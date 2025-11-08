package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.SummarizeConverter;
import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.service.OpenAiService.SummaryCommandService;
import TtattaBackend.ttatta.service.OpenAiService.SummaryCommandServiceImpl;
import TtattaBackend.ttatta.web.dto.ChatGPTResponseDTO;
import TtattaBackend.ttatta.web.dto.DiaryReSummarizeResponseDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryResponseDTO;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gpt")
public class ChatGPTController {
    private final ChatgptService chatgptService;
    private final SummaryCommandServiceImpl summaryCommandServiceImpl;
    private final SummaryCommandService summaryCommandService;
    private final DiaryRepository diaryRepository;

    public ChatGPTController(ChatgptService chatgptService, SummaryCommandServiceImpl summaryCommandServiceImpl, SummaryCommandService summaryCommandService, DiaryRepository diaryRepository) {
        this.chatgptService = chatgptService;
        this.summaryCommandServiceImpl = summaryCommandServiceImpl;
        this.summaryCommandService = summaryCommandService;
        this.diaryRepository = diaryRepository;
    }

    @Operation(
            summary = "하루 일기 요약 api",
            description = "하루동안의 일기를 요약하는 api 입니다."
    )
    @PostMapping("/summary")
    public ApiResponse<ChatGPTResponseDTO> summarizeDiary(@RequestBody DiarySummaryRequestDTO.SummarizeDTO request) {
        SummaryDiary summary = summaryCommandService.summarize(request);
        return ApiResponse.onSuccess(SummarizeConverter.toSummarizeResponseDTO(summary));
    }

    @Operation(
            summary = "하루 일기 재생성 api",
            description = "hash값으로 일기 추가 / 삭제 / 수정 감지 후 재생성하는 api 입니다."
    )
    @PutMapping("/summary/reSummary")
    public ApiResponse<DiaryReSummarizeResponseDTO> reSummarizeDiary(@RequestBody DiarySummaryRequestDTO.SummarizeDTO request) {
        SummaryDiary summary = summaryCommandService.reSummarize(request);
        return ApiResponse.onSuccess(SummarizeConverter.toReSummarizeResponseDTO(summary));
    }


    @Operation(
            summary = "하루 일기 요약 조회 api",
            description = "날짜를 기준으로 해당 날짜의 요약된 일기를 조회하는 api 입니다."
    )
    @GetMapping("/get/summary")
    public ApiResponse<DiarySummaryResponseDTO.DiarySummaryResultDTO> getSummarizeDiary(@ModelAttribute DiarySummaryRequestDTO.GetDiarySummaryDTO request) {
        return ApiResponse.onSuccess(summaryCommandService.getSummary(request));
    }

}
