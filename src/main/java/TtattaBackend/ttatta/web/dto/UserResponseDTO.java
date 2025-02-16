package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.IsAvailable;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSignInResultDTO {
        String accessToken;
        String refreshToken;
        Long userId;
        String nickname;
        LoginType loginType;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSignUpResultDTO {
        Long userId;
        String nickname;
        LoginType loginType;
        LocalDateTime createdAt;
    }

    // 미구현
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyUsernameOverlapResultDTO {
        IsAvailable isAvailable;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshResultDTO {
        Long userId;
        String accessToken;
        String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResultDTO {
        Long userId;
        String nickname;
        LoginType loginType;
        String email;
        String profileImg;
        Long point;
        UserStatus status;
        Gender gender;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendVerificationMailResultDTO {
        Integer verificationCode;
    }
}
