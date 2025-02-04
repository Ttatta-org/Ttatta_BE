package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diaries extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime date;

    private double latitude;

    private double longitude;

    @Column(nullable = false, length = 50)
    private String locationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_category_id")
    private DiaryCategories diaryCategories;

    @OneToMany(mappedBy = "diaries", cascade = CascadeType.ALL)
    private List<DiaryPhotos> diaryPhotosList = new ArrayList<>();

    private Long clusterId;

    public void setUsers(Users users) {
        // 기존에 이미 등록되어 있던 관계를 제거
        if (this.users != null) {
            this.users.getDiariesList().remove(this);
        }

        this.users = users;

        // 양방향 관계를 설정
        if (users != null) {
            users.getDiariesList().add(this);
        }
    }

    public void setDiaryCategories(DiaryCategories diaryCategories) {
        // 기존에 이미 등록되어 있던 관계를 제거
        if (this.diaryCategories != null) {
            this.diaryCategories.getDiariesList().remove(this);
        }

        this.diaryCategories = diaryCategories;

        // 양방향 관계를 설정
        if (diaryCategories != null) {
            diaryCategories.getDiariesList().add(this);
        }
    }

    public void setClusterId(Long clusterId) { this.clusterId = clusterId;}

    public void updateContent(String content) {
        this.content = content;
    }

}
