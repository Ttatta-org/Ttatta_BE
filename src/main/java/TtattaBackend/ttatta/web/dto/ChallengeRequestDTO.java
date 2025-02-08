package TtattaBackend.ttatta.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChallengeRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateChallengeRequestDTO {
        @NotBlank(message = "제목은 빈값일 수 없습니다.")
        @Size(max = 20, message = "제목은 20자이하여야 합니다.")
        private String title;
        private String content;
    }
}
