package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.JwtAuthenticationFilter;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.UsersWithdrawals;
import TtattaBackend.ttatta.domain.enums.*;
import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.oidc.*;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.repository.UserWithdrawalRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final OauthOIDCHelper oauthOIDCHelper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Value("${jwt.ACCESS_EXP_TIME}")
    private int accessExpTime;
    @Value("${jwt.REFRESH_EXP_TIME}")
    private int refreshExpTime;

    @Value("${oidc.iss}")
    private String iss;

    @Value("${oidc.aud}")
    private String aud;

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final UserWithdrawalRepository userWithdrawalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoOauthHelper kakaoOauthHelper;
    private final JwtOIDCProvider jwtOIDCProvider;

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
        // 아이디 중복 확인
        IsAvailable usernameAvailable = verifyUsernameOverlap(request.getUsername());
        if (usernameAvailable.equals(IsAvailable.UNAVAILABLE)) {
            throw new ExceptionHandler(ErrorStatus.USERNAME_ALREADY_EXIST);
        }

        Users newUser = UserConverter.toUsers(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));
        // 일상 카테고리 생성
        createDefaultCategory(newUser);
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public UserResponseDTO.IsPendingResultDTO checkIsPending() {
        Long UserId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(UserId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        boolean isPending = user.getStatus().equals(UserStatus.PENDING);
        return UserResponseDTO.IsPendingResultDTO.builder()
                .isPending(isPending)
                .build();
    }

    // open id 인증 완료 한 후 1. 로그인을 시키거나, 2. 가입 대기상태의 유저로 임시 회원가입 처리
    @Override
    @Transactional
    public UserResponseDTO.UserKaKaoOpenIdResultDTO openIdKakao(String openId) {

        // 공개키 가져오기
        OIDCPublicKeyResponse oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();
        // 페이로드 검증 && 서명 후 sub 값 추출
        OIDCDecodePayload oidcDecodePayload = oauthOIDCHelper.getPayloadFromIdToken(openId, iss, aud, oidcPublicKeysResponse);
        String sub = oidcDecodePayload.getSub();


        // sub가 없다면 에러처리
        if (sub == null || sub.isEmpty()) {
            throw new ExceptionHandler(INVALID_OPEN_ID);
        }

        Optional<Users> userSub = userRepository.findByProviderId(sub);

        // sub 추출 완료
        // sub의 user 가 존재한다면 -> 로그인 처리 => access token, refresh token 리턴
        if (userSub.isPresent()) {
            Users ExistUser = userSub.get();
            String key = ExistUser.getId().toString();
            String accessToken = generateAccessToken(userSub.get().getId(), accessExpTime);
            String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);
            Boolean isRegistered = ExistUser.getStatus().equals(UserStatus.PENDING) ? false : true;

            return UserConverter.toUserKaKaoOpenIdResultDTO(isRegistered, accessToken, refreshToken, userSub.get());
        }
        // sub가 잘 추출되었지만, 회원 db에 없어 임시 회원가입 처리 해야하는 부분
        else {
            // 새로운 유저 생성
            Users newUser = UserConverter.toKakaoUsers(sub);

            // 회원 정보 db에 저장
            Users savedUser = userRepository.save(newUser);

            // 액세스 토큰 및 리프레시 토큰 생성
            String key = "users:" + savedUser.getId().toString();
            String accessToken = generateAccessToken(savedUser.getId(), accessExpTime);
            String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);
            return UserConverter.toUserKaKaoOpenIdResultDTO(false, accessToken, refreshToken, savedUser);
        }
    }

    // Nickname 입력받고 회원 상태 업데이트
    // 여기서 일상 카테고리 만들어도 될듯!
    @Override
    @Transactional
    public UserResponseDTO.KaKaoFinalSignUpResultDTO kakaoSignUp(UserRequestDTO.SignUpKakaoRequestDTO request) {
        
        Long userId = SecurityUtil.getCurrentUserId();
        Users savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        // 해당 닉네임을 업데이트
        savedUser.updateNickname(request.getNickname());
        // 유저의 상태 pending -> activate 업데이트
        savedUser.updateStatus(UserStatus.ACTIVE);
        // 일상 카테고리 생성
        createDefaultCategory(savedUser);

        // 액세스 토큰 및 리프레시 토큰 생성
        String key = "users:" + savedUser.getId().toString();
        String accessToken = generateAccessToken(savedUser.getId(), accessExpTime);
        String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);
        return UserConverter.toUserKaKaoFinalSignUpResultDTO(accessToken, refreshToken, savedUser);
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

        Users getUser = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));
        String key = "users:" + getUser.getId().toString();
        String accessToken = generateAccessToken(getUser.getId(), accessExpTime);
        String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);

        return UserConverter.toUserSignInResultDTO(getUser, accessToken, refreshToken);
    }

    @Override
    public UserResponseDTO.RefreshResultDTO refresh(String refreshToken) {
        Long userId = SecurityUtil.getCurrentUserId();
        String key = "users:" + userId.toString();
        String accessToken;
        String newRefreshToken;

        // 전달된 refresh token과 redis의 refresh token비교
        String getRefreshTokenFromRedis = redisTemplate.opsForValue().get(key);
        System.out.println("userId: " + userId);
        System.out.println("redis에서 가져온 refreshToken: " + getRefreshTokenFromRedis);
        if (refreshToken.equals(getRefreshTokenFromRedis)) {
            accessToken = generateAccessToken(userId, accessExpTime);
            newRefreshToken = generateAndSaveRefreshToken(key, refreshExpTime);
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

    @Override
    public void logout(String accessToken) {
        // 로그아웃시킬 회원의 refresh token redis에서 삭제
        Long userId = SecurityUtil.getCurrentUserId();
        String key = "users:" + userId.toString();
        redisTemplate.delete(key);

        // 로그아웃시킬 회원의 access token redis의 블랙리스트로 저장
        key = "blackList:" + userId.toString();
        long tokenRemainTimeSecond = jwtUtils.tokenRemainTimeSecond(accessToken);
        redisTemplate.opsForValue().set(key, accessToken, tokenRemainTimeSecond, TimeUnit.SECONDS);

    }

    // 미구현
//    @Override
//    public Users signInKakao(UserRequestDTO.SignInKakaoRequestDTO request) {
//        // 서비스 구현
//        return null;
//    }

    @Override
    public UserResponseDTO.UserInfoResultDTO getUserInfo() {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        long diaryCount = diaryRepository.countByUsers(user);

        return UserConverter.toUserInfoResultDTO(user, diaryCount);
    }

    @Override
    public Users editUserInfo(UserRequestDTO.EditRequestDTO request) {
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
    @Transactional
    public UserResponseDTO.UserDeleteResultDTO deleteUser(UserRequestDTO.DeleteRequestDTO request) {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        LocalDateTime withdrawnAt = LocalDateTime.now();    // 탈퇴일시
        Integer totalDiary = Math.toIntExact(diaryRepository.countByUsers(user));   // 전체 일기 수

        // 가입일 - 탈퇴일 활동일수 (일수로)
        LocalDate joined = user.getCreatedAt().toLocalDate();
        int activeDays = (int) ChronoUnit.DAYS.between(joined, withdrawnAt.toLocalDate()) + 1;

        // 탈퇴 정보 저장
        UsersWithdrawals withdrawal = UsersWithdrawals.builder()
                .reason(request.getReason())
                .withdrawnAt(withdrawnAt)
                .activeDays(activeDays)
                .totalDiary(totalDiary)
                .build();
        UsersWithdrawals savedWithdrawal = userWithdrawalRepository.save(withdrawal);

        userRepository.delete(user);

        return UserResponseDTO.UserDeleteResultDTO.builder()
                .id(savedWithdrawal.getId())
                .reason(savedWithdrawal.getReason())
                .withdrawnAt(savedWithdrawal.getWithdrawnAt())
                .activeDays(savedWithdrawal.getActiveDays())
                .totalDiary(savedWithdrawal.getTotalDiary())
                .build();
    }

    @Override
    public void sendMail(String email) {
        // 인증 번호 (6자리 난수) 생성
        int verificationCode = (int)(Math.random() * 899999) + 100000;

        // 이메일 내용 설정
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setSubject("[따따] 이메일 인증 코드 발송");  // 메일 제목
            messageHelper.setTo(email);  // 수신자 이메일
            messageHelper.setFrom("2025ttatta@gmail.com");  // 발신자 이메일

            // 템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);

            String html = templateEngine.process("verification-email", context);
            messageHelper.setText(html, true);

            // 템플릿에 들어가는 이미지 cid로 삽입
            messageHelper.addInline("image", new ClassPathResource("img/ttatta_logo.png"));

            // 인증번호 Redis 저장 (유효시간 10분)
            redisTemplate.opsForValue().set(email, String.valueOf(verificationCode), 10, TimeUnit.MINUTES);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // 인증번호 삭제
        redisTemplate.delete(inputEmail);
    }

    @Override
    public void sendVerificationMailFindId(UserRequestDTO.SendVerificationMailFindIdRequestDTO request) {
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

    @Override
    public void verifyUsername(String username) {
        // 아이디 존재 여부 확인
        if(!userRepository.existsByUsername(username)) {
            throw new ExceptionHandler(ID_NOT_FOUND);
        }
    }

    @Override
    public void sendVerificationMailFindPw(UserRequestDTO.SendVerificationMailFindPwRequestDTO request) {
        String inputId = request.getUsername();
        String inputName = request.getName();
        String inputEmail = request.getEmail();

        // 아이디 존재 여부 확인
        if(!userRepository.existsByUsername(inputId)) {
            throw new ExceptionHandler(ID_NOT_FOUND);
        }

        // 이메일로 유저 찾기
        Users user = userRepository.findByEmail(inputEmail)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 이메일와 이름, 아이디 일치 여부 확인
        if(!user.getName().equals(inputName)) {
            throw new ExceptionHandler(NAME_NOT_EQUAL);
        }
        else if(!user.getUsername().equals(inputId)) {
            throw new ExceptionHandler(ID_NOT_EQUAL);
        }

        sendMail(inputEmail);
    }

    @Override
    @Transactional
    public void findPw(UserRequestDTO.FindPwRequestDTO request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // 입력된 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ExceptionHandler(SAME_PASSWORD);
        }

        // 새 비밀번호 암호화 후 업데이트
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    public Long getUserPoint() {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return user.getPoint();
    }

    private String generateAccessToken(Long userId, int accessExpTime) {
        // 인증 완료 후 jwt토큰(accessToken) 생성
        Map<String, Object> valueMap = Map.of(
                "userId", userId // String으로 저장??? 그래서 SecurityUtil에서 Long으로 타입변환 해주나?
        );
        return jwtUtils.generateToken(valueMap, accessExpTime);
    }

    private String generateAndSaveRefreshToken(String key, int refreshExpTime) {
        // 인증 완료 후 jwt토큰(refreshToken) 생성
        String refreshToken = jwtUtils.generateToken(Collections.emptyMap(), refreshExpTime);
        redisTemplate.opsForValue().set(key, refreshToken, refreshExpTime, TimeUnit.MINUTES);
        return refreshToken;
    }

    @Override
    public void deleteUserByAdmin(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO.SetPinResultDTO setPin(UserRequestDTO.SetPinRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        String hashedPin = passwordEncoder.encode(request.getPin());
        user.updatePinHash(hashedPin);

        userRepository.save(user);

        return UserResponseDTO.SetPinResultDTO.builder()
                .pinHash(user.getPinHash())
                .build();
    }

    @Override
    public UserResponseDTO.ChangePinResultDTO changePin(UserRequestDTO.ChangePinRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        if(user.getPinHash() == null || user.getPinHash().isEmpty()) {
            throw new ExceptionHandler(PIN_HASH_NOT_FOUND);
        }

        // 새 핀으로 업데이트
        String hashedNewPin = passwordEncoder.encode(request.getNewPin());
        user.updatePinHash(hashedNewPin);

        userRepository.save(user);

        return UserResponseDTO.ChangePinResultDTO.builder()
                .newPinHash(user.getPinHash())
                .build();
    }

    @Override
    public UserResponseDTO.GetPinResultDTO getPin() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        if(user.getPinHash() == null || user.getPinHash().isEmpty()) {
            throw new ExceptionHandler(PIN_HASH_NOT_FOUND);
        }

        return UserResponseDTO.GetPinResultDTO.builder()
                .pinHash(user.getPinHash())
                .build();
    }

    @Override
    public void deletePin() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        if(user.getPinHash() == null || user.getPinHash().isEmpty()) {
            throw new ExceptionHandler(PIN_HASH_NOT_FOUND);
        }

        user.updatePinHash(null);
        userRepository.save(user);
    }
}

