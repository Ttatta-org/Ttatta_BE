package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.SummarizeConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.SummaryDiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryCommandServiceImpl implements SummaryCommandService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SummaryDiaryRepository summaryDiaryRepository;


    @Override
    public String summarize(DiarySummaryRequestDTO.SummarizeDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElse(null);
        LocalDate  day = request.getDate();

        LocalDateTime todayStart = day.atStartOfDay();
        LocalDateTime todayEnd = day.atTime(LocalTime.MAX);

        List<Diaries> diaries = diaryRepository.findAllByUserIdAndDate(user, todayStart, todayEnd);
        String prompt  = PromptBuilder.buildPrompt(diaries);

        ChatGPTRequestDTO gptRequest = new ChatGPTRequestDTO(model, prompt);
        ChatGPTResponseDTO responseDTO = restTemplate.postForObject(apiUrl, gptRequest, ChatGPTResponseDTO.class);

        String content = responseDTO.getChoices().get(0).getMessage().getContent();

        SummaryDiary summaryDiary = SummaryDiary.builder()
                .date(day)
                .content(content)
                .users(user)
                .build();

        summaryDiaryRepository.save(summaryDiary);
        return content;
    }

    @Override
    public DiarySummaryResponseDTO.DiarySummaryResultDTO getSummary(DiarySummaryRequestDTO.GetDiarySummaryDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        LocalDate day = request.getDate();

        SummaryDiary result = summaryDiaryRepository.findByDateAndUsers(day, user)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.SUMMARY_DIARY_NOT_FOUND));
        return SummarizeConverter.toGetDiarySummaryResponseDTO(result);
    }
}
