package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;


public interface DiaryQueryService {
    Page<Diaries> getDiaryList(LocalDateTime date, int requestNum);
    Page<Diaries> getSearchDiaryList(String content, int requestNum);
}
