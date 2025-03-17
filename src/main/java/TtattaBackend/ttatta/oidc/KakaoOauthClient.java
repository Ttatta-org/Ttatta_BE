package TtattaBackend.ttatta.oidc;

import TtattaBackend.ttatta.config.KaKaoOauthConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = KaKaoOauthConfig.class
)

public interface KakaoOauthClient {
    @Cacheable(cacheNames = "KaKaoOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeyResponse getKakaoOIDCOpenKeys();
}