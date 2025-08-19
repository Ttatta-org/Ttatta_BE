package TtattaBackend.ttatta.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UsersWithdrawals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime withdrawnAt;

    @Column(nullable = false)
    private Integer activeDays;

    @Column(nullable = false)
    private Integer totalDiary;

    // 탈퇴 정보 설정
    public void setWithdrawalInfo(String reason, Integer activeDays, Integer totalDiary) {
        this.reason = reason;
        this.withdrawnAt = LocalDateTime.now();
        this.activeDays = activeDays;
        this.totalDiary = totalDiary;
    }
}
