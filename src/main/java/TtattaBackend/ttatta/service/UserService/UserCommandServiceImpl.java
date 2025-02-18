package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.DiaryCategoryConverter;
import TtattaBackend.ttatta.converter.UserConverter;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.*;
import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.oidc.*;
import TtattaBackend.ttatta.repository.DiaryCategoryRepository;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.DiaryCategoryRequestDTO;
import TtattaBackend.ttatta.web.dto.UserRequestDTO;
import TtattaBackend.ttatta.web.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Value("${jwt.ACCESS_EXP_TIME}")
    private int accessExpTime;
    @Value("${jwt.REFRESH_EXP_TIME}")
    private int refreshExpTime;

    @Value("${oidc.iss}")
    private String iss;

    @Value("{oidc.aud}")
    private String aud;

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
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
        Users newUser = UserConverter.toUsers(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));
        // 일상 카테고리 생성
        createDefaultCategory(newUser);
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public Users signUpKakao(UserRequestDTO.SignUpKakaoRequestDTO request) {

        // openId를 통해 sub 추출하기
        OIDCPublicKeyResponse oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();
        OIDCDecodePayload oidcDecodePayload = oauthOIDCHelper.getPayloadFromIdToken(request.getOpenId(), iss, aud, oidcPublicKeysResponse);
        String sub = oidcDecodePayload.getSub();

        String accessToken = "";
        String refreshToken = "";

        Users newUser = UserConverter.toKakaoUsers(request, sub);
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

        Users getUser = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));
        String key = "users:" + getUser.getId().toString();
        String accessToken = generateAccessToken(getUser.getId(), accessExpTime);
        String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);

        return UserConverter.toUserSignInResultDTO(getUser, accessToken, refreshToken);
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
    public void deleteUser() {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO.TokenValidationResultDTO validateToken(UserRequestDTO.tokenValidationRequestDTO request) {
        // 공개키 가져오기
        OIDCPublicKeyResponse oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();

        // 페이로드 검증 && 서명 검증 후 sub 값 기준으로 회원가입 or 로그인 처리
        OIDCDecodePayload oidcDecodePayload = oauthOIDCHelper.getPayloadFromIdToken(request.getOpenId(), iss, aud, oidcPublicKeysResponse);
        String sub = oidcDecodePayload.getSub();

        if (sub == null || sub.isEmpty()) {
            return new UserResponseDTO.TokenValidationResultDTO(false, "access token", "refresh token");
        }

        Optional<Users> userSub = userRepository.findByProviderId(sub);

        if (userSub.isPresent()) {
            // 사용자가 이미 존재하면 로그인 처리 (토큰 반환)
            Users user = userSub.get();

            // 액세스 토큰 및 리프레시 토큰 생성
            String accessToken = "";
            String refreshToken = "";

            return new UserResponseDTO.TokenValidationResultDTO(true, accessToken, refreshToken);
        } else {
            return new UserResponseDTO.TokenValidationResultDTO(true, null, null);
        }
    }

    @Override
    public Long getUserPoint() {
        Users user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return user.getPoint();
    }
}

