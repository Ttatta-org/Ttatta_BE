package TtattaBackend.ttatta.domain;

import TtattaBackend.ttatta.domain.common.BaseEntity;
import TtattaBackend.ttatta.domain.enums.IsActive;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WrittingDiaryAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private IsActive isActive;

    private Time alaramTime;
}
