package TtattaBackend.ttatta.config.security;

import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AwsKmsConfig {

    @Value("${cloud.aws.region.static}")
    private String awsRegion; // ap-northeast-2

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    // 로컬 테스트용
//    @Bean
//    public KmsClient KmsClient() {
//        return KmsClient.builder()
//                .region(Region.of(awsRegion))
//                .credentialsProvider(
//                        software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider.create("dev")
//                )
//                .build();
//    }

    @Bean
    public KmsClient kmsClient() {

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return KmsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
