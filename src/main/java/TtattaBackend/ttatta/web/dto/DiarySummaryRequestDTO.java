package TtattaBackend.ttatta.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public class DiarySummaryRequestDTO {

    @Getter
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