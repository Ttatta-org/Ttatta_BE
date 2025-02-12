package TtattaBackend.ttatta.Oidc;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Helper;

import static org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor.BEARER;

//@Helper
@RequiredArgsConstructor
public class KakaoOauthHelper {
    private final OauthProperties oauthProperties;
    private final KakaoInfoClient kakaoInfoClient;
    private final KakaoOauthClient kakaoOauthClient;
    private final OauthOIDCHelper oauthOIDCHelper;
    public static final String KAKAO_OAUTH_QUERY_STRING =
            "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code";


    public String getKaKaoOauthLinkTest() {
        return oauthProperties.getKakaoBaseUrl()
                + String.format(
                KAKAO_OAUTH_QUERY_STRING,
                oauthProperties.getKakaoClientId(),
                oauthProperties.getKakaoRedirectUrl());
    }

    public String getKaKaoOauthLink(String referer) {
        return oauthProperties.getKakaoBaseUrl()
                + String.format(
                KAKAO_OAUTH_QUERY_STRING,
                oauthProperties.getKakaoClientId(),
                referer + "/kakao/callback");
    }

    public KakaoTokenResponse getOauthToken(String code, String referer) {

        return kakaoOauthClient.kakaoAuth(
                oauthProperties.getKakaoClientId(),
                referer + "/kakao/callback",
                code,
                oauthProperties.getKakaoClientSecret());
    }

    public KakaoTokenResponse getOauthTokenTest(String code) {

        return kakaoOauthClient.kakaoAuth(
                oauthProperties.getKakaoClientId(),
                oauthProperties.getKakaoRedirectUrl(),
                code,
                oauthProperties.getKakaoClientSecret());
    }

    public KakaoUserInfoDto getUserInfo(String oauthAccessToken) {
        KakaoInformationResponse response =
                kakaoInfoClient.kakaoUserInfo(BEARER + oauthAccessToken);

        return KakaoUserInfoDto.builder()
                .oauthProvider(OauthProvider.KAKAO)
                .name(response.getName())
                .phoneNumber(response.getPhoneNumber())
                .profileImage(response.getProfileUrl())
                .email(response.getEmail())
                .oauthId(response.getId())
                .build();
    }

    public OIDCDecodePayload getOIDCDecodePayload(String token) {

        // 공개키 목록을 조회한다. 캐싱이 되어있다.
        OIDCPublicKeyResponse oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();
        return oauthOIDCHelper.getPayloadFromIdToken(
                //idToken
                token,
                // iss 와 대응되는 값
                oauthProperties.getKakaoBaseUrl(),
                // aud 와 대응되는 값
                oauthProperties.getKakaoAppId(),
                // 공개키 목록
                oidcPublicKeysResponse
        );
    }

    public OauthInfo getOauthInfoByIdToken(String idToken) {
        OIDCDecodePayload oidcDecodePayload = getOIDCDecodePayload(idToken);
        return OauthInfo.builder()
                .provider(OauthProvider.KAKAO)
                .oid(oidcDecodePayload.getSub())
                .build();
    }

    public void unlink(String oid) {
        String kakaoAdminKey = oauthProperties.getKakaoAdminKey();
        UnlinkKaKaoTarget unlinkKaKaoTarget = UnlinkKaKaoTarget.from(oid);
        String header = "KakaoAK " + kakaoAdminKey;
        kakaoInfoClient.unlinkUser(header, unlinkKaKaoTarget);
    }

}
