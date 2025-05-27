package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveFcmToken(AlarmRequestDTO.GetFcmTokenRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        getUser.updateFcmToken(request.getFcmToken());
    }
}
