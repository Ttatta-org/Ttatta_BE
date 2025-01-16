package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryCommandService {
    Diaries save(DiaryRequestDTO.PostDTO postDTO, MultipartFile diaryPhotos);
    void delete(Long diaryId);
}
