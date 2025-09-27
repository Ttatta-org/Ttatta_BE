package TtattaBackend.ttatta.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 추가해주어야함.
                // 폼 로그인 비활성화
                .formLogin( (formLogin) -> formLogin
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/loginProc")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin")
                        .successHandler(customAuthenticationSuccessHandler)
//                        .failureHandler(customFailureHandler)
                        .permitAll()
                )
                .logout( (logout) -> logout
                        .logoutUrl("/logoutProc")
                        .logoutSuccessUrl("/admin/login")
                        .permitAll()
                )
                // HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션을 Stateless로 설정
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/admin/**").hasRole("ADMIN")    // == hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/**").permitAll()
//                        .requestMatchers(
//                                "/users/signup",
//                                "/users/signup/**",
//                                "/users/signin",
//                                "/users/signin/**",
//                                "/users/testuser",
//                                "/users/find/**",
//                                "/swagger-ui/**",
//                                "/v3/**",
//                                "/items",
//                                "/users/verificate/kakao",
//                                "/users/admin/**",
//                                "/admin/login",
//                                "/loginProc"
//                        ).permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
