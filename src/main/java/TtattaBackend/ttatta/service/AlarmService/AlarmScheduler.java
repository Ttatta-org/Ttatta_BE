//package TtattaBackend.ttatta.service.AlarmService;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AlarmScheduler {
//
//    private final AlarmCommandService alarmCommandService;
//
//    public AlarmScheduler(AlarmCommandService alarmCommandService) {
//        this.alarmCommandService = alarmCommandService;
//    }
//
//    // 매일 새벽 3시 실행
//    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
//    public void runDailyAlarmScheduler() {
//        alarmCommandService.sendPushNotificationByFcm(); // 서비스 메서드 호출
//    }
//}
