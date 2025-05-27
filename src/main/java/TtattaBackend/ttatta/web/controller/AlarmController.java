package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.service.AlarmService.AlarmCommandService;
import TtattaBackend.ttatta.web.dto.AlarmRequestDTO;
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
}
