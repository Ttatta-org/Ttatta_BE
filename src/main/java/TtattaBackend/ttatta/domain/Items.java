package TtattaBackend.ttatta.domain;


import TtattaBackend.ttatta.domain.common.BaseEntity;
import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Items extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long cost;

    @Column(columnDefinition = "TEXT")
    private String itemImg;

    @Enumerated(EnumType.STRING)
    private CharacterType characterType;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    @OneToMany(mappedBy = "items",cascade = CascadeType.ALL)
    private List<OwnedItems> ownedItemsList = new ArrayList<>();

}