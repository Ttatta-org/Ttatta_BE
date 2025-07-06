package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryQueryService {
    List<Diaries> getFootprintDiaryList(Long diaryCategoryId);
    DiaryResponseDTO.KeepDiaryListDTO getDiaryList(LocalDateTime date, int requestNum);
    Page<Diaries> getSearchDiaryList(String content, int requestNum);
    Page<Diaries> getMapDiaryList(Long clusterId, Long diaryCategoryId, int requestNum);
    List<LocalDateTime> getDiaryDateList();
    List<String> getPresignedForPost(String imageType);
    String getPresignedUrlForEdit(Long diaryId, String imageType);
    String getPresignedUrlForView(String objectKey);
}
