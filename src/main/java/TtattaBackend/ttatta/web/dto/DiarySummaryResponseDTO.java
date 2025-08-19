package TtattaBackend.ttatta.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DiarySummaryResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiarySummaryResultDTO {
        LocalDateTime createdAt;
        String summaryDiary;
    }
}
