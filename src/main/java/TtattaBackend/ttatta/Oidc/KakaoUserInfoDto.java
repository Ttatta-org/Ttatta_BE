package TtattaBackend.ttatta.Oidc;

import TtattaBackend.ttatta.domain.Users;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.annotation.Profile;

@Getter
@Builder
public class KakaoUserInfoDto {

    // oauth인증한 사용자 고유 아이디
    private final String oauthId;

    private final String email;
    private final String phoneNumber;
    private final String profileImage;
    private final String name;
    // oauth 제공자
    private final OauthProvider oauthProvider;

//    public Users toUser() {
//        return Users.builder()
//                .profileImage(this.profileImage)
//                .name(name)
//                .email(email)
//                .build();
//    }

//    public OauthInfo toOauthInfo() {
//        return OauthInfo.builder().oid(oauthId).provider(oauthProvider).build();
//    }
}
