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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRespositoryTest {
    private final UserRepository userRepository;
    private static Users testUser;

    @Autowired
    public UserRespositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signUpTest() {
        // given-----------------------------------------------------------------------------------------
        // static testUser 사용

        // when------------------------------------------------------------------------------------------
        // UserRepository에 저장
        Users saveUser = userRepository.save(testUser);

        // then------------------------------------------------------------------------------------------
        //Answer
        assertThat(saveUser).isNotNull();
        assertThat(saveUser.getNickname().equals(testUser.getNickname()));
        assertThat(saveUser.getUsername().equals(testUser.getUsername()));
        assertThat(saveUser.getPassword().equals(testUser.getPassword()));
        assertThat(saveUser.getLoginType().equals(LoginType.REGULAR));
        assertThat(saveUser.getEmail().equals(testUser.getEmail()));
        assertThat(saveUser.getProfileImage().equals(testUser.getProfileImage()));
        assertThat(saveUser.getPoint().equals(testUser.getPoint()));
        assertThat(saveUser.getProviderId().equals(testUser.getProviderId()));
    }
}
