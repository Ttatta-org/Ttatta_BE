package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.LoginType;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private Long Id;

    @Column(nullable = false, length = 8)
    private String nickname;

    @Column(nullable = false, length = 10)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private LoginType loginType;

    @Column(length = 50)
    private String email;

    @Column(length = 13)
    private String phoneNumber;

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

    // 로그인 관련
//    private LocalDateTime lastLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    private LocalDateTime tokenExpiry;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Diaries> diariesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<DiaryCategories> diaryCategoriesList = new ArrayList<>();

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
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void updatePoint(Long point) {
        this.point = point;
    }
}