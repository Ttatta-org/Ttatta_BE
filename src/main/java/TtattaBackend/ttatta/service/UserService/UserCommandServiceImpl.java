package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    @Value("${jwt.ACCESS_EXP_TIME}")
    private int accessExpTime;
    @Value("${jwt.REFRESH_EXP_TIME}")
    private int refreshExpTime;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtils jwtUtils;

    @Override
    public Users createTestUser() {
        Users newUser = Users.builder()
                .nickname("testName")
                .username("testId")
                .password("testPassword")
                .loginType(LoginType.REGULAR)
                .email(LocalDateTime.now() + "@test.com")
                .phoneNumber("010-0000-0000")
                .profileImage("testProfileImage")
                .gender(Gender.MALE)
                .point(0L)
                .status(UserStatus.ACTIVE)
                .inactiveDate(null)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional // ???
    public Users signUp(UserRequestDTO.SignUpRequestDTO request) {
        Users newUser = UserConverter.toUsers(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    @Transactional // ???
    public UserResponseDTO.UserSignInResultDTO signIn(UserRequestDTO.SignInRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication); // 로그인을 한 후 인증 정보를 사용할 일은 없을 것 같다.

        Users user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        // 인증 완료 후 jwt 생성
        Map<String, Object> valueMap = Map.of(
                "userId", user.getId()
        );
        String accessToken = jwtUtils.generateToken(valueMap, accessExpTime);

        return UserConverter.toUserSignInResultDTO(user, accessToken);
    }

    // 미구현
    @Override
    public Users signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request) {
        // 서비스 구현
        return null;
    }

    // 미구현
    @Override
    public Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request) {
        // 서비스 구현
        return null;
    }

    // 미구현
    @Override
    public Users refresh(String request) {
        // 서비스 구현
        return null;
    }

    @Override
    public Users getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));
    }

    @Override
    public Users updateUserInfo(Long userId, UserRequestDTO.UpdateRequestDTO request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 입력 들어온 값만 업데이트
        request.getNickname().ifPresent(user::updateNickname);
        request.getEmail().ifPresent(user::updateEmail);
        request.getPhoneNumber().ifPresent(user::updatePhoneNumber);
        request.getProfileImage().ifPresent(user::updateProfileImage);
        request.getPoint().ifPresent(user::updatePoint);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 이후 유저에 연관된 모든 데이터 삭제해야함 cascade 설정 필요

        userRepository.delete(user);
    }
}
