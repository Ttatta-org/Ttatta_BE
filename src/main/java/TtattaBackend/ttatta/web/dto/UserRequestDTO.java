package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.validation.annotation.ExistUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class UserRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequestDTO {
        @NotBlank(message = "이름은 빈값일 수 없습니다.")
        private String name;
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
        @NotBlank(message = "닉네임은 빈값일 수 없습니다.")
        @Size(max = 8, message = "닉네임은 1 ~ 8자이어야 합니다.")
        private String nickname;
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        @Size(max = 15, message = "아이디는 1 ~ 15자이어야 합니다.")
        private String username;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignInRequestDTO {
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        private String username;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        private String password;
    }
  
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpKakaoRequestDTO {
        @NotBlank(message = "닉네임은 빈값일 수 없습니다.")
        @Size(max = 8, message = "닉네임은 1 ~ 8자이어야 합니다.")
        private String nickname;
    }
      
    // 미구현
    // 하단의 tokenValidationRequestDTO 에서 카카오 로그인 구현함
//    @Getter
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class SignInKakaoRequestDTO {
//        private String kakaoToken;
//    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EditRequestDTO {
        // 입력 값을 선택적으로 받기 위해 Optional 사용
        private Optional<String> nickname = Optional.empty();
        private Optional<String> email = Optional.empty();
        private Optional<String> profileImage = Optional.empty();
        private Optional<Long> point = Optional.empty();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendVerificationMailSignUpRequestDTO {
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckVerificationCodeRequestDTO {
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
        @NotBlank(message = "인증번호는 빈값일 수 없습니다.")
        private String code;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendVerificationMailFindIdRequestDTO {
        @NotBlank(message = "이름은 빈값일 수 없습니다.")
        private String name;
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendVerificationMailFindPwRequestDTO {
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        private String username;
        @NotBlank(message = "이름은 빈값일 수 없습니다.")
        private String name;
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FindPwRequestDTO {
        @NotBlank(message = "이메일은 빈값일 수 없습니다.")
        private String email;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        private String password;
    }
}
