package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.IsAvailable;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;

public interface UserCommandService {
    Users createTestUser();
    Users signUp(UserRequestDTO.SignUpRequestDTO request);
    IsAvailable verifyUsernameOverlap(String username);
    UserResponseDTO.UserSignInResultDTO signIn(UserRequestDTO.SignInRequestDTO request);
    UserResponseDTO.RefreshResultDTO refresh(String refreshToken);
    Users signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request);    // 미구현
    Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request);    // 미구현
    Users getUserInfo();
    Users updateUserInfo(UserRequestDTO.UpdateRequestDTO request);
    void deleteUser();
    void sendMail(String email);
    void sendVerificationMailSignUp(UserRequestDTO.SendVerificationMailSignUpRequestDTO request);
    void checkVerificationCode(UserRequestDTO.CheckVerificationCodeRequestDTO request);
    void sendVerificationMailFindId(UserRequestDTO.SendVerificationMailFindIdRequestDTO request);
    UserResponseDTO.FindIdResultDTO findId(UserRequestDTO.CheckVerificationCodeRequestDTO request);
    void sendVerificationMailFindPw(UserRequestDTO.SendVerificationMailFindPwRequestDTO request);
    void findPw(UserRequestDTO.FindPwRequestDTO request);
}
