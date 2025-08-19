package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryQueryService {
    DiaryResponseDTO.FootprintDiaryListDTO getFootprintDiaryList(Long diaryCategoryId, DiaryRequestDTO.ViewOnMapDTO request);
    DiaryResponseDTO.KeepDiaryListDTO getDiaryList(LocalDateTime date, int requestNum);
    DiaryResponseDTO.SearchDiaryListDTO getSearchDiaryList(String content, int requestNum);
    DiaryResponseDTO.MapResultDTO getMapDiaryList(Long clusterId, Long diaryCategoryId, int requestNum);
    List<LocalDateTime> getDiaryDateList();
    DiaryResponseDTO.RemindResultDTO findRemindDiary(DiaryRequestDTO.RemindDTO request);
    List<String> getPresignedForPost(String imageType);
    String getPresignedUrlForEdit(Long diaryId, String imageType);
}
