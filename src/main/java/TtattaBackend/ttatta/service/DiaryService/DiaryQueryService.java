package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DiaryQueryService {
    List<Diaries> getFootprintDiaryList(Long diaryCategoryId);
    Page<Diaries> getDiaryList(LocalDateTime date, int requestNum);
    Page<Diaries> getSearchDiaryList(String content, int requestNum);
    Page<Diaries> getMapDiaryList(Long clusterId, Long diaryCategoryId, int requestNum);
    List<LocalDateTime> getDiaryDateList();
}
