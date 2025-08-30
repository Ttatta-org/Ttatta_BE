package TtattaBackend.ttatta.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AwsKmsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public KmsClient KmsClient() {
        return KmsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
