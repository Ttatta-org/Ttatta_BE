package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static TtattaBackend.ttatta.domain.enums.LoginType.REGULAR;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class DiaryCategoryRepositoryTest {

    @Autowired
    private DiaryCategoryRepository diaryCategoryRepository;

    @Autowired
    private UserRepository userRepository; // Users 엔티티 저장용

    private Users savedUser;

    @BeforeEach
    void setUp() {
        Users savedUser = Users.builder()
                .id(1L)
                .name("testName")
                .nickname("testnick")
                .username("testUsername")
                .password("testPassword")
                .email("testEmail")
                .gender(Gender.MALE)
                .status(UserStatus.ACTIVE)
                .loginType(REGULAR)
                .point(null)
                .phoneNumber(null)
                .profileImage(null)
                .inactiveDate(null)
                .refreshToken(null)
                .tokenExpiry(null)
                .diaryCategoriesList(null)
                .build();
        userRepository.save(savedUser);
    }

    @Test
    @DisplayName("새로운 다이어리 카테고리 생성")
    void createDiaryCategoryTest() {
        // Given
        DiaryCategories diaryCategory = new DiaryCategories(null,"testCategory", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory = diaryCategoryRepository.save(diaryCategory);


        // When
        Optional<DiaryCategories> foundDiaryCategory = diaryCategoryRepository.findById(savedDiaryCategory.getId());
        // 조회되지 않을 수도 있기 때문에 Optional 추가

        // Then
        assertThat(foundDiaryCategory)
                .isPresent()
                .hasValueSatisfying(newCategory -> {
                    assertThat(newCategory.getName()).isEqualTo("testCategory");
                    assertThat(newCategory.getUsers()).isEqualTo(savedUser);
                });
    }

    @Test
    @DisplayName("다이어리 카테고리 수정")
    void modifyDiaryCategoryTest() {
        // Given
        DiaryCategories diaryCategory = new DiaryCategories(null,"testCategory", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory = diaryCategoryRepository.save(diaryCategory);

        savedDiaryCategory.modifyCategoryColor(CategoryColor.BLACK);
        savedDiaryCategory.modifyCategoryName("changed name");

        assertThat(savedDiaryCategory.getColor()).isEqualTo(CategoryColor.BLACK);
        assertThat(savedDiaryCategory.getName()).isEqualTo("changed name");
    }

    @Test
    @DisplayName("모든 기록 삭제")
    void deleteAllDiaryCategoryTest() {

    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteDiaryCategoryTest() {

    }




}
