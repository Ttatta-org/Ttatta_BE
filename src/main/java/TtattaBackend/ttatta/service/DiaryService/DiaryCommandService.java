package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryCommandService {
    Diaries save(DiaryRequestDTO.PostDTO postDTO, MultipartFile diaryPhotos);
    DiaryPhotos savePhoto(MultipartFile diaryPhoto);
    void delete(Long diaryId);
    void deletePhoto(DiaryPhotos diaryPhoto);
    Diaries edit(DiaryRequestDTO.EditDTO editDTO, Long diaryId, MultipartFile editPhoto);

}
