package TtattaBackend.ttatta.web.dto;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class DiaryRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryPostDTO {
        //private Long diaryCategoryId;
        private String content;
        private LocalDateTime date;

        //private MultipartFile image;

        private String latitude;
        private String longitude;
        private String locationName;

        // dto to entity
        public Diaries toEntity() {
            return Diaries.builder()
                    .content(content)
                    .date(date)
                    .latitude(latitude)
                    .longitude(longitude)
                    .locationName(locationName)
                    .build();
        }
    }
}
