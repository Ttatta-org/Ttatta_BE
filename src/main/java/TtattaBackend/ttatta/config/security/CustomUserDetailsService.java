package TtattaBackend.ttatta.config.security;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements CustomDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofHours(12);

    @Override
    public UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다: " + username));

        if (user.isLockedNow()) {
            Duration remain = Duration.between(LocalDateTime.now(), user.getLockUntil());

            long remainHours = remain.toHours();
            long remainMinutes = remain.toMinutes();

            String message;
            if (remainHours >= 1) {
                message = "비밀번호 " + MAX_ATTEMPTS + "회 오류로 계정 잠긴 상태입니다. 약 " + (remainHours + 1) + "시간 후에 다시 시도해주세요.";
            } else {
                long remainMin = Math.max(1, remainMinutes);
                message = "비밀번호 " + MAX_ATTEMPTS + "회 오류로 계정 잠긴 상태입니다. 약 " + remainMin + "분 후에 다시 시도해주세요.";
            }

            throw new LockedException(message);
        } else if (user.getLockUntil() != null) {
            user.resetLock();
            userRepository.save(user);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            Users refreshed = userRepository.findByUsername(username).orElse(user);
            int attempts = refreshed.getFailedAttempts();
            int currentAttempts = attempts + 1;

            if (currentAttempts >= MAX_ATTEMPTS) {
                refreshed.lockFor(LOCK_DURATION);
                userRepository.save(refreshed);
                throw new LockedException("비밀번호 " + MAX_ATTEMPTS + "회 오류로 계정 잠긴 상태입니다.");
            }

            user.updateFailedAttempts(currentAttempts);
            userRepository.save(user);

            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도: " + (MAX_ATTEMPTS - currentAttempts) + "회)");
        }

        if (user.getFailedAttempts() != 0 || user.getLockUntil() != null) {
            user.resetLock();
            userRepository.save(user);
        }

        User securityUser = new User(
                user.getUsername(),
                "",
                Collections.emptyList()
        );

        return securityUser;
    }
}
