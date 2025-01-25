package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;

public interface UserCommandService {
    Users createTestUser();
    Users signUp(UserRequestDTO.SignUpRequestDTO request);
    UserResponseDTO.UserSignInResultDTO signIn(UserRequestDTO.SignInRequestDTO request);
    UserResponseDTO.RefreshResultDTO refresh(String refreshToken);
    Users signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request);    // 미구현
    Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request);    // 미구현
    Users getUserInfo(Long userId);
    Users updateUserInfo(Long userId, UserRequestDTO.UpdateRequestDTO request);
    void deleteUser(Long userId);
}
