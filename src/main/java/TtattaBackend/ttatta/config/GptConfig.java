package TtattaBackend.ttatta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptConfig {

    @Value("${openai.secret-key}")
    private String secretKey;

    @Value("${openai.model}")
    private String model;

    public String getSecretKey() {
        return secretKey;
    }

    public String getModel() {
        return model;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request,body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + secretKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
