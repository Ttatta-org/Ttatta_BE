package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Point;

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


    // POINT(경도 위도), SRID 4326 적용
    @Column(columnDefinition = "POINT SRID 4326") //nullable = false 추가 필요
    private Point location;


    private Long clusterId;

    // ====== [추가] 암호화 좌표 관련 필드 ======

    // ~Cipher : 평문 위도, 경도를 AES-GCM으로 암호화한 결과값
    // ciphertext + GCM 인증 태그
    @Lob @Column(name = "lat_ct", nullable = false)
    private byte[] latCipher;
    @Lob @Column(name = "lng_ct", nullable = false)
    private byte[] lngCipher;

    // AES-GCM에 사용하는 초기화 벡터(IV)
    // GCM은 동일한 키 또는 IV로 암호화하면 치명적인 보안 문제가 발생하기 때문에 랜덤으로 생성
    @Column(name = "iv_lat", nullable = false)
    private byte[] ivLat;
    @Column(name = "iv_lng", nullable = false)
    private byte[] ivLng;

    // 실제 AES-GCM 암호화에 사용된 데이터 암호화 키(DEK)를 AWS KMS로 래핑한 결과
    // DB에는 평문 DEK가 절대 저장되지 않고, 암호화된 DEK만 저장됨.
    // 복호화 시에는 KMS에 dekWrapped를 보내서 평문 DEK를 다시 얻는다.
    @Lob @Column(name = "dek_wrapped", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] dekWrapped;

    // 어떤 KMS를 사용해 DEK를 래핑했는지 식별하기 위한 ARN
    @Column(name = "kms_key_id", nullable = false, length = 200)
    private String kmsKeyId;

    // 암호화 버전
    @Column(name = "enc_ver", nullable = false)
    private short encVer = 1;

    @Column(nullable = false)
    private int memoryDiaryAlarmCoolTime;

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

    public void updateMemoryDiaryAlarmCoolTime(int memoryDiaryAlarmCoolTime) {
        this.memoryDiaryAlarmCoolTime = memoryDiaryAlarmCoolTime;
    }
}
