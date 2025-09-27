package TtattaBackend.ttatta.config.security;

import TtattaBackend.ttatta.domain.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() { }

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtAuthenticationFilter 의 doFilterInternal 에서 저장
    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw  new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }

    public static UserRole getCurrentUserRole() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null || !authentication.isAuthenticated()) {
            throw  new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        UserRole role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)   // ROLE_ADMIN
                .map(r -> r.replaceFirst("^ROLE_", "")) // ADMIN
                .map(UserRole::valueOf)
                .findFirst()
                .orElse(null);
        return role;
    }
}
