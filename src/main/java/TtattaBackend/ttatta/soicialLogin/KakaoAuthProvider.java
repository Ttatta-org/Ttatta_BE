package TtattaBackend.ttatta.soicialLogin;

import TtattaBackend.ttatta.Oidc.KakaoOauthClient;
import org.springframework.stereotype.Component;

@Component
public class KakaoAuthProvider {
    private KakaoOauthClient KakaoAuthClient;
}