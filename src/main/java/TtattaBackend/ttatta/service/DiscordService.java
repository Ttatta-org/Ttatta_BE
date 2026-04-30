package TtattaBackend.ttatta.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    public DiscordService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(3)) // 연결 자체를 맺는데 걸리는 최대 시간 (3초)
                .readTimeout(Duration.ofSeconds(3))    // 연결 후 응답 데이터를 읽는데 걸리는 최대 시간 (3초)
                .build();
    }

    public void sendSignUpNotification(String username, String type) {
        Map<String, String> message = new HashMap<>();
        message.put("content", "새로운 회원 " + username + "님이 " + type + " 가입하셨습니다!");

        try {
            restTemplate.postForEntity(webhookUrl, message, String.class);
        } catch (Exception e) {
            // 예외 처리 (로그 기록 등)
            System.err.println("Discord 알림 전송 실패: " + e.getMessage());
        }
    }
}
