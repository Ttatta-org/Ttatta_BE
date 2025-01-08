package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    @Override
    public Users createTestUser() {
        Users newUser = Users.builder()
                .nickname("testName")
                .username("testId")
                .password("testPassword")
                .loginType(LoginType.REGULAR)
                .email(LocalDateTime.now() + "@test.com")
                .phoneNumber("010-0000-0000")
                .profileImage("testProfileImage")
                .gender(Gender.MALE)
                .point(0L)
                .status(UserStatus.ACTIVE)
                .inactiveDate(null)
                .build();

        return userRepository.save(newUser);
    }
}
