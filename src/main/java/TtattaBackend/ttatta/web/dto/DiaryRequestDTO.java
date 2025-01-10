package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class DiaryRequestDTO {
    @Getter
    public static class DiaryPostDTO {
        private Long userId;
        //private Long diaryCategoryId;
        private String content;
        private LocalDateTime date;

        //private List<MultipartFile> image;

        private String latitude;
        private String longitude;
        private String locationName;

    }
}
