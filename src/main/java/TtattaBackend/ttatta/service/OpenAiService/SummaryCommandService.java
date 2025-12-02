package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.web.dto.DiarySummaryRequestDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryResponseDTO;

public interface SummaryCommandService {
    public SummaryDiary summarize(DiarySummaryRequestDTO.SummarizeDTO request);
    public SummaryDiary reSummarize(DiarySummaryRequestDTO.SummarizeDTO request);
    public DiarySummaryResponseDTO.DiarySummaryResultDTO getSummary(DiarySummaryRequestDTO.GetDiarySummaryDTO request);
}
