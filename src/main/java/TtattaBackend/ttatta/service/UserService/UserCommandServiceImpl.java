package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.*;
import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender javaMailSender;

    @Override
    public Users createTestUser() {
        Users newUser = Users.builder()
                .nickname("testName")
                .username("testId")
                .password("testPassword")
                .loginType(LoginType.REGULAR)
                .email(LocalDateTime.now() + "@test.com")
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
        // 일상 카테고리 생성
        createDefaultCategory(newUser);
        return userRepository.save(newUser);
    }

    private void createDefaultCategory(Users newUser) {
        DiaryCategories defaultDiaryCategories = DiaryCategoryConverter.toDiaryCategory(
                DiaryCategoryRequestDTO.CreateCategoryDTO.builder()
                    .categoryName("일상")
                    .categoryColor(CategoryColor.RED)
                    .build()
        );
        defaultDiaryCategories.setUsers(newUser);
        diaryCategoryRepository.save(defaultDiaryCategories);
    }

    @Override
    @Transactional // ???
    public UserResponseDTO.UserSignInResultDTO signIn(UserRequestDTO.SignInRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication); // 로그인을 한 후 인증 정보를 사용할 일은 없을 것 같다.

        Users user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        // 인증 완료 후 jwt토큰(accessToken) 생성
        Map<String, Object> valueMap = Map.of(
                "userId", user.getId() // String으로 저장??? 그래서 SecurityUtil에서 Long으로 타입변환 해주나?
        );
        String accessToken = jwtUtils.generateToken(valueMap, accessExpTime);

        // 인증 완료 후 jwt토큰(refreshToken) 생성
        String refreshToken = jwtUtils.generateToken(Collections.emptyMap(), refreshExpTime);
        redisTemplate.opsForValue().set(user.getId().toString(), refreshToken, refreshExpTime, TimeUnit.MINUTES);
        System.out.println("redis에 저장된 refreshToken: " + (String) redisTemplate.opsForValue().get(user.getId().toString()));

        return UserConverter.toUserSignInResultDTO(user, accessToken, refreshToken);
    }

    @Override
    public UserResponseDTO.RefreshResultDTO refresh(String refreshToken) {
        Long userId = SecurityUtil.getCurrentUserId();
        String accessToken;
        String newRefreshToken;

        // 전달된 refresh token과 redis의 refresh token비교
        String getUserIdFromRedis = redisTemplate.opsForValue().get(userId.toString());
        System.out.println("userId: " + userId);
        System.out.println("redis에서 가져온 refreshToken: " + getUserIdFromRedis);
        if (refreshToken.equals(getUserIdFromRedis)) {
            // 인증 완료 후 jwt토큰(accessToken) 생성
            Map<String, Object> valueMap = Map.of(
                    "userId", userId
            );
            accessToken = jwtUtils.generateToken(valueMap, accessExpTime);
            // 인증 완료 후 jwt토큰(refreshToken) 생성
            newRefreshToken = jwtUtils.generateToken(Collections.emptyMap(), refreshExpTime);
            redisTemplate.opsForValue().set(userId.toString(), newRefreshToken, refreshExpTime, TimeUnit.MINUTES);
        } else {
            throw new ExceptionHandler(REFRESHTOKEN_NOT_EQUAL);
        }

        return UserConverter.toRefreshResultDTO(userId, accessToken, newRefreshToken);
    }

    @Override
    public IsAvailable verifyUsernameOverlap(String username) {
        Users getUser = userRepository.findByUsername(username).orElse(null);
        System.out.println("getUser");
        if (getUser == null) {
            return IsAvailable.AVAILABLE;
        }
        return IsAvailable.UNAVAILABLE;
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

    @Override
    public Users getUserInfo() {
        return userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));
    }

    @Override
    public Users updateUserInfo(UserRequestDTO.UpdateRequestDTO request) {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 입력 들어온 값만 업데이트
        request.getNickname().ifPresent(user::updateNickname);
        request.getEmail().ifPresent(user::updateEmail);
        request.getProfileImage().ifPresent(user::updateProfileImage);
        request.getPoint().ifPresent(user::updatePoint);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Override
    public void sendMail(String email) {

        // 인증 번호 (6자리 난수) 생성
        int verificationCode = (int)(Math.random() * 899999) + 100000;

        MimeMessage message = javaMailSender.createMimeMessage();

        // 이메일 내용 설정
        try {
            message.setFrom(email);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[따따] 이메일 인증 코드 발송");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + verificationCode + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "utf-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 인증번호 Redis 저장 (유효시간 10분)
        redisTemplate.opsForValue().set(email, String.valueOf(verificationCode), 10, TimeUnit.MINUTES);

        javaMailSender.send(message);
    }

    @Override
    public void sendVerificationMailSignUp(UserRequestDTO.SendVerificationMailSignUpRequestDTO request) {
        String inputEmail = request.getEmail();

        // 이메일 중복 여부 확인
        if(userRepository.existsByEmail(inputEmail)) {
            throw new ExceptionHandler(EMAIL_ALREADY_EXIST);
        }

        sendMail(inputEmail);
    }

    @Override
    public void checkVerificationCode(UserRequestDTO.CheckVerificationCodeRequestDTO request) {
        String inputEmail = request.getEmail();
        String inputCode = request.getCode();

        // 입력한 이메일로 저장된 인증번호 가져오기
        String code = redisTemplate.opsForValue().get(inputEmail);

        // 인증번호 일치 여부 확인
        if (code == null || !code.equals(inputCode)) {
            throw new ExceptionHandler(CODE_NOT_EQUAL);
        }
    }

    @Override
    public void sendVerificationMailFind(UserRequestDTO.SendVerificationMailFindRequestDTO request) {
        String inputName = request.getName();
        String inputEmail = request.getEmail();

        // 이메일로 유저 찾기
        Users user = userRepository.findByEmail(inputEmail)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 이메일와 이름 일치 여부 확인
        if(!user.getName().equals(inputName)) {
            throw new ExceptionHandler(NAME_NOT_EQUAL);
        }

        sendMail(inputEmail);
    }

    @Override
    public UserResponseDTO.FindIdResultDTO findId(UserRequestDTO.CheckVerificationCodeRequestDTO request) {
        checkVerificationCode(request);

        // 이메일로 유저 찾기
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return UserConverter.toFindIdResultDTO(user);
    }
}
