package TtattaBackend.ttatta.service.ChallengeService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.ChallengeRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeQueryServiceImpl implements ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
  
    @Override
    public List<Challenges> getChallenges() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return challengeRepository.findByUsersAndCreatedAtDateOrderByCreatedAtAsc(getUser, LocalDate.now());
    }
  
    @Override
    public List<Challenges> getFailChallenges() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        return challengeRepository.findTop5ByUserAndIsCompletedFalseExcludeTodayOrderByCreatedAtDesc(getUser);
    }
}
