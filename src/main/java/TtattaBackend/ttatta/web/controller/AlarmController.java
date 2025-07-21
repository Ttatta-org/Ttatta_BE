package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.service.AlarmService.AlarmCommandService;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {
    private final AlarmCommandService alarmCommandService;

    // 앱에서 생성한 토큰 서버에 저장
    @Operation(summary = "FCM 토큰 저장 및 갱신 api",
            description = "FCM 토큰을 저장 및 갱신하는 api입니다.\n"
                    + "header에 access token을 넣어주세요.")
    @PostMapping("/token")
    public ApiResponse<Object> saveFcmToken(
            @RequestBody AlarmRequestDTO.GetFcmTokenRequestDTO request
    ) {
        alarmCommandService.saveFcmToken(request);
        return ApiResponse.onSuccess("");
    }

    // 일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능)
    @Operation(summary = "일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능) api",
            description = "일기 작성 알림을 on으로 바꿀 시에 요청하는 api입니다. 사용자가 설정했던 시간에 알림을 예약합니다. 첫 알림 on이라면 default로 오후 10시에 알림이 예약됩니다. \n"
                    + "header에 access token을 넣어주세요.")
    @PostMapping("/write/diary/on")
    public ApiResponse<AlarmResponseDTO.WrittingDiaryAlarmOnResponseDTO> writeDiaryAlarmOn() {
        return ApiResponse.onSuccess(
                alarmCommandService.sendPushNotificationByFcm()
        );
    }

    // 일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능)
    @Operation(summary = "일기 작성 알림 시간 변경 (일기 작성 알림이 on일때) api",
            description = "일기 작성 알림 시간 변경 (일기 작성 알림이 on일때) api입니다. \n"
                    + "header에 access token을 넣어주세요.")
    @PatchMapping("/write/diary/on")
    public ApiResponse<?> updateWriteDiaryAlarm(
            @RequestBody AlarmRequestDTO.UpdateWritingAlarmRequestDTO request
    ) {
        alarmCommandService.updateWrittingDiaryAlarm(request);
        return ApiResponse.onSuccess("");
    }

    // 일기 작성 알림 on (일기 작성 알림 시간 설정 및 변경 가능)
    @Operation(summary = "일기 작성 알림 off api",
            description = "일기 작성 알림 off api api입니다. \n"
                    + "header에 access token을 넣어주세요.")
    @PatchMapping("/write/diary/off")
    public ApiResponse<?> deleteWriteDiaryAlarm() {
        alarmCommandService.deleteWrittingDiaryAlarm();
        return ApiResponse.onSuccess("");
    }
}
