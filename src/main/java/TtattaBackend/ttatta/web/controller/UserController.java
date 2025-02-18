package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.oidc.*;
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
            @RequestBody @Valid UserRequestDTO.SignUpRequestDTO request
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
            @RequestBody @Valid UserRequestDTO.SignInRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                userCommandService.signIn(request)
        );
    }

    @Operation(summary = "회원가입 중 아이디 중복 확인 API", description =
            "# 회원가입 중 아이디 중복 확인 API 입니다. 중복을 확인할 아이디를 body에 입력해주세요.\n"
            + "# 아이디가 사용가능하면 AVAILABLE, 사용불가능하다면 UNAVAILABLE을 반환합니다."
    )
    @GetMapping("/signup/verify/overlap")
    public ApiResponse<UserResponseDTO.VerifyUsernameOverlapResultDTO> checkUsernameSame(
            @RequestParam String username
    ) {
        return ApiResponse.onSuccess(
                UserConverter.toVerifyUsernameOverlapResultDTO(
                        userCommandService.verifyUsernameOverlap(username)
                )
        );
    }

    // 미구현
    @Operation(summary = "토큰 갱신", description =
            "# access token 갱신 API 입니다. access token과 refresh token을 header에 입력해주세요."
    )
    @PostMapping("/refresh")
    public ApiResponse<UserResponseDTO.RefreshResultDTO> refreshToken(
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        return ApiResponse.onSuccess(
                userCommandService.refresh(refreshToken)
        );
    }

    @Operation(summary = "로그아웃 API", description =
            "# 로그아웃 API 입니다. 로그아웃하고자 하는 유저의 access token을 header에 입력해주세요."
    )
    @DeleteMapping("/logout")
    public ApiResponse<?> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userCommandService.logout(accessToken);
        return ApiResponse.onSuccess("");
    }

    // 미구현
    @Operation(summary = "카카오 회원가입", description =
            "# 카카오 회원가입 API 입니다. header에 'OpneId: {ID token}'형식으로 ID token을 입력하고 request body에 닉네임을 입력해주세요."
    )
    @PostMapping("/signup/kakao")
    public ApiResponse<UserResponseDTO.UserKaKaoSignUpResultDTO> signUpKakao(
            @RequestHeader("OpneId") String openId,
            @RequestBody UserRequestDTO.SignUpKakaoRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                userCommandService.signUpKakao(openId, request)
        );
    }

    // 미구현
//    @Operation(summary = "카카오 로그인", description =
//            "# 카카오 로그인 API 입니다."
//    )
//    @PostMapping("/signin/kakao")
//    public ApiResponse<UserResponseDTO.UserSignInResultDTO> signInKakao(
//            @RequestBody UserRequestDTO.SignInKakaoRequestDTO request
//    ) {
//        Users user = userCommandService.signInKakao(request);
//        return ApiResponse.onSuccess(
//                UserConverter.toUserSignInResultDTO(
//                        user, null, null
//                )
//        );
//    }

    @Operation(summary = "회원 정보 조회", description =
            "# 회원 정보 조회 API 입니다."
    )
    @GetMapping("/info")
    public ApiResponse<UserResponseDTO.UserInfoResultDTO> getUserInfo() {
        return ApiResponse.onSuccess(
                userCommandService.getUserInfo()
        );
    }

    @Operation(summary = "회원 정보 수정", description =
            "# 회원 정보 수정 API 입니다. 수정할 정보를 입력해주세요.\n수정을 원하는 데이터만 보내도 수정 가능합니다."
    )
    @PatchMapping("/info")
    public ApiResponse<UserResponseDTO.UserInfoEditResultDTO> editUserInfo(
            @RequestBody UserRequestDTO.EditRequestDTO request
    ) {
        Users user = userCommandService.editUserInfo(request);
        return ApiResponse.onSuccess(
                UserConverter.toUserInfoEditResultDTO(
                        user
                )
        );
    }

    @Operation(summary = "회원 탈퇴", description =
            "# 회원 탈퇴 API 입니다."
    )
    @DeleteMapping("")
    public ApiResponse<Object> deleteUser() {
        userCommandService.deleteUser();
        return ApiResponse.onSuccess("");
    }

    // 미구현
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

    // 미구현
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

    // 미구현
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

    @Operation(summary = "카카오 로그인 시 회원가입인지 로그인인지 확인하는 API", description =
                    "header에 'OpneId: {ID token}'형식으로 ID token을 입력해주세요.\n" +
                    "1. 페이로드 검증 및 서명 검증을 진행합니다.\n" +
                    "2. 이미 가입한 회원인지 확인합니다.\n\n" +
                    "회원가입이라면 isRegistered로 false를 반환하고 로그인이라면 isRegistered로 true를 반환함과 동시에 access token과 refresh token을 반환합니다."
    )
    @PostMapping("/verificate/kakao")
    public ApiResponse<UserResponseDTO.TokenValidationResultDTO> validKakaoToken(
            @RequestHeader("OpneId") String openId
    ) {
        return ApiResponse.onSuccess(
                userCommandService.validateToken(openId)
        );
    }
}
