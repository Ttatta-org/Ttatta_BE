//package TtattaBackend.ttatta.service.OpenAiService;
//
//import TtattaBackend.ttatta.domain.DailySummaryAlarm;
//import TtattaBackend.ttatta.domain.Diaries;
//import TtattaBackend.ttatta.repository.DailySummaryAlarmRepository;
//import TtattaBackend.ttatta.repository.DiaryRepository;
//import TtattaBackend.ttatta.web.dto.DiarySummaryRequestDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class SummaryDiaryBatchService {
//    private final DailySummaryAlarmRepository dailySummaryAlarmRepository;
//    private final SummaryCommandService summaryCommandService;
//    private final DiaryRepository diaryRepository;
//
//    @Scheduled(cron = "0 * * * * *")
//    public void processDailySummaryDiary() {
//        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
//
//        List<DailySummaryAlarm> alarms = dailySummaryAlarmRepository.findByAlarmTime(currentTime);
//
//        for (DailySummaryAlarm alarm : alarms) {
//            try {
//                LocalDate today = LocalDate.now();
//                LocalDateTime todayStart = today.atStartOfDay();
//                LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
//
//                List<Diaries> todayDiaries = diaryRepository.findAllByUserIdAndDate(
//                        alarm.getUsers(), todayStart, todayEnd);
//
//                if (todayDiaries.size() < 2) {
//                    log.info("요약 건너뜀 - 사용자 ID: {}, 오늘 일기 수: {}",
//                            alarm.getUsers().getId(), todayDiaries.size());
//                    continue;
//                }
//
//                DiarySummaryRequestDTO.SummarizeDTO request = DiarySummaryRequestDTO.SummarizeDTO.builder()
//                        .date(today)
//                        .build();
//
//                summaryCommandService.summarize(request);
//                log.info("자동 요약 완료 - 사용자 ID: {}, 알람 시간: {}",
//                        alarm.getUsers().getId(), alarm.getAlarmTime());
//            } catch (Exception e) {
//                log.error("자동 요약 실패 - 사용자 ID: {}, 에러: {}",
//                        alarm.getUsers().getId(), e.getMessage());
//            }
//        }
//    }
//}