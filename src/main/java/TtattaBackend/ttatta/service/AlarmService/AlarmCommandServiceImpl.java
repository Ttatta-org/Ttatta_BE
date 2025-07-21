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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private static final LocalTime DEFAULT_ALARM_TIME = LocalTime.of(22, 0);
    private static final LocalTime STANDARD_SETTING_ALARM_TIME = LocalTime.of(3, 0); // 새벽 3시에 모든 알림 예약

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
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser);

        if (getUser.getFcmToken() == null || getUser.getFcmToken().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_TOKEN_NOT_FOUND);
        }
        if (writingDiaryAlarmRepository.findByUsers(getUser) == null) {
            getWrittingDiaryAlarm = writingDiaryAlarmRepository.save(
                    WrittingDiaryAlarm.builder()
                            .users(getUser)
                            .alaramTime(DEFAULT_ALARM_TIME)
                            .isActive(IsActive.ON)
                            .build()
            );
        } else {
            getWrittingDiaryAlarm.updateIsActive(IsActive.ON);
        }
        if (getWrittingDiaryAlarm.getAlaramTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
            System.out.println("저장된 알림 시간: " + getWrittingDiaryAlarm.getAlaramTime());
            LocalDateTime ALARM_TIME = getAlarmLocalDateTime(getWrittingDiaryAlarm.getAlaramTime());
            reserveSendPushNotificationByFcm(ALARM_TIME, getUser, AlaramType.WRITE_DIARY);
        }

        return AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO.builder()
                .alarmTime(getWrittingDiaryAlarm.getAlaramTime())
                .build();
    }

    // 매일 새벽 3시 on 알림들 예약하는 메소드 실행
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void scheduleDailyAlarm() {
        for (WrittingDiaryAlarm writtingDiaryAlarm : writingDiaryAlarmRepository.findAllByIsActiveUsingFetchJoin(IsActive.ON)) {
            System.out.println("Alarm Time: " + writtingDiaryAlarm.getAlaramTime());
            if (writtingDiaryAlarm.getAlaramTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
                LocalDateTime alarmTime = getAlarmLocalDateTime(writtingDiaryAlarm.getAlaramTime());
                reserveSendPushNotificationByFcm(alarmTime, writtingDiaryAlarm.getUsers(), AlaramType.WRITE_DIARY);
            }
        }
    }

    private LocalDateTime getAlarmLocalDateTime(LocalTime alarmTime) {
        if (alarmTime.isAfter(STANDARD_SETTING_ALARM_TIME)) {
            // 새벽 3시 이후 알림인 경우
            return LocalDateTime.of(
                    LocalDate.now(),
                    alarmTime
            );
        } else {
            // 새벽 3시 이전 알림인 경우
            return LocalDateTime.of(
                    LocalDate.now().plusDays(1),
                    alarmTime
            );
        }
    }

    private void reserveSendPushNotificationByFcm(LocalDateTime alarmTime, Users user, AlaramType alaramType) {
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                if (scheduledTasks.containsKey(user.getId())) {
                    // 이미 예약된 알림이 있는 경우, 기존 예약 취소
                    scheduledTasks.get(user.getId()).cancel(false);
                    scheduledTasks.remove(user.getId());
                }
                fcmPushSender.sendPushNotification(user.getFcmToken(), alaramType);
            } finally {
                scheduledTasks.remove(user.getId()); // 실행 후 Map에서 예약 알림 제거
            }
        }, alarmTime.toInstant(KST_OFFSET));
        scheduledTasks.put(user.getId(), future); // 이후에 ScheduledFuture를 이용한 task 업데이트를 위해 userId를 key로 ScheduledFuture를 Map에 value로 저장
    }

    @Override
    public void updateWrittingDiaryAlarm(AlarmRequestDTO.UpdateWritingAlarmRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser);
        System.out.println("저장된 알림 시간: " + getWrittingDiaryAlarm.getAlaramTime());

        if (getWrittingDiaryAlarm == null) {
            throw new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND);
        }
        // 알림 시간 업데이트
        getWrittingDiaryAlarm.updateAlarmTime(request.getAlarmTime());
        writingDiaryAlarmRepository.save(getWrittingDiaryAlarm);
        // 기존 예약된 알림 취소
        if (scheduledTasks.containsKey(getUser.getId())) {
            // 이미 예약된 알림이 있는 경우, 기존 예약 취소
            scheduledTasks.get(getUser.getId()).cancel(false);
            scheduledTasks.remove(getUser.getId());
        }
        // 새 알림 예약
        if (request.getAlarmTime().isAfter(LocalTime.now())) {
            LocalDateTime alarmTime = getAlarmLocalDateTime(getWrittingDiaryAlarm.getAlaramTime());
            reserveSendPushNotificationByFcm(alarmTime, getUser, AlaramType.WRITE_DIARY);
        }
    }

    @Override
    public void deleteWrittingDiaryAlarm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser);

        if (getWrittingDiaryAlarm == null) {
            throw new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND);
        }
        // 알림 비활성화
        getWrittingDiaryAlarm.updateIsActive(IsActive.OFF);
        writingDiaryAlarmRepository.save(getWrittingDiaryAlarm);
        // 기존 예약된 알림 취소
        if (scheduledTasks.containsKey(getUser.getId())) {
            scheduledTasks.get(getUser.getId()).cancel(false);
            scheduledTasks.remove(getUser.getId());
        }
    }
}
