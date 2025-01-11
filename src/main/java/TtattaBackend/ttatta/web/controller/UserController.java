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
}
