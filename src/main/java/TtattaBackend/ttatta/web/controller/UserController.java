package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.service.UserService.UserCommandService;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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


    // 미구현
    @Operation(summary = "카카오 회원가입", description =
            "# 카카오 회원가입 API 입니다."
    )
    @PostMapping("/signup/kakao")
    public ApiResponse<UserResponseDTO.UserSignUpResultDTO> signUpKakao(
            @RequestBody UserRequestDTO.SignUpKakaoRequestDTO request
    ) {
        Users newUser = userCommandService.signUpKakao(request);
        return ApiResponse.onSuccess(
                UserConverter.toUserSignUpResultDTO(
                        newUser
                )
        );
    }

    // 미구현
    @Operation(summary = "카카오 로그인", description =
            "# 카카오 로그인 API 입니다."
    )
    @PostMapping("/signin/kakao")
    public ApiResponse<UserResponseDTO.UserSignInResultDTO> signInKakao(
            @RequestBody UserRequestDTO.SignInKakaoRequestDTO request
    ) {
        Users user = userCommandService.signInKakao(request);
        return ApiResponse.onSuccess(
                UserConverter.toUserSignInResultDTO(
                        user
                )
        );
    }

    // 미구현
    @Operation(summary = "토큰 갱신", description =
            "# 토큰 갱신 API 입니다. 리프레시 토큰을 header에 입력해주세요."
    )
    @PostMapping("/refresh")
    public ApiResponse<UserResponseDTO.RefreshResultDTO> refreshToken(
            @RequestHeader("refreshToken") String refreshToken
    ) {
        Users user = userCommandService.refresh(refreshToken);
        return ApiResponse.onSuccess(
                UserConverter.toRefreshResultDTO(
                        user
                )
        );
    }

    @Operation(summary = "회원 정보 조회", description =
            "# 회원 정보 조회 API 입니다. 회원의 ID를 입력해주세요."
    )
    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO.UserInfoResultDTO> getUserInfo(
            @PathVariable Long userId
    ) {
        Users user = userCommandService.getUserInfo(userId);
        return ApiResponse.onSuccess(
                UserConverter.toUserInfoResultDTO(
                        user
                )
        );
    }

    @Operation(summary = "회원 정보 수정", description =
            "# 회원 정보 수정 API 입니다. 회원의 ID와 수정할 정보를 입력해주세요.\n수정을 원하는 데이터만 보내도 수정 가능합니다."
    )
    @PatchMapping("/{userId}")
    public ApiResponse<UserResponseDTO.UserInfoResultDTO> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody UserRequestDTO.UpdateRequestDTO request
    ) {
        Users user = userCommandService.updateUserInfo(userId, request);
        return ApiResponse.onSuccess(
                UserConverter.toUserInfoResultDTO(
                        user
                )
        );
    }

    @Operation(summary = "회원 탈퇴", description =
            "# 회원 탈퇴 API 입니다. 회원의 ID를 입력해주세요."
    )
    @DeleteMapping("/{userId}")
    public ApiResponse<Object> deleteUser(
            @PathVariable Long userId
    ) {
        userCommandService.deleteUser(userId);
        return ApiResponse.onSuccess("");
    }
}
