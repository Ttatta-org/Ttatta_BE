package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.Gender;
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
    public static class UserInfoResultDTO {
        Long userId;
        String nickname;
        LoginType loginType;
        String email;
        String profileImg;
        Long point;
        UserStatus status;
        Gender gender;
        String phoneNum;
    }
}
