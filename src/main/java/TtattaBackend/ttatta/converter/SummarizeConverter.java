package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.SummaryDiary;
import TtattaBackend.ttatta.web.dto.DiaryReSummarizeResponseDTO;
import TtattaBackend.ttatta.web.dto.DiarySummaryResponseDTO;
import TtattaBackend.ttatta.web.dto.Message;
import TtattaBackend.ttatta.web.dto.ChatGPTResponseDTO;

import java.time.LocalDateTime;
import java.util.Collections;

public class SummarizeConverter {

    public static ChatGPTResponseDTO toSummarizeResponseDTO(SummaryDiary summary) {
        Message message = new Message("assistant", summary.getContent());
        ChatGPTResponseDTO.Choice choice = new ChatGPTResponseDTO.Choice(0,message, summary.getCreatedAt());
        return new ChatGPTResponseDTO(Collections.singletonList(choice));
    }

    public static DiarySummaryResponseDTO.DiarySummaryResultDTO toGetDiarySummaryResponseDTO(SummaryDiary summary) {
        return DiarySummaryResponseDTO.DiarySummaryResultDTO.builder()
                .createdAt(summary.getCreatedAt())
                .summaryDiary(summary.getContent())
                .build();
    }

    public static DiaryReSummarizeResponseDTO toReSummarizeResponseDTO(SummaryDiary summary) {
        return DiaryReSummarizeResponseDTO.builder()
                .createdAt(summary.getCreatedAt())
                .content(summary.getContent())
                .build();
    }
}
