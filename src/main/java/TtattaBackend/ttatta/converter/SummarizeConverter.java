package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.web.dto.Message;
import TtattaBackend.ttatta.web.dto.ChatGPTResponseDTO;

import java.util.Collections;

public class SummarizeConverter {

    public static ChatGPTResponseDTO toSummarizeResponseDTO(String summary) {
        Message message = new Message("assistant", summary);
        ChatGPTResponseDTO.Choice choice = new ChatGPTResponseDTO.Choice(0,message);
        return new ChatGPTResponseDTO(Collections.singletonList(choice));
    }
}
