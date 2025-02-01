package TtattaBackend.ttatta.config.security;

import TtattaBackend.ttatta.jwt.JwtUtils;
import TtattaBackend.ttatta.jwt.exception.CustomExpiredJwtException;
import TtattaBackend.ttatta.jwt.exception.CustomJwtException;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j //???
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //@Value("${jwt.JWT_HEADER}")
    private String jwtHeader = "Authorization";
    private static final String[] whitelist = {
            "/users/signup",
            "/users/signup/**",
            "/users/signin",
            "/users/signin/**",
            "/users/testuser",
            "/swagger-ui/**",
            "/v3/**"
//            "/refresh", "/",
//            "/index.html"
    };
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    private static void checkAuthorizationHeader(String header) {
        if(header == null) {
            throw new CustomJwtException("토큰이 전달되지 않았습니다");
        } else if (!header.startsWith("Bearer ")) {
            throw new CustomJwtException("Bearer 로 시작하지 않는 올바르지 않은 토큰 형식입니다");
        }
    }

    // 필터를 거치지 않을 URL(로그인, 회원가입) 을 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("--------------------------- JwtVerifyFilter ---------------------------");

        String requestUri = request.getRequestURI();
        String authHeader = request.getHeader(jwtHeader);
        String refreshToken = request.getHeader("refreshToken");

        // 특정 경로에서만 Refresh Token 처리
        if ("/users/refresh".equals(requestUri)) { // if문 마지막에 return하지 말고 refresh token검증하는 로직을 service로 옮겨야 하나???
            if (refreshToken != null) {
                try {
                    // access token 검증
                    checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
                    String token = JwtUtils.getTokenFromHeader(authHeader);
                    Claims claims = jwtUtils.validateTokenOnlySignature(token); // 토큰 검증
                    Authentication authentication = jwtUtils.getAuthentication(token); // 사용자 인증 정보 생성
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 사용자 인증 정보 저장
                    // refresh token 검증
                    jwtUtils.validateRefreshToken(refreshToken); // 토큰 검증
                    filterChain.doFilter(request, response);    // 다음 필터로 이동
                } catch (Exception e) {
                    Gson gson = new Gson();
                    String json = "";
                    json = gson.toJson(Map.of("error", e.getMessage()));
                }
            }
            return;
        }

        try {
            checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
            String token = JwtUtils.getTokenFromHeader(authHeader);
            jwtUtils.validateToken(token); // 토큰 검증
            jwtUtils.isExpired(token); // 토큰 만료 검증

            Authentication authentication = jwtUtils.getAuthentication(token); // 사용자 인증 정보 생성
            log.info("authentication = {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);    // 다음 필터로 이동
        } catch (Exception e) {
            Gson gson = new Gson();
            String json = "";
            if (e instanceof CustomExpiredJwtException) {
                json = gson.toJson(Map.of("Token_Expired", e.getMessage()));
            } else {
                json = gson.toJson(Map.of("error", e.getMessage()));
            }

            response.setContentType("application/json; charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(json);
            printWriter.close();
        }
    }
}
