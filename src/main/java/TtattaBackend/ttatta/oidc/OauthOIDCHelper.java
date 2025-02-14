package TtattaBackend.ttatta.oidc;

import lombok.RequiredArgsConstructor;

//@Helper
@RequiredArgsConstructor
public class OauthOIDCHelper {
    private final JwtOIDCProvider jwtOIDCProvider;

    // OauthOIDC는 스펙이기때문에 OauthOIDCHelper 하나로 카카오,구글 다 대응 가능하다.
    // KakaoOauthHelper 등에서 아래 소스들을 사용한다.
    // kid를 토큰에서 가져온다.

    private String getKidFromUnsignedIdToken(String token, String iss, String aud) {
        return jwtOIDCProvider.getKidFromUnsignedTokenHeader(token, iss, aud);
    }

    public OIDCDecodePayload getPayloadFromIdToken (
            String token, String iss, String aud, OIDCPublicKeyResponse oidcPublicKeyResponse
    ) {
        String kid = getKidFromUnsignedIdToken(token, iss, aud);
        // KakaoOauthHelper 에서 공개키를 조회했고 해당 디티오를 넘겨준다.
        OIDCPublicKeyDto oidcPublicKeyDto =
                oidcPublicKeyResponse.getKeys().stream()
                        // 같은 kid를 가져온다
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        // 검증이 된 토큰에서 바디를 꺼내온다.
        return jwtOIDCProvider.getOIDCTokenBody(
                token,oidcPublicKeyDto.getN(), oidcPublicKeyDto.getE());
    }
}
