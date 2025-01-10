package TtattaBackend.ttatta.config.security;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements CustomDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Password가 일치하지 않습니다.");
        }

        User securityUser = new User(
                user.getUsername(),
                "",
                Collections.emptyList()
        );

        return securityUser;
    }
}
