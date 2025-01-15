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
        private String username;
        private String password;
    }
  
    // 미구현
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckUsernameSameRequestDTO {
        private String username;
    }  
  
    // 미구현
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpKakaoRequestDTO {
        private String kakaoToken;
        private String nickname;
    }
      
    // 미구현
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignInKakaoRequestDTO {
        private String kakaoToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequestDTO {
        // 입력 값을 선택적으로 받기 위해 Optional 사용
        private Optional<String> nickname = Optional.empty();
        private Optional<String> email = Optional.empty();
        private Optional<String> phoneNumber = Optional.empty();
        private Optional<String> profileImage = Optional.empty();
        private Optional<Long> point = Optional.empty();
    }
      
    // 미구현
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogoutRequestDTO {
        private Long userId;
    }  
    
    // 미구현  
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendVerificationCodeRequestDTO {
        private String email;  
    }
}
