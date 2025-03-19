package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.LoginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private Users savedUser;

    @BeforeEach
    void setUp() {
        this.savedUser = userRepository.save(
                Users.builder()
                        .name("testName")
                        .nickname("testNick")
                        .username("testUsername")
                        .password("testPassword")
                        .loginType(LoginType.REGULAR)
                        .email("test@gmail.com")
                        .profileImage("testProfileImage")
                        .point(1000L)
                        .providerId("providerTest")
                        .build()
        );  // 테스트용 유저 생성
    }

    @Test
    void findByUsername() {
        // when
        Optional<Users> foundUser = userRepository.findByUsername(savedUser.getUsername());

        // then
        assertThat(foundUser).isPresent(); // 회원이 존재하는지
        assertThat(foundUser.get().getUsername()).isEqualTo(savedUser.getUsername()); // 회원이름이 동일한지
    }

    @Test
    void existsByEmail() {
        // when
        boolean exists = userRepository.existsByEmail(savedUser.getEmail());

        // then
        assertThat(exists).isTrue(); // 이메일이 존재하는지
    }

    @Test
    void findByEmail() {
        // when
        Optional<Users> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // then
        assertThat(foundUser).isPresent(); // 회원이 존재하는지
        assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail()); // 이메일이 동일한지
    }

    @Test
    void existsByUsername() {
        // when
        boolean exists = userRepository.existsByUsername(savedUser.getUsername());

        // then
        assertThat(exists).isTrue(); // 회원이 존재하는지
    }

    @Test
    void findByProviderId() {
        // when
        Optional<Users> foundUser = userRepository.findByProviderId(savedUser.getProviderId());

        // then
        assertThat(foundUser).isPresent(); // 회원이 존재하는지
        assertThat(foundUser.get().getProviderId()).isEqualTo(savedUser.getProviderId()); // 제공자 ID가 동일한지
    }
}
