package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DiaryPhotos extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="diary_id")
    private Diaries diaries;

    public void setDiaries(Diaries diaries) {
        // 기존에 이미 등록되어 있던 관계를 제거
        if (this.diaries != null && diaries.getDiaryPhotosList() != null) {
            this.diaries.getDiaryPhotosList().remove(this);
        }

        this.diaries = diaries;

        // 양방향 관계를 설정
        if (diaries != null && diaries.getDiaryPhotosList() != null) {
            diaries.getDiaryPhotosList().add(this);
        }
    }
}
