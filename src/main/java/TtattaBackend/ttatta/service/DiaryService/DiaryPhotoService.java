package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;

public interface DiaryPhotoService {
    Diaries save(DiaryRequestDTO.DiaryPostDTO diaryPostDTO);
}
