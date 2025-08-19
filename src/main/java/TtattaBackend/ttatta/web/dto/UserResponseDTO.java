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
        Long diaryCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoEditResultDTO {
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
    public static class UserDeleteResultDTO {
        Long id;
        String reason;
        LocalDateTime withdrawnAt;
        Integer activeDays;
        Integer totalDiary;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindIdResultDTO {
        String name;
        String id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenValidationResultDTO {
        Boolean isRegistered;
        String accessToken;
        String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserKaKaoOpenIdResultDTO {
        Boolean isRegistered;
        String accessToken;
        String refreshToken;
        Long userId;
        LoginType loginType;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KaKaoFinalSignUpResultDTO {
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
    public static class IsPendingResultDTO {
        boolean isPending;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetPinResultDTO {
        String pinHash;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePinResultDTO {
        String newPinHash;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPinResultDTO {
        String pinHash;
    }
}
