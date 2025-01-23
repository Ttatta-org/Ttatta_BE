package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;

import java.util.List;

public interface DiaryQueryService {
    List<Diaries> getDiaryList(Long userId);
}
