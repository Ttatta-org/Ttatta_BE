package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.WrittingDiaryAlarm;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.repository.WritingDiaryAlarmRepository;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {
    private final UserRepository userRepository;
    private final WritingDiaryAlarmRepository writingDiaryAlarmRepository;
    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final FcmPushSender fcmPushSender;
    private static final LocalDateTime DEFAULT_ALARM_TIME = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 22, 0);

    @Override
    @Transactional
    public void saveFcmToken(AlarmRequestDTO.GetFcmTokenRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        getUser.updateFcmToken(request.getFcmToken());
    }

    @Override
    @Transactional
    public AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO sendPushNotificationByFcm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        if (getUser.getFcmToken() == null || getUser.getFcmToken().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_TOKEN_NOT_FOUND);
        }

        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser);
        if (writingDiaryAlarmRepository.findByUsers(getUser) == null) {
            getWrittingDiaryAlarm = writingDiaryAlarmRepository.save(
                    WrittingDiaryAlarm.builder()
                            .users(getUser)
                            .alaramTime(DEFAULT_ALARM_TIME)
                            .isActive(IsActive.ON)
                            .build()
            );
        } else {
            getWrittingDiaryAlarm.setIsActive(IsActive.ON);
        }

        if (LocalDateTime.now().isAfter(getWrittingDiaryAlarm.getAlaramTime())) {
            // 이미 지난 시간인 경우 시간 예약을 하지 않음
            return AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO.builder()
                    .alarmTime(getWrittingDiaryAlarm.getAlaramTime())
                    .build();
        }

        // 예약 알림 설정
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
                    fcmPushSender.sendPushNotification(getUser.getFcmToken());
                },
                getWrittingDiaryAlarm.getAlaramTime().toInstant(KST_OFFSET)
        );
        scheduledTasks.put(userId, future); // 이후에 ScheduledFuture를 이용한 task 업데이트를 위해 userId를 key로 ScheduledFuture를 Map에 value로 저장

        return AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO.builder()
                .alarmTime(getWrittingDiaryAlarm.getAlaramTime())
                .build();
    }
}
