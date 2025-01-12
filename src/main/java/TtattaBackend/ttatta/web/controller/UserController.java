package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.apiPayload.code.status.SuccessStatus;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.service.UserService.UserCommandService;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserCommandService userCommandService;

    @Operation(summary = "(개발용) 테스트 유저 생성", description =
            "# Test User를 생성합니다. 다른 기능을 테스트 할때 이용 하세요."
    )
    @PostMapping("/testuser")
    public ApiResponse<UserResponseDTO.UserSignUpResultDTO> createTestUser() {
        Users newTestUser = userCommandService.createTestUser();
        return ApiResponse.onSuccess(
                UserConverter.toUserSignUpResultDTO(
                        newTestUser
                )
        );
    }

    @Operation(summary = "회원가입", description =
            "# 회원가입 API 입니다. 닉네임과 아이디, 패스워드를 body에 입력해주세요."
    )
    @PostMapping("/signup")
    public ApiResponse<UserResponseDTO.UserSignUpResultDTO> signUp(
            @RequestBody UserRequestDTO.SignUpRequestDTO request
    ) {
        Users newUser = userCommandService.signUp(request);
        return ApiResponse.onSuccess(
                UserConverter.toUserSignUpResultDTO(
                        newUser
                )
        );
    }

    @Operation(summary = "로그인", description =
            "# 로그인 API 입니다. 아이디와 패스워드를 body에 입력해주세요."
    )
    @PostMapping("/signin")
    public ApiResponse<UserResponseDTO.UserSignInResultDTO> signIn(
            @RequestBody UserRequestDTO.SignInRequestDTO request
    ) {
        Users user = userCommandService.signIn(request);
        return ApiResponse.onSuccess(
                UserConverter.toUserSignInResultDTO(
                        user
                )
        );
    }

    @Operation(summary = "아이디 중복 확인 API", description =
            "# 아이디 중복 확인 API 입니다. 확인할 아이디를 body에 입력해주세요."
    )
    @GetMapping("/signup/same")
    public ApiResponse<UserResponseDTO.CheckUsernameSameResultDTO> checkUsernameSame(
            @RequestBody UserRequestDTO.CheckUsernameSameRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                null
        );
    }

    @Operation(summary = "로그아웃 API", description =
            "# 로그아웃 API 입니다. 로그아웃하고자 하는 userId를 body에 입력해주세요."
    )
    @DeleteMapping("/logout")
    public ApiResponse<?> logout(
            @RequestBody UserRequestDTO.LogoutRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                null
        );
    }

    @Operation(summary = "인증번호 발송 API", description =
            "# 인증번호 발송 API 입니다. 인증번호를 발송할 이메일을 body에 입력해주세요."
    )
    @PostMapping("/code")
    public ApiResponse<UserResponseDTO.SendVerificationCodeResultDTO> sendVerificationCode(
            @RequestBody UserRequestDTO.SendVerificationCodeRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                null
        );
    }

    @Operation(summary = "인증번호 확인 API (아이디 찾기용)", description =
            "# 인증번호 확인 API 입니다 (아이디 찾기용). 확인할 인증코드를 query string에 입력해주세요."
    )
    @GetMapping("/verify/id")
    public ApiResponse<UserResponseDTO.VerifyVerificationCodeForUsernameResultDTO> verifyVerificationCodeForUsername(
            @RequestParam Integer verificationCode
    ) {
        return ApiResponse.onSuccess(
                null
        );
    }

    @Operation(summary = "인증번호 확인 API (비밀번호 찾기용)", description =
            "# 인증번호 확인 API 입니다 (비밀번호 찾기용). 확인할 인증코드를 query string에 입력해주세요."
    )
    @GetMapping("/verify/pw")
    public ApiResponse<UserResponseDTO.VerifyVerificationCodeForPasswordResultDTO> verifyVerificationCodeForPassword(
            @RequestParam Integer verificationCode
    ) {
        return ApiResponse.onSuccess(
                null
        );
    }
}
