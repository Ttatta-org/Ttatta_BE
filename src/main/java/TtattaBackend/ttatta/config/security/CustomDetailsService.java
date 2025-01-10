package TtattaBackend.ttatta.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomDetailsService {
    UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException;
}
