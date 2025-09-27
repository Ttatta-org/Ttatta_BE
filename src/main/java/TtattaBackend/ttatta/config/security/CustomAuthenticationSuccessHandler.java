package TtattaBackend.ttatta.config.security;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.UserRole;
import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        // 로그인 성공 후 처리할 로직을 여기에 작성합니다.
        System.out.println("로그인 성공: " + authentication.getName());
        // User의 Role을 포함한 JWT 토큰 생성
        Users getUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다: " + authentication.getName()));
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("userId", getUser.getId());
        valueMap.put("role", getUser.getRole());
        String accessToken = jwtUtils.generateToken(valueMap, 15);
        // JWT 토큰을 HTTP 응답 헤더에 추가
        var cookie = org.springframework.http.ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true).secure(true).sameSite("Lax").path("/")
                .maxAge(java.time.Duration.ofMinutes(30)).build();
        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        if (getUser.getRole().equals(UserRole.ADMIN)) response.sendRedirect("/admin/location-log");
        else if (getUser.getRole().equals(UserRole.SUPER_ADMIN)) response.sendRedirect("/super/admin/home");
        else response.sendRedirect("/admin/login?error=insufficient_role"); // 관리자 권한이 없는 경우: 로그인 페이지로 재이동 (사유 전달)
    }
}
