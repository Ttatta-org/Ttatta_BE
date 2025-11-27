package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.web.dto.DiarySummaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryResponseDTO;

public interface SummaryCommandService {
    SummaryDiary summarize(DiarySummaryRequestDTO.SummarizeDTO request);
    SummaryDiary summarizeByDailySummaryAlarm(Long userId, DiarySummaryRequestDTO.SummarizeDTO request);
    SummaryDiary reSummarize(DiarySummaryRequestDTO.SummarizeDTO request);
    DiarySummaryResponseDTO.DiarySummaryResultDTO getSummary(DiarySummaryRequestDTO.GetDiarySummaryDTO request);
}
