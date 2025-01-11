package TtattaBackend.ttatta.web.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class DiaryRequestDTO {
    @Getter
    public static class DiaryPostDTO {
        private Long userId;

        //private Long diaryCategoryId;
        private String content;
        private LocalDateTime date;

        private String latitude;
        private String longitude;
        private String locationName;

    }
}
