package TtattaBackend.ttatta.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AlarmResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrittingDiaryAlarmOnResponseDTO {
        LocalTime alarmTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeRemindAlarmOnResponseDTO {
        String hoursAgo;
    }
}
