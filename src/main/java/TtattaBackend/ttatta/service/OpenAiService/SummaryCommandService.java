package TtattaBackend.ttatta.service.OpenAiService;

import TtattaBackend.ttatta.web.dto.ChatGPTRequestDTO;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;

public interface SummaryCommandService {
    public String summarize(DiaryRequestDTO.SummarizeDTO request);
}
