package TtattaBackend.ttatta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3); // 스레드 풀 크기 설정
        scheduler.setThreadNamePrefix("scheduled-task-"); // 스레드 이름 접두사 설정
        scheduler.initialize();
        return scheduler;
    }
}
