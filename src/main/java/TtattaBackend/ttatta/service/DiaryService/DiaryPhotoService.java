package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DiaryPhotoService {
    Diaries save(DiaryRequestDTO.DiaryPostDTO diaryPostDTO, List<MultipartFile> diaryPhotos);
}
