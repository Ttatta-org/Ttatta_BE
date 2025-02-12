package TtattaBackend.ttatta.Oidc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OauthProvider {
    KAKAO("KAKAO");

    private String value;
}
