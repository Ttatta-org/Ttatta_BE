package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.domain.enums.Gender;
import TtattaBackend.ttatta.domain.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
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

    @Autowired
    private DiaryRepository diaryRepository;

    private Users savedUser;

    @BeforeEach
    void setUp() {
        this.savedUser = userRepository.save(
            Users.builder()
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
                    .build()
        );
    }

    @AfterEach
    void cleanUp() {
        diaryRepository.deleteAll(); // 다이어리 먼저 삭제 (FK 제약 조건 때문)
        diaryCategoryRepository.deleteAll(); // 카테고리 삭제
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

        // When
        savedDiaryCategory.modifyCategoryColor(CategoryColor.BLACK);
        savedDiaryCategory.modifyCategoryName("changed name");

        // Then
        assertThat(savedDiaryCategory.getColor()).isEqualTo(CategoryColor.BLACK);
        assertThat(savedDiaryCategory.getName()).isEqualTo("changed name");
    }

    @Test
    @DisplayName("모든 기록 삭제")
    @WithMockUser(username = "testUser", roles = {"USERS"})
    void deleteAllDiaryCategoryTest() {
        // Given
        // 삭제할 카테고리 생성
        DiaryCategories diaryCategory = new DiaryCategories(null,"testCategory", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory = diaryCategoryRepository.save(diaryCategory);

        // 남을 카테고리 생성
        DiaryCategories diaryCategory2 = new DiaryCategories(null,"testCategory2", CategoryColor.RED,savedUser,null);
        DiaryCategories savedDiaryCategory2 = diaryCategoryRepository.save(diaryCategory2);


        Diaries diary1 = new Diaries(null,"즐거운 하루", LocalDateTime.now(),0L,0L,"숭실대학교",savedUser,savedDiaryCategory,null);
        Diaries diary2 = new Diaries(null,"재밌는 하루", LocalDateTime.now(),0L,0L,"정보과학관",savedUser,savedDiaryCategory,null);

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        // 다이어리 저장 확인
        assertThat(diaryCategoryRepository.findAll()).hasSize(2);

        // When
//        diaryCategoryRepository.delete(savedDiaryCategory);

        // Then
//        assertThat(diaryCategoryRepository.findAll()).hasSize(1);
//        assertThat(diaryRepository.findAllByDiaryCategories(savedDiaryCategory)).isEmpty();
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteDiaryCategoryTest() {

    }

    @Test
    @DisplayName("카테고리 개수 테스트")
    void getAllDiaryCategoriesTest() {
        // Given
        DiaryCategories diaryCategory = new DiaryCategories(null,"testCategory", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory = diaryCategoryRepository.save(diaryCategory);
        DiaryCategories diaryCategory2 = new DiaryCategories(null,"testCategory2", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory2 = diaryCategoryRepository.save(diaryCategory2);
        DiaryCategories diaryCategory3 = new DiaryCategories(null,"testCategory3", CategoryColor.WHITE,savedUser,null);
        DiaryCategories savedDiaryCategory3 = diaryCategoryRepository.save(diaryCategory3);


        // When
        diaryCategoryRepository.findCategoriesByUsersId(savedUser.getId());

        // Then
        assertThat(diaryCategoryRepository.findAll().stream().count()).isEqualTo(3);
    }
}
