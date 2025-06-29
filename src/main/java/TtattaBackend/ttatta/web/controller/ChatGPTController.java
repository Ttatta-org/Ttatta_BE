package TtattaBackend.ttatta.web.controller;


import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.SummarizeConverter;
import TtattaBackend.ttatta.service.OpenAiService.SummaryCommandService;
import TtattaBackend.ttatta.service.OpenAiService.SummaryCommandServiceImpl;
import TtattaBackend.ttatta.web.dto.ChatGPTRequestDTO;
import TtattaBackend.ttatta.web.dto.ChatGPTResponseDTO;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gpt")
public class ChatGPTController {
    private final ChatgptService chatgptService;
    private final SummaryCommandServiceImpl summaryCommandServiceImpl;
    private final SummaryCommandService summaryCommandService;

    public ChatGPTController(ChatgptService chatgptService, SummaryCommandServiceImpl summaryCommandServiceImpl, SummaryCommandService summaryCommandService) {
        this.chatgptService = chatgptService;
        this.summaryCommandServiceImpl = summaryCommandServiceImpl;
        this.summaryCommandService = summaryCommandService;
    }

    @Operation(
            summary = "하루 일기 요약 api",
            description = "하루동안의 일기를 요약하는 api 입니다."
    )
    @PostMapping("/chat")
    public ApiResponse<ChatGPTResponseDTO> summarizeDiary(@RequestBody DiaryRequestDTO.SummarizeDTO request) {
        String summary = summaryCommandService.summarize(request);
        return ApiResponse.onSuccess(SummarizeConverter.toSummarizeResponseDTO(summary));
    }
}
