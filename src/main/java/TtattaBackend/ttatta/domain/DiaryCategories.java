package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DiaryCategories extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diary_category_id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'DEFAULT'")
    private CategoryColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @OneToMany(mappedBy = "diaryCategories", cascade = CascadeType.ALL)
    private List<Diaries> diariesList = new ArrayList<>();

    public void setUsers(Users users) {
        // 기존에 이미 등록되어 있던 관계를 제거
        if (this.users != null) {
            this.users.getDiaryCategoriesList().remove(this);
        }

        this.users = users;

        // 양방향 관계를 설정
        if (users != null) {
            users.getDiaryCategoriesList().add(this);
        }
    }
}
