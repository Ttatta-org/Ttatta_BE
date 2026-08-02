package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.LoginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private Users savedUser;
    private static Users testUser;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .name("testName")
                .nickname("testNick")
                .username("testUsername")
                .password("testPassword")
                .loginType(LoginType.REGULAR)
                .email("test@gmail.com")
                .profileImage("testProfileImage")
                .point(1000L)
                .providerId("providerTest")
                .diariesList(new ArrayList<>())
                .diaryCategoriesList(new ArrayList<>())
                .ownItemsList(new ArrayList<>())
                .challengesList(new ArrayList<>())
                .build();

        this.savedUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signUpTest() {
        // given-----------------------------------------------------------------------------------------
        // static testUser 사용

        // when------------------------------------------------------------------------------------------
        // UserRepository에 저장
        // Users saveUser = userRepository.save(testUser);

        // then------------------------------------------------------------------------------------------
        //Answer
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getNickname().equals(testUser.getNickname()));
        assertThat(savedUser.getUsername().equals(testUser.getUsername()));
        assertThat(savedUser.getPassword().equals(testUser.getPassword()));
        assertThat(savedUser.getLoginType().equals(LoginType.REGULAR));
        assertThat(savedUser.getEmail().equals(testUser.getEmail()));
        assertThat(savedUser.getProfileImage().equals(testUser.getProfileImage()));
        assertThat(savedUser.getPoint().equals(testUser.getPoint()));
        assertThat(savedUser.getProviderId().equals(testUser.getProviderId()));
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
