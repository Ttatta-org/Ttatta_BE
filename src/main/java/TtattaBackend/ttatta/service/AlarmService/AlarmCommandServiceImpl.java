package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.MemoryDiaryAlarm;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.WrittingDiaryAlarm;
import TtattaBackend.ttatta.domain.enums.MemoryDiaryAlarmStatus;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.repository.ChallengeRemindAlarmRepository;
import TtattaBackend.ttatta.repository.MemoryDiaryAlarmRepository;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {
    private final UserRepository userRepository;
    private final WritingDiaryAlarmRepository writingDiaryAlarmRepository;
    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> writingDiaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> memoryDiaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> challengeRemindAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> diarySummaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final FcmPushSender fcmPushSender;
    private static final LocalTime DEFAULT_ALARM_TIME = LocalTime.of(22, 0);
    private static final LocalTime STANDARD_SETTING_ALARM_TIME = LocalTime.of(3, 0); // 새벽 3시에 모든 알림 예약
    private final MemoryDiaryAlarmRepository memoryDiaryAlarmRepository;

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
            reserveSendPushNotificationByFcm(ALARM_TIME, getUser, AlaramType.WRITE_DIARY, writingDiaryAlarmScheduledTasks);
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
                reserveSendPushNotificationByFcm(alarmTime, writtingDiaryAlarm.getUsers(), AlaramType.WRITE_DIARY, writingDiaryAlarmScheduledTasks);
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

    private void reserveSendPushNotificationByFcm(LocalDateTime alarmTime, Users user, AlaramType alaramType, Map<Long, ScheduledFuture<?>> scheduledTasks) {
        reserveSendPushNotificationByFcm(alarmTime, user, alaramType, scheduledTasks, null, null);
    }

    private void reserveSendPushNotificationByFcm(LocalDateTime alarmTime, Users user, AlaramType alaramType, Map<Long, ScheduledFuture<?>> scheduledTasks, String memoryDiaryAlarmDaysAgo, Long diaryId) {
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                if (scheduledTasks.containsKey(user.getId())) {
                    // 이미 예약된 알림이 있는 경우, 기존 예약 취소
                    scheduledTasks.get(user.getId()).cancel(false);
                    scheduledTasks.remove(user.getId());
                }
                fcmPushSender.sendPushNotification(user.getFcmToken(), alaramType, memoryDiaryAlarmDaysAgo, diaryId);
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
        if (writingDiaryAlarmScheduledTasks.containsKey(getUser.getId())) {
            // 이미 예약된 알림이 있는 경우, 기존 예약 취소
            writingDiaryAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            writingDiaryAlarmScheduledTasks.remove(getUser.getId());
        }
        // 새 알림 예약
        if (request.getAlarmTime().isAfter(LocalTime.now())) {
            LocalDateTime alarmTime = getAlarmLocalDateTime(getWrittingDiaryAlarm.getAlaramTime());
            reserveSendPushNotificationByFcm(alarmTime, getUser, AlaramType.WRITE_DIARY, writingDiaryAlarmScheduledTasks);
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
        if (writingDiaryAlarmScheduledTasks.containsKey(getUser.getId())) {
            writingDiaryAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            writingDiaryAlarmScheduledTasks.remove(getUser.getId());
        }
    }

    @Override
    @Transactional
    public void setMemoryDiaryAlarmStatus(MemoryDiaryAlarmStatus memoryDiaryAlarmStatus) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        MemoryDiaryAlarm getMemoryDiaryAlarm = memoryDiaryAlarmRepository.findByUsers(getUser)
                .orElseGet(() -> memoryDiaryAlarmRepository.save(
                        MemoryDiaryAlarm.builder()
                                .users(getUser)
                                .isActive(IsActive.OFF) // 기본값 OFF
                                .build()
                )); // orElseGet은 Optional이 비어 있을 때에만 생성/저장 수행
        if (memoryDiaryAlarmStatus == MemoryDiaryAlarmStatus.ON) {
            // 현재 상태가 OFF 상태인지 확인하는 로직이 필요할까
            getMemoryDiaryAlarm.updateIsActive(IsActive.ON);
        } else if (memoryDiaryAlarmStatus == MemoryDiaryAlarmStatus.OFF) {
            // 현재상태가 ON 상태인지 확인하는 로직이 필요할까
            getMemoryDiaryAlarm.updateIsActive(IsActive.OFF);
        }
    }

    @Override
    public void sendMemoryDiaryAlarm(Users user, String memoryDiaryAlarmDaysAgo, Long diaryId) {
        reserveSendPushNotificationByFcm(LocalDateTime.now(), user, AlaramType.MEMORY_DIARY, memoryDiaryAlarmScheduledTasks, memoryDiaryAlarmDaysAgo, diaryId);
    }
}
