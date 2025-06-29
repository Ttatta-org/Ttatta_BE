package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.ChatGPTRequestDTO;
import TtattaBackend.ttatta.web.dto.ChatGPTResponseDTO;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
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


    @Override
    public String summarize(DiaryRequestDTO.SummarizeDTO request) {
        Long userId = request.getUserId();
        Users user = userRepository.findById(userId).orElse(null);
        LocalDate  day = request.getDate();

        LocalDateTime todayStart = day.atStartOfDay();
        LocalDateTime todayEnd = day.atTime(LocalTime.MAX);

        List<Diaries> diaries = diaryRepository.findAllByUserIdAndDate(user, todayStart, todayEnd);
        String prompt  = PromptBuilder.buildPrompt(diaries);

        ChatGPTRequestDTO gptRequest = new ChatGPTRequestDTO(model, prompt);

        ChatGPTResponseDTO responseDTO = restTemplate.postForObject(apiUrl, gptRequest, ChatGPTResponseDTO.class);
        return responseDTO.getChoices().get(0).getMessage().getContent();
    }
}
