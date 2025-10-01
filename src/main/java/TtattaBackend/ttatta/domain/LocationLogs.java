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
public class LocationLogs extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String target; // 대상

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'Android'")
    private String acquisitionPath; // 취득 경로

    @Column(nullable = false, length = 20)
    private String provisionalService; // 제공 서비스

    @Column(length = 20)
    private String recipient; // 제공받는 자
}
