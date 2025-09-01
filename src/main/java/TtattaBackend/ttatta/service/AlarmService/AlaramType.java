package TtattaBackend.ttatta.service.AlarmService;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlaramType {
    WRITE_DIARY("DIARY_REMINDER", "일기 작성 알림", "지금 일기를 작성해보세요!"),
    MEMORY_DIARY("MEMORY_LOCATION", "위치 기반 추억 회상 알림", " 전 이곳을 방문해 기록을 남겼어요");

    private final String type;
    private final String title;
    private final String body;
}
