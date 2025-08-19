package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.enums.CharacterType;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static TtattaBackend.ttatta.domain.enums.LoginType.REGULAR;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OwnedItemRepository ownedItemRepository;

    @Autowired
    private UserRepository userRepository;

    private Users savedUser;

    @BeforeEach
    void setUp() {
        this.savedUser = userRepository.save(
                Users.builder()
                        .name("testName")
                        .nickname("testnick")
                        .username("testUsername")
                        .password("testPassword")
                        .email("testEmail")
                        .gender(Gender.MALE)
                        .status(UserStatus.ACTIVE)
                        .loginType(REGULAR)
                        .point(null)
                        .profileImage(null)
                        .inactiveDate(null)
                        .diaryCategoriesList(null)
                        .build()
        );
    }

    @Test
    @DisplayName("아이템 추가 테스트")
    void addItem() {
        // given
        Items item1 = new Items(null,"ttuttu","testItem1",500L, CharacterType.TTOTTO, BodyPart.HEAD,null);
        itemRepository.save(item1);

        // when
        assertThat(itemRepository.findAll()).hasSize(1);
        Items item2 = new Items(null,"ttuttu","testItem2",500L, CharacterType.TTOTTO, BodyPart.HEAD,null);
        itemRepository.save(item2);

        // then
        assertThat(itemRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("아이템 구매 테스트")
    void buyItem() {
        // given
        Items item1 = new Items(null,"ttuttu","testItem1",500L, CharacterType.TTOTTO, BodyPart.HEAD,null);
        itemRepository.save(item1);
        Items item2 = new Items(null,"ttuttu","testItem2",500L, CharacterType.TTOTTO, BodyPart.HEAD,null);
        itemRepository.save(item2);

        // when
        assertThat(ownedItemRepository.findAll()).hasSize(0);
        OwnedItems ownedItem = new OwnedItems(null,false,savedUser,item1);
        ownedItemRepository.save(ownedItem);

        // then
        assertThat(ownedItemRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("아이템 착용 테스트")
    void equipItem() {
        // given
        Items item1 = new Items(null,"ttuttu","testItem1",500L, CharacterType.TTOTTO, BodyPart.HEAD,null);
        itemRepository.save(item1);
        OwnedItems ownedItem = new OwnedItems(null,false,savedUser,item1);
        ownedItemRepository.save(ownedItem);

        // when
        ownedItem.setEquipped(true);

        // then
        Optional<OwnedItems> ownedItems = ownedItemRepository.findById(ownedItem.getId());
        assertThat(ownedItems)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item.getIsEquipped()).isTrue());
    }

    @Test
    @DisplayName("구매하지 않은 아이템 조회 테스트")
    void getShopItemTest() {
        // given
        Items item1 = new Items(null,null,"쌈뽕 썬글라스",300L,null,BodyPart.HEAD,null);
        itemRepository.save(item1);
        Items item2 = new Items(null,null,"귀여운 모자",500L,null,BodyPart.TORSO,null);
        itemRepository.save(item2);
        Items item3 = new Items(null,null,"맛있는 아이스크림",400L,null,BodyPart.HEAD,null);
        itemRepository.save(item3);

        OwnedItems ownedItem = new OwnedItems(null,false,savedUser,item1);

        // when
        ownedItemRepository.save(ownedItem);

        List<Items> shopItems = itemRepository.getShopItem(savedUser);

        // then
        assertThat(ownedItemRepository.findAll()).hasSize(1);
        assertThat(shopItems)
                .hasSize(2)
                .containsExactlyInAnyOrder(item2, item3);
    }
}
