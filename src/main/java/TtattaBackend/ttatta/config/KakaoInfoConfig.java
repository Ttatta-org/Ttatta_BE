package TtattaBackend.ttatta.config;

import TtattaBackend.ttatta.Oidc.KakaoInfoErrorDecoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(KakaoInfoErrorDecoder.class)
public class KakaoInfoConfig {
    @Bean
    @ConditionalOnMissingBean(value = ErrorDecoder.class)
    public KakaoInfoErrorDecoder commonFeignErrorDecoder() {
        return new KakaoInfoErrorDecoder();
    }

    @Bean
    FormEncoder formEncoder() {
        return new feign.form.FormEncoder();
    }
}
