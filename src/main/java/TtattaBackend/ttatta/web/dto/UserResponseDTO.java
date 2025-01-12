package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.IsSame;
import TtattaBackend.ttatta.domain.enums.LoginType;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckUsernameSameResultDTO {
        IsSame isSame;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendVerificationCodeResultDTO {
        Integer verificationCode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyVerificationCodeForUsernameResultDTO {
        String username;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyVerificationCodeForPasswordResultDTO {
        String newPassword;
    }
}
