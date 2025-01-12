package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;

public interface UserCommandService {
    Users createTestUser();
    Users signUp(UserRequestDTO.SignUpRequestDTO request);
    Users signIn(UserRequestDTO.SignInRequestDTO request);
    Users signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request);    // 미구현
    Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request);    // 미구현
    Users refresh(String request);  // 미구현
    Users getUserInfo(Long userId);
    Users updateUserInfo(Long userId, UserRequestDTO.UpdateRequestDTO request);
    void deleteUser(Long userId);
}
