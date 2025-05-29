package TtattaBackend.ttatta.service.UserService;

import TtattaBackend.ttatta.domain.enums.UserStatus;
import TtattaBackend.ttatta.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PendingUserCleanUpService {
    private final UserRepository userRepository;

    public PendingUserCleanUpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 매일 00:00에 실행 (원하는 주기/시간으로 조정 가능)
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredPendingUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        userRepository.deleteByStatusAndCreatedAtBefore(UserStatus.PENDING, cutoff);
        // 필요하면 삭제된 개수를 로그로 남겨도 좋습니다.
    }
}
