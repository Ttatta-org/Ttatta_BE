package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;

public interface AlarmCommandService {
    void saveFcmToken(AlarmRequestDTO.GetFcmTokenRequestDTO request);
}
