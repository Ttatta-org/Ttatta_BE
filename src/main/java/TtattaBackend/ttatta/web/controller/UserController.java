package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.apiPayload.code.status.SuccessStatus;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.service.UserService.UserCommandService;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
