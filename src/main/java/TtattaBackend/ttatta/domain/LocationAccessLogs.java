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
public class LocationAccessLogs extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String handler; // 취급자

    @Column(nullable = false, length = 20)
    private String requestor; // 요청자

    @Column(nullable = false, length = 50)
    private String purpose; // 목적
}
