package TtattaBackend.ttatta.service.AlarmService;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlaramType {
    WRITE_DIARY("writeDiary", "일기 작성 알림", "지금 일기를 작성해보세요!");

    private final String type;
    private final String title;
    private final String body;
}
