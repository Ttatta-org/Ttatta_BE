package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.*;
import TtattaBackend.ttatta.domain.enums.IsActive;
import TtattaBackend.ttatta.web.dto.AlarmResponseDTO;

public class AlarmConverter {
    public static AlarmResponseDTO.GetAllAlarmsResponseDTO toGetAllAlarmsResponseDTO(
            WrittingDiaryAlarm writtingDiaryAlarm,
            MemoryDiaryAlarm memoryDiaryAlarm,
            ChallengeRemindAlarm challengeRemindAlarm,
            String hoursAgo,
            DailySummaryAlarm dailySummaryAlarm
    ) {
        return AlarmResponseDTO.GetAllAlarmsResponseDTO.builder()
                .writingDiaryAlarm(
                        AlarmResponseDTO.WritingDiaryAlarm.builder()
                                .isActive(writtingDiaryAlarm != null ? writtingDiaryAlarm.getIsActive() : IsActive.OFF)
                                .alarmTime(writtingDiaryAlarm != null ? (writtingDiaryAlarm.getIsActive() == IsActive.ON ? writtingDiaryAlarm.getAlaramTime() : null) : null)
                                .build()
                )
                .memoryDiaryAlarm(
                        AlarmResponseDTO.MemoryDiaryAlarm.builder()
                                .isActive(memoryDiaryAlarm != null ? memoryDiaryAlarm.getIsActive() : IsActive.OFF)
                                .build()
                )
                .challengeRemindAlarm(
                        AlarmResponseDTO.ChallengeRemindAlarm.builder()
                                .isActive(challengeRemindAlarm != null ? challengeRemindAlarm.getIsActive() : IsActive.OFF)
                                .hoursAgo(challengeRemindAlarm != null ? (challengeRemindAlarm.getIsActive() == IsActive.ON ? hoursAgo : null) : null)
                                .build()
                )
                .dailySummaryAlarm(
                        AlarmResponseDTO.DailySummaryAlarm.builder()
                                .isActive(dailySummaryAlarm != null ? dailySummaryAlarm.getIsActive() : IsActive.OFF)
                                .alarmTime(dailySummaryAlarm != null ? (dailySummaryAlarm.getIsActive() == IsActive.ON ? dailySummaryAlarm.getAlaramTime() : null) : null)
                                .build()
                )
                .build();
    }
}
