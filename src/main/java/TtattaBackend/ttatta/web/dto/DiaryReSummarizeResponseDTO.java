package TtattaBackend.ttatta.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryReSummarizeResponseDTO {
    private String content;
    private LocalDateTime createdAt;
}
