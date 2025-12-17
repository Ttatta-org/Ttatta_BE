package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.AlarmConverter;
import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.domain.enums.MemoryDiaryAlarmStatus;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.repository.*;
import TtattaBackend.ttatta.service.OpenAiService.SummaryCommandService;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {
    private static final LocalTime DEFAULT_ALARM_TIME = LocalTime.of(22, 0);
    private static final LocalTime STANDARD_SETTING_ALARM_TIME = LocalTime.of(3, 0); // 새벽 3시에 모든 알림 예약
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final DiaryRepository diaryRepository;
    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private final TaskScheduler taskScheduler;
    private final FcmPushSender fcmPushSender;
    private final Map<Long, ScheduledFuture<?>> writingDiaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> memoryDiaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> challengeRemindAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> dailySummaryAlarmScheduledTasks = new ConcurrentHashMap<>();
    private final SummaryCommandService summaryCommandService;
    private final WritingDiaryAlarmRepository writingDiaryAlarmRepository;
    private final MemoryDiaryAlarmRepository memoryDiaryAlarmRepository;
    private final ChallengeRemindAlarmRepository challengeRemindAlarmRepository;
    private final DailySummaryAlarmRepository dailySummaryAlarmRepository;

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
    public AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO sendWritingDiaryPushAlarmNotificationByFcm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser).orElse(null);

        if (getUser.getFcmToken() == null || getUser.getFcmToken().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_TOKEN_NOT_FOUND);
        }
        if (getWrittingDiaryAlarm == null) {
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
            if (writtingDiaryAlarm.getAlaramTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
                LocalDateTime alarmTime = getReservationAlarmLocalDateTime(writtingDiaryAlarm.getAlaramTime());
                reserveSendPushNotificationByFcm(alarmTime, writtingDiaryAlarm.getUsers(), AlaramType.WRITE_DIARY, writingDiaryAlarmScheduledTasks);
            }
        }
        for (ChallengeRemindAlarm challengeRemindAlarm : challengeRemindAlarmRepository.findAllByIsActiveUsingFetchJoin(IsActive.ON)) {
            if (challengeRemindAlarm.getAlaramTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
                LocalDateTime alarmTime = getReservationAlarmLocalDateTime(challengeRemindAlarm.getAlaramTime());
                reserveSendPushNotificationByFcm(alarmTime, challengeRemindAlarm.getUsers(), AlaramType.CHALLENGE_REMIND, challengeRemindAlarmScheduledTasks);
            }
        }
        for (DailySummaryAlarm dailySummaryAlarm : dailySummaryAlarmRepository.findAllByIsActiveUsingFetchJoin(IsActive.ON)) {
            if (dailySummaryAlarm.getAlarmTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
                LocalDateTime alarmTime = getReservationAlarmLocalDateTime(dailySummaryAlarm.getAlarmTime());
                reserveSendPushNotificationByFcm(alarmTime, dailySummaryAlarm.getUsers(), AlaramType.DAILY_SUMMARY, dailySummaryAlarmScheduledTasks);
            }
        }
    }

    private LocalDateTime getReservationAlarmLocalDateTime(LocalTime alarmTime) {
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

    private LocalDateTime getAlarmLocalDateTime(LocalTime alarmTime) {
        return LocalDateTime.of(
                LocalDate.now(),
                alarmTime
        );
    }

    private void reserveSendPushNotificationByFcm(LocalDateTime alarmTime, Users user, AlaramType alaramType, Map<Long, ScheduledFuture<?>> scheduledTasks) {
        reserveSendPushNotificationByFcm(alarmTime, user, alaramType, scheduledTasks, null, null);
    }

    private void reserveSendPushNotificationByFcm(LocalDateTime alarmTime, Users user, AlaramType alaramType, Map<Long, ScheduledFuture<?>> scheduledTasks, String memoryDiaryAlarmDaysAgo, Long diaryId) {
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                LocalDate today = LocalDate.now(zoneId);
                List<Diaries> diaries = diaryRepository.findAllByCreatedAtBetween(
                        today.atStartOfDay(),                  // 오늘 00:00:00
                        today.plusDays(1).atStartOfDay()       // 내일 00:00:00
                );

                if (scheduledTasks.containsKey(user.getId())) {
                    // 이미 예약된 알림이 있는 경우, 기존 예약 취소
                    scheduledTasks.get(user.getId()).cancel(false);
                    scheduledTasks.remove(user.getId());
                }
                if (alaramType == AlaramType.DAILY_SUMMARY && diaries.size() < 2) return; // 일일 요약 알림은 오늘 작성한 일기가 2개 미만이면 보내지 않음
                else if (alaramType == AlaramType.DAILY_SUMMARY && diaries.size() >= 2) {
                    summaryCommandService.summarize(
                            DiarySummaryRequestDTO.SummarizeDTO.builder()
                                    .date(today)
                                    .build()
                    );
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
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));

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
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));

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
                                .isActive(IsActive.ON) // 기본값 ON
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

    @Override
    @Transactional
    public AlarmResponseDTO.ChallengeRemindAlarmOnResponseDTO sendChallengeRemindPushAlarmNotificationByFcm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        ChallengeRemindAlarm getChallengeRemindAlarm = challengeRemindAlarmRepository.findByUsers(getUser).orElse(null);

        if (getUser.getFcmToken() == null || getUser.getFcmToken().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_TOKEN_NOT_FOUND);
        }
        if (getChallengeRemindAlarm == null) { // on을 처음한다면 10시로 기본시간 설정
            getChallengeRemindAlarm = challengeRemindAlarmRepository.save(
                    ChallengeRemindAlarm.builder()
                            .users(getUser)
                            .alaramTime(DEFAULT_ALARM_TIME)
                            .isActive(IsActive.ON)
                            .build()
            );
        } else {
            getChallengeRemindAlarm.updateIsActive(IsActive.ON);
        }
        if (getChallengeRemindAlarm.getAlaramTime().isAfter(LocalTime.now()) && challengeRepository.existsByUsersAndIsCompletedFalseAndCreatedAtBetween(getUser, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음, 챌린지가 없으면 알림 예약 x
            LocalDateTime ALARM_TIME = getAlarmLocalDateTime(getChallengeRemindAlarm.getAlaramTime());
            reserveSendPushNotificationByFcm(ALARM_TIME, getUser, AlaramType.CHALLENGE_REMIND, challengeRemindAlarmScheduledTasks);
        }

        return AlarmResponseDTO.ChallengeRemindAlarmOnResponseDTO.builder()
                .hoursAgo(toHoursAgoString(getChallengeRemindAlarm.getAlaramTime()))
                .build();
    }

    @Override
    public void updateChallengeRemindAlarm(AlarmRequestDTO.UpdateChallengeRemindAlarmRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        ChallengeRemindAlarm getChallengeRemindAlarm = challengeRemindAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));
        LocalTime requestAlarmTime = toLocalTime(request.getHoursAgo());

        // 알림 시간 업데이트
        getChallengeRemindAlarm.updateAlarmTime(requestAlarmTime);
        challengeRemindAlarmRepository.save(getChallengeRemindAlarm);
        // 기존 예약된 알림 취소
        if (challengeRemindAlarmScheduledTasks.containsKey(getUser.getId())) {
            // 이미 예약된 알림이 있는 경우, 기존 예약 취소
            challengeRemindAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            challengeRemindAlarmScheduledTasks.remove(getUser.getId());
        }
        // 새 알림 예약
        if (requestAlarmTime.isAfter(LocalTime.now()) && challengeRepository.findByUsers(getUser) != null) { // 챌린지가 없으면 알림 예약 x
            LocalDateTime alarmTime = getAlarmLocalDateTime(getChallengeRemindAlarm.getAlaramTime());
            reserveSendPushNotificationByFcm(alarmTime, getUser, AlaramType.CHALLENGE_REMIND, challengeRemindAlarmScheduledTasks);
        }
    }

    private String toHoursAgoString(LocalTime time) {
        // 몇시간전인지 계산하고 시간을 String으로 반환
        return String.valueOf(24 - time.getHour());
    }

    private LocalTime toLocalTime(String hoursAgoString) {
        // hoursAgoString을 LocalTime으로 변환
        return LocalTime.of(24 - Integer.parseInt(hoursAgoString), 0);
    }

    @Override
    public void deleteChallengeRemindAlarm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        ChallengeRemindAlarm getChallengeRemindAlarm = challengeRemindAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));

        // 알림 비활성화
        getChallengeRemindAlarm.updateIsActive(IsActive.OFF);
        challengeRemindAlarmRepository.save(getChallengeRemindAlarm);
        // 기존 예약된 알림 취소
        if (challengeRemindAlarmScheduledTasks.containsKey(getUser.getId())) {
            challengeRemindAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            challengeRemindAlarmScheduledTasks.remove(getUser.getId());
        }
    }

    @Override
    @Transactional
    public AlarmResponseDTO.DailySummaryAlarmOnResponseDTO sendDailySummaryPushAlarmNotificationByFcm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        if (getUser.getFcmToken() == null || getUser.getFcmToken().isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_TOKEN_NOT_FOUND);
        }
        // 없으면 생성해서 저장, 있으면 그대로 사용
        DailySummaryAlarm getDailySummaryAlarm = dailySummaryAlarmRepository.findByUsers(getUser)
                .orElseGet(() -> dailySummaryAlarmRepository.save(
                        DailySummaryAlarm.builder()
                                .users(getUser)
                                .alarmTime(DEFAULT_ALARM_TIME)
                                .isActive(IsActive.ON)
                                .build()
                )); // orElseGet은 비었을 때만 실행되어 불필요한 save 방지
        // 상태를 ON으로(이미 ON이면 변경 없음)
        if (getDailySummaryAlarm.getIsActive() != IsActive.ON) {
            getDailySummaryAlarm.updateIsActive(IsActive.ON);
        }
        if (getDailySummaryAlarm.getAlarmTime().isAfter(LocalTime.now())) { // 현재 시간보다 이전 알림 시간은 예약하지 않음
            LocalDateTime ALARM_TIME = getAlarmLocalDateTime(getDailySummaryAlarm.getAlarmTime());
            reserveSendPushNotificationByFcm(ALARM_TIME, getUser, AlaramType.DAILY_SUMMARY, dailySummaryAlarmScheduledTasks);
        }

        return AlarmResponseDTO.DailySummaryAlarmOnResponseDTO.builder()
                .alarmTime(getDailySummaryAlarm.getAlarmTime())
                .build();
    }

    @Override
    public void updateDailySummaryAlarm(AlarmRequestDTO.UpdateDailySummaryAlarmRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        DailySummaryAlarm getDailySummaryAlarm = dailySummaryAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));

        // 알림 시간 업데이트
        getDailySummaryAlarm.updateAlarmTime(request.getAlarmTime());
        dailySummaryAlarmRepository.save(getDailySummaryAlarm);
        // 기존 예약된 알림 취소
        if (dailySummaryAlarmScheduledTasks.containsKey(getUser.getId())) {
            // 이미 예약된 알림이 있는 경우, 기존 예약 취소
            dailySummaryAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            dailySummaryAlarmScheduledTasks.remove(getUser.getId());
        }
        // 새 알림 예약
        if (request.getAlarmTime().isAfter(LocalTime.now())) {
            LocalDateTime alarmTime = getAlarmLocalDateTime(getDailySummaryAlarm.getAlarmTime());
            reserveSendPushNotificationByFcm(alarmTime, getUser, AlaramType.DAILY_SUMMARY, dailySummaryAlarmScheduledTasks);
        }
    }

    @Override
    public void deleteDailySummaryAlarm() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        DailySummaryAlarm getDailySummaryAlarm = dailySummaryAlarmRepository.findByUsers(getUser)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ALARM_NOT_FOUND));

        // 알림 비활성화
        getDailySummaryAlarm.updateIsActive(IsActive.OFF);
        dailySummaryAlarmRepository.save(getDailySummaryAlarm);
        // 기존 예약된 알림 취소
        if (dailySummaryAlarmScheduledTasks.containsKey(getUser.getId())) {
            dailySummaryAlarmScheduledTasks.get(getUser.getId()).cancel(false);
            dailySummaryAlarmScheduledTasks.remove(getUser.getId());
        }
    }

    @Override
    public AlarmResponseDTO.GetAllAlarmsResponseDTO getAllAlarms() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users getUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        WrittingDiaryAlarm getWrittingDiaryAlarm = writingDiaryAlarmRepository.findByUsers(getUser).orElse(null);
        MemoryDiaryAlarm getMemoryDiaryAlarm = memoryDiaryAlarmRepository.findByUsers(getUser).orElse(null);
        ChallengeRemindAlarm getChallengeRemindAlarm = challengeRemindAlarmRepository.findByUsers(getUser).orElse(null);
        DailySummaryAlarm getDailySummaryAlarm = dailySummaryAlarmRepository.findByUsers(getUser).orElse(null);

        return AlarmConverter.toGetAllAlarmsResponseDTO(
                getWrittingDiaryAlarm,
                getMemoryDiaryAlarm,
                getChallengeRemindAlarm,
                getChallengeRemindAlarm != null ? toHoursAgoString(getChallengeRemindAlarm.getAlaramTime()) : null,
                getDailySummaryAlarm
        );
    }

    // 매일 자정에 위치기반 알림 쿨타임 차감 on 알림들 예약하는 메소드 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void decreaseMemoryDiaryAlarmCoolTime() {
        List<Diaries> diariesList = diaryRepository.findAll();
        for (Diaries diary : diariesList) {
            int currentCoolTime = diary.getMemoryDiaryAlarmCoolTime();
            if (currentCoolTime > 0) {
                diary.updateMemoryDiaryAlarmCoolTime(currentCoolTime - 1);
            }
        }
    }
}
