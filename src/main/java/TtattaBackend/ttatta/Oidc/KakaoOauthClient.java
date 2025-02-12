package TtattaBackend.ttatta.Oidc;

import TtattaBackend.ttatta.config.KaKaoOauthConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = KaKaoOauthConfig.class
)

public interface KakaoOauthClient {
    @PostMapping(
            "/oauth/token?grant_type=authorization_code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&code={CODE}&client_secret={CLIENT_SECRET}")
    KakaoTokenResponse kakaoAuth(
            @PathVariable("CLIENT_ID") String clientId,
            @PathVariable("REDIRECT_URI") String redirectUri,
            @PathVariable("CODE") String code,
            @PathVariable("CLIENT_SECRET") String client_secret);

    @Cacheable(cacheNames = "KaKaoOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeyResponse getKakaoOIDCOpenKeys();
}
