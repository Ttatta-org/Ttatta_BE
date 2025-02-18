package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.IsAvailable;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;

public interface UserCommandService {
    Users createTestUser();
    Users signUp(UserRequestDTO.SignUpRequestDTO request);
    IsAvailable verifyUsernameOverlap(String username);
    void logout(String accessToken);
    UserResponseDTO.UserSignInResultDTO signIn(UserRequestDTO.SignInRequestDTO request);
    UserResponseDTO.RefreshResultDTO refresh(String refreshToken);
    UserResponseDTO.UserKaKaoSignUpResultDTO signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request);
//    Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request);    // 미구현
    UserResponseDTO.UserInfoResultDTO getUserInfo();
    Users editUserInfo(UserRequestDTO.EditRequestDTO request);
    void deleteUser();
    UserResponseDTO.TokenValidationResultDTO validateToken(UserRequestDTO.tokenValidationRequestDTO request);
    Long getUserPoint();
}
