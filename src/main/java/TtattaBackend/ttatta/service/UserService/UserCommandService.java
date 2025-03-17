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
    UserResponseDTO.UserKaKaoSignUpResultDTO signUpKakao(String openId, UserRequestDTO.SignUpKakaoRequestDTO request);
//    Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request);    // 미구현
    UserResponseDTO.UserInfoResultDTO getUserInfo();
    Users editUserInfo(UserRequestDTO.EditRequestDTO request);
    void deleteUser();
    void sendMail(String email);
    void sendVerificationMailSignUp(UserRequestDTO.SendVerificationMailSignUpRequestDTO request);
    void checkVerificationCode(UserRequestDTO.CheckVerificationCodeRequestDTO request);
    void sendVerificationMailFindId(UserRequestDTO.SendVerificationMailFindIdRequestDTO request);
    UserResponseDTO.FindIdResultDTO findId(UserRequestDTO.CheckVerificationCodeRequestDTO request);
    void verifyUsername(String username);
    void sendVerificationMailFindPw(UserRequestDTO.SendVerificationMailFindPwRequestDTO request);
    void findPw(UserRequestDTO.FindPwRequestDTO request);
    UserResponseDTO.TokenValidationResultDTO validateToken(String openId);
    Long getUserPoint();
    void deleteUserByAdmin(Long userId);
}
