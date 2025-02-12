package TtattaBackend.ttatta.soicialLogin;

import org.springframework.stereotype.Component;

import java.security.Provider;
import java.util.EnumMap;
import java.util.Map;

@Component
public class OidcProviderFactory {
    private final Map<Provider, OidcProvider> authProviderMap;
    private final KakaoAuthProvider kakaoAuthProvider;

    public OidcProviderFactory(
            KakaoAuthProvider kakaoAuthProvider
    ) {
        this.authProviderMap = new EnumMap<>(Provider.class);
        this.kakaoAuthProvider = kakaoAuthProvider;

        initialize();
    }

    public String getProviderId(Provider provider, String idToken) {
        return getProvider(provider).getProviderId(idToken);
    }

    private void initialize() {
        authProviderMap.put(Provider.APPLE, appleAuthProvider);
        authProviderMap.put(Provider.KAKAO, kakaoAuthProvider);
        authProviderMap.put(Provider.GOOGLE, googleAuthProvider);
    }

    private OidcProvider getProvider(final Provider provider) {
        final OidcProvider oidcProvider = authProviderMap.get(provider);

        if (oidcProvider == null) {
            throw new CakkException(ReturnCode.WRONG_PROVIDER);
        }

        return oidcProvider;
    }
}
