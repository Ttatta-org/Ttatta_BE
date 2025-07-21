package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(nullable = false)
    private String diaryKeyHash;

    public void setUser(Users user) {
        if (this.users != null) {
            this.users.getDiariesList().remove(this);
        }

        this.users = user;

        if (!user.getSummaryDiaryList().contains(this)) {
            user.getSummaryDiaryList().add(this);
        }
    }

    public void updateContentAndKeyHash(String content, String diaryKeyHash) {
        this.content = content;
        this.diaryKeyHash = diaryKeyHash;
    }
}
