package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.enums.IsActive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySummaryAlarmOnResponseDTO {
        LocalTime alarmTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllAlarmsResponseDTO {
        WritingDiaryAlarm writingDiaryAlarm;
        MemoryDiaryAlarm memoryDiaryAlarm;
        ChallengeRemindAlarm challengeRemindAlarm;
        DailySummaryAlarm dailySummaryAlarm;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WritingDiaryAlarm {
        IsActive isActive;
        LocalTime alarmTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryDiaryAlarm {
        IsActive isActive;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeRemindAlarm {
        IsActive isActive;
        String hoursAgo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySummaryAlarm {
        IsActive isActive;
        LocalTime alarmTime;
    }
}
