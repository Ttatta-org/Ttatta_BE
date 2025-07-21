package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;

public interface AlarmCommandService {
    void saveFcmToken(AlarmRequestDTO.GetFcmTokenRequestDTO request);
    AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO sendPushNotificationByFcm();
    void updateWrittingDiaryAlarm(AlarmRequestDTO.UpdateWritingAlarmRequestDTO request);
}
