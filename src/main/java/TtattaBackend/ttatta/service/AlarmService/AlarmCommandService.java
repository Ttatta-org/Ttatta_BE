package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.MemoryDiaryAlarmStatus;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;

public interface AlarmCommandService {
    void saveFcmToken(AlarmRequestDTO.GetFcmTokenRequestDTO request);
    AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO sendWritingDiaryPushAlarmNotificationByFcm();
    void updateWrittingDiaryAlarm(AlarmRequestDTO.UpdateWritingAlarmRequestDTO request);
    void deleteWrittingDiaryAlarm();
    void setMemoryDiaryAlarmStatus(MemoryDiaryAlarmStatus memoryDiaryAlarmStatus);
    void sendMemoryDiaryAlarm(Users user, String memoryDiaryAlarmDaysAgo, Long diaryId);
    AlarmResponseDTO.ChallengeRemindAlarmOnResponseDTO sendChallengeRemindPushAlarmNotificationByFcm();
    void updateChallengeRemindAlarm(AlarmRequestDTO.UpdateChallengeRemindAlarmRequestDTO request);
    void deleteChallengeRemindAlarm();
    AlarmResponseDTO.DailySummaryAlarmOnResponseDTO sendDailySummaryPushAlarmNotificationByFcm();
    void updateDailySummaryAlarm(AlarmRequestDTO.UpdateDailySummaryAlarmRequestDTO request);
    void deleteDailySummaryAlarm();
    AlarmResponseDTO.GetAllAlarmsResponseDTO getAllAlarms();
}
