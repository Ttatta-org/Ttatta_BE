package TtattaBackend.ttatta;

import TtattaBackend.ttatta.oidc.OauthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "TtattaBackend.ttatta.oidc")
@EnableCaching
@EnableConfigurationProperties(OauthProperties.class)
public class TtattaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtattaApplication.class, args);
	}

}
