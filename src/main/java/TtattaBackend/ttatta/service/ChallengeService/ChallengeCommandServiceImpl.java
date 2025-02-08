package TtattaBackend.ttatta.service.ChallengeService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.ChallengeConverter;
import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.ChallengeRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeCommandServiceImpl implements ChallengeCommandService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public Challenges createChallenge(ChallengeRequestDTO.CreateChallengeRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        // 하루에 생성한 챌린지가 이미 3개인지 확인
        int challengeCount = challengeRepository.countByCreatedAtOn(LocalDateTime.now());
        if(challengeCount == 3) {
            throw new ExceptionHandler(ErrorStatus.CHALLENGE_FULL);
        }

        // 챌린지 생성
        Challenges challenge = ChallengeConverter.toChallenge(request);
        challenge.setUsers(getUser);
        return challengeRepository.save(challenge);
    }
}
