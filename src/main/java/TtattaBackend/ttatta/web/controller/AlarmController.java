package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.service.AlarmService.AlarmCommandService;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {
    private final AlarmCommandService alarmCommandService;

    // 앱에서 생성한 토큰 서버에 저장
    @PostMapping("/token")
    public ApiResponse<Object> saveFcmToken(
            @RequestBody AlarmRequestDTO.GetFcmTokenRequestDTO request
    ) {
        alarmCommandService.saveFcmToken(request);
        return ApiResponse.onSuccess("");
    }

    // 일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능)
    @PostMapping("/write/diary/on")
    public ApiResponse<AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO> writeDiaryAlarmOn() {
        return ApiResponse.onSuccess(
                alarmCommandService.sendPushNotificationByFcm()
        );
    }

    // 일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능)
    @PatchMapping("/write/diary/on")
    public ApiResponse<?> updateWriteDiaryAlarm(
            @RequestBody AlarmRequestDTO.UpdateWritingAlarmRequestDTO request
    ) {
        alarmCommandService.updateWrittingDiaryAlarm(request);
        return ApiResponse.onSuccess("");
    }
}
