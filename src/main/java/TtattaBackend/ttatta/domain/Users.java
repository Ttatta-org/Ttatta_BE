package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
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
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(length = 8)
    private String nickname;

    @Column(length = 15)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(length = 60)
    private String pinHash;

    @Enumerated(EnumType.STRING)
    @Column
    private LoginType loginType;

    @Column(length = 50)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Gender gender;

    @ColumnDefault("0")
    private Long point;

    @Enumerated(EnumType.STRING)
    @Column
    private UserStatus status;

    private LocalDateTime inactiveDate;

    private String providerId;

    @Column(columnDefinition = "TEXT")
    private String fcmToken;

    @Column(nullable = false)
    private int failedAttempts;

    private LocalDateTime lockUntil;

    // 로그인 관련
//    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Diaries> diariesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<DiaryCategories> diaryCategoriesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<OwnedItems> ownItemsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Challenges> challengesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<WrittingDiaryAlarm> writingDiaryAlarmList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<MemoryDiaryAlarm> memoryDiaryAlarmList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<ChallengeRemindAlarm> challengeRemindAlarmList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<DailySummaryAlarm> dailySummaryAlarmList = new ArrayList<>();
  
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<SummaryDiary> summaryDiaryList = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }

    // setter 대신 명시적인 표현을 위해 update 표현 사용
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updateEmail(String email) {
        this.email = email;
    }
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void updatePoint(Long point) {this.point = point;}
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public void updateStatus(UserStatus status) {this.status = status;}
    public void updatePinHash(String pinHash) {this.pinHash = pinHash;}

    public void updateFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isLockedNow() {
        return lockUntil != null && LocalDateTime.now().isBefore(lockUntil);
    }
    public void resetLock() {
        this.failedAttempts = 0;
        this.lockUntil = null;
    }

    public void lockFor(Duration duration) {
        this.lockUntil = LocalDateTime.now().plus(duration);
    }
}