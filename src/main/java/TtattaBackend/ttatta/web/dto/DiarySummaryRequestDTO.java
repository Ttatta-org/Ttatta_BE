package TtattaBackend.ttatta.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public class DiarySummaryRequestDTO {

    @Getter
    @Builder
    public static class SummarizeDTO {
        private LocalDate date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetDiarySummaryDTO {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;
    }
}