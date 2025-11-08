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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
    public SummaryDiary summarize(DiarySummaryRequestDTO.SummarizeDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElse(null);
        LocalDate day = request.getDate();

        LocalDateTime todayStart = day.atStartOfDay();
        LocalDateTime todayEnd = day.atTime(LocalTime.MAX);

        List<Diaries> diaries = diaryRepository.findAllByUserIdAndDate(user, todayStart, todayEnd);
        String prompt  = PromptBuilder.buildPrompt(diaries);

        ChatGPTRequestDTO gptRequest = new ChatGPTRequestDTO(model, prompt);
        ChatGPTResponseDTO responseDTO = restTemplate.postForObject(apiUrl, gptRequest, ChatGPTResponseDTO.class);

        String content = responseDTO.getChoices().get(0).getMessage().getContent();

        String rawKey = diaries.stream()
                .map(d -> d.getId() + ":" + d.getUpdatedAt().toString())
                .sorted()
                .collect(Collectors.joining(","));

        String diaryKeyHash = generateSHA256(rawKey);

        SummaryDiary summaryDiary = SummaryDiary.builder()
                .date(day)
                .content(content)
                .users(user)
                .diaryKeyHash(diaryKeyHash)
                .build();

        summaryDiaryRepository.save(summaryDiary);
        return summaryDiary;
    }

    @Override
    public SummaryDiary reSummarize(DiarySummaryRequestDTO.SummarizeDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId).orElse(null);
        LocalDate day = request.getDate();

        LocalDateTime todayStart = day.atStartOfDay();
        LocalDateTime todayEnd = day.atTime(LocalTime.MAX);


        SummaryDiary originalSummaryDiary = summaryDiaryRepository.findByDateAndUsers(day, user)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.SUMMARY_DIARY_NOT_FOUND));

        String originalKeyHash = originalSummaryDiary.getDiaryKeyHash();


        List<Diaries> diaries = diaryRepository.findAllByUserIdAndDate(user, todayStart, todayEnd);

        String rawKey = diaries.stream()
                .map(d -> d.getId() + ":" + d.getUpdatedAt().toString())
                .sorted()
                .collect(Collectors.joining(","));

        String newKeyHash = generateSHA256(rawKey);

        if (originalKeyHash.equals(newKeyHash)) {
            return originalSummaryDiary;
        }

        String prompt  = PromptBuilder.buildPrompt(diaries);
        ChatGPTRequestDTO gptRequest = new ChatGPTRequestDTO(model, prompt);
        ChatGPTResponseDTO responseDTO = restTemplate.postForObject(apiUrl, gptRequest, ChatGPTResponseDTO.class);
        String content = responseDTO.getChoices().get(0).getMessage().getContent();

        originalSummaryDiary.updateContentAndKeyHash(content, newKeyHash);

        summaryDiaryRepository.save(originalSummaryDiary);
        return originalSummaryDiary;
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

    private String generateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 해시 생성 실패", e);
        }
    }
}
