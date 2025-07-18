package TtattaBackend.ttatta.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponseDTO {
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private Message message;
    }
}
