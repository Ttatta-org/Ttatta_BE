package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
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

    @Operation(summary = "사용자가 PENDING 상태인지 확인하는 API", description =
            "# 사용자의 상태 검증 API 입니다."
    )
    @GetMapping("/status")
    public ApiResponse<UserResponseDTO.IsPendingResultDTO> checkIsPending(
    ) {
        return ApiResponse.onSuccess(
                userCommandService.checkIsPending()
        );
    }

    @Operation(summary = "카카오 openId 검증 API", description =
            "# 카카오 openId 검증 API 입니다. header에 'OpenId: {ID token}'형식으로 ID token을 입력해주세요."
    )
    @PostMapping("/signup/kakao")
    public ApiResponse<UserResponseDTO.UserKaKaoOpenIdResultDTO> openIdKakao(
            @RequestHeader("OpenId") String openId
    ) {
        return ApiResponse.onSuccess(
                userCommandService.openIdKakao(openId)
        );
    }

    @Operation(summary = "카카오 회원가입 API", description =
            "# 카카오 회원가입 API 입니다. request body에 닉네임을 입력해주세요."
    )
    @PostMapping("/kakao/signup/nickname")
    public ApiResponse<UserResponseDTO.KaKaoFinalSignUpResultDTO> signUpNickname(
            @RequestBody UserRequestDTO.SignUpKakaoRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                userCommandService.kakaoSignUp(request)
        );
    }

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


    @Operation(summary = "인증메일 발송 (회원가입)", description =
            "# 인증메일 발송 API 입니다. 회원가입 시, 입력한 이메일의 중복 여부를 확인 후 인증 메일을 발송합니다."
    )
    @PostMapping("/signup/verify/send")
    public ApiResponse<Object> sendVerificationMailSignUp(
            @RequestBody UserRequestDTO.SendVerificationMailSignUpRequestDTO request
    ) {
        userCommandService.sendVerificationMailSignUp(request);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "인증번호 확인", description =
            "# 인증번호 확인 API 입니다. 입력한 이메일로 발송된 인증번호를 입력해주세요."
    )
    @PostMapping("/signup/verify/check")
    public ApiResponse<Object> checkVerificationCodeSignUp(
            @RequestBody UserRequestDTO.CheckVerificationCodeRequestDTO request
    ) {
        userCommandService.checkVerificationCode(request);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "인증메일 발송 (ID 찾기)", description =
        "# 인증메일 발송 API 입니다. ID 찾기 시, 입력한 이메일과 이름의 일치 여부를 확인 후 인증메일을 발송합니다."
    )
    @PostMapping("/find/send-id")
    public ApiResponse<Object> sendVerificationMailFindId(
            @RequestBody UserRequestDTO.SendVerificationMailFindIdRequestDTO request
    ) {
        userCommandService.sendVerificationMailFindId(request);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "ID 찾기", description =
            "# ID 찾기 API 입니다. 입력한 이메일로 발송된 인증번호를 입력해주세요."
    )
    @PostMapping("/find/id")
    public ApiResponse<UserResponseDTO.FindIdResultDTO> findId(
            @RequestBody UserRequestDTO.CheckVerificationCodeRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                userCommandService.findId(request)
        );
    }

    @Operation(summary = "비밀번호 찾기 시 ID 존재여부 검증 API", description =
            "# 비밀번호 찾기 시 ID 존재여부 검증 API 입니다. 비밀번호를 찾고자 하는 계정의 ID를 입력해주세요."
    )
    @GetMapping("/find/verify/id")
    public ApiResponse<Object> verifyUsername(
            @RequestParam String username
    ) {
        userCommandService.verifyUsername(username);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "인증메일 발송 (PW 찾기)", description =
            "# 인증메일 발송 API 입니다. PW 찾기 시, 입력한 ID의 존재 여부, 이메일과 이름의 일치 여부를 확인 후 인증메일을 발송합니다."
    )
    @PostMapping("/find/send-pw")
    public ApiResponse<Object> sendVerificationMailFindPw(
            @RequestBody UserRequestDTO.SendVerificationMailFindPwRequestDTO request
    ) {
        userCommandService.sendVerificationMailFindPw(request);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "PW 재설정", description =
            "# PW 재설정 API 입니다. 이메일과 변경할 비밀번호를 입력해주세요."
    )
    @PostMapping("/find/pw")
    public ApiResponse<Object> findPw(
            @RequestBody UserRequestDTO.FindPwRequestDTO request
    ) {
        userCommandService.findPw(request);
        return ApiResponse.onSuccess("");
    }

//    @Operation(summary = "카카오 로그인 시 회원가입인지 로그인인지 확인하는 API", description =
//                    "header에 'OpenId: {ID token}'형식으로 ID token을 입력해주세요.\n" +
//                    "1. 페이로드 검증 및 서명 검증을 진행합니다.\n" +
//                    "2. 이미 가입한 회원인지 확인합니다.\n\n" +
//                    "회원가입이라면 isRegistered로 false를 반환하고 로그인이라면 isRegistered로 true를 반환함과 동시에 access token과 refresh token을 반환합니다."
//    )
//    @PostMapping("/verificate/kakao")
//    public ApiResponse<UserResponseDTO.TokenValidationResultDTO> validKakaoToken(
//            @RequestHeader("OpenId") String openId
//    ) {
//        return ApiResponse.onSuccess(
//                userCommandService.validateToken(openId)
//        );
//    }

    @Operation(summary = "[관리자용] 회원 삭제", description =
            "# 관리자용 API입니다. 삭제할 회원의 ID를 입력해주세요. (사용 주의)"
    )
    @DeleteMapping("/admin/{userId}")
    public ApiResponse<Object> deleteUserByAdmin(
            @PathVariable Long userId
    ) {
        userCommandService.deleteUserByAdmin(userId);
        return ApiResponse.onSuccess("");
    }
}
