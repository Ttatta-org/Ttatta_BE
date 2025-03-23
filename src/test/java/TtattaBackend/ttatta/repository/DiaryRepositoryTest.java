package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.DiaryPhotos;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.CategoryColor;
import TtattaBackend.ttatta.domain.enums.LoginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DiaryRepositoryTest {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryCategoryRepository diaryCategoryRepository;
    private final DiaryPhotosRepository diaryPhotosRepository;

    @Autowired
    public DiaryRepositoryTest(DiaryRepository diaryRepository, UserRepository userRepository, DiaryCategoryRepository diaryCategoryRepository, DiaryPhotosRepository diaryPhotosRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
        this.diaryCategoryRepository = diaryCategoryRepository;
        this.diaryPhotosRepository = diaryPhotosRepository;
    }

    private Users savedUser;
    private DiaryCategories savedDiaryCategories;

    @BeforeEach
    void setUp() {
        this.savedUser = Users.builder()
                .name("testName")
                .nickname("testNick")
                .username("testUsername")
                .password("testPassword")
                .loginType(LoginType.REGULAR)
                .email("test@gmail.com")
                .profileImage(null)
                .point(1000L)
                .providerId("providerTest")
                .diariesList(new ArrayList<>())
                .diaryCategoriesList(new ArrayList<>())
                .ownItemsList(new ArrayList<>())
                .challengesList(new ArrayList<>())
                .build();

        savedUser = userRepository.save(savedUser);

        this.savedDiaryCategories = DiaryCategories.builder()
                .name("testCategoryName")
                .color(CategoryColor.ORANGE)
                .diariesList(new ArrayList<>())
                .build();

        savedDiaryCategories.setUsers(savedUser);
        diaryCategoryRepository.save(savedDiaryCategories);

        savedUser = userRepository.findById(savedUser.getId()).get();
        savedDiaryCategories = diaryCategoryRepository.findDiaryCategoriesById(savedDiaryCategories.getId());

        for (int i = 0; i < 6; i++) {
            Diaries diaries = Diaries.builder()
                    .content("테스트 일기 저장")
                    .date(LocalDateTime.now().minusHours(i))
                    .locationName("테스트 위치")
                    .latitude(153.666666 + i)
                    .longitude(345.777777 + i)
                    .clusterId((long) i)
                    .diaryPhotosList(new ArrayList<>())
                    .build();

            diaries.setUsers(savedUser);
            diaries.setDiaryCategories(savedDiaryCategories);

            Diaries savedDiary = diaryRepository.save(diaries);

            DiaryPhotos diaryPhotos = DiaryPhotos.builder()
                    .imageUrl("테스트 s3 url")
                    .build();
            diaryPhotos.setDiaries(savedDiary);

            diaryPhotosRepository.save(diaryPhotos);
        }
    }


    @Test
    @DisplayName("일기 저장 테스트")
    public void save() {
        //given
        Diaries diaries = Diaries.builder()
                .content("테스트 일기 저장")
                .date(LocalDateTime.now())
                .locationName("테스트 위치")
                .latitude(153.666666)
                .longitude(345.777777)
                .clusterId(0L)
                .diaryPhotosList(new ArrayList<>())
                .build();

        diaries.setUsers(savedUser);
        diaries.setDiaryCategories(savedDiaryCategories);

        Diaries savedDiary = diaryRepository.save(diaries);

        DiaryPhotos diaryPhotos = DiaryPhotos.builder()
                .imageUrl("테스트 s3 url")
                .build();

        diaryPhotos.setDiaries(savedDiary);

        diaryPhotosRepository.save(diaryPhotos);

        // when
        Optional<Diaries> foundDiary = diaryRepository.findById(savedDiary.getId());

        // then
        assertThat(foundDiary).isPresent();
        assertThat(foundDiary.get().getContent()).isEqualTo("테스트 일기 저장");
        assertThat(foundDiary.get().getDiaryPhotosList().get(0).getImageUrl()).isEqualTo("테스트 s3 url");
        assertThat(foundDiary.get().getUsers()).isEqualTo(savedUser);
        assertThat(foundDiary.get().getDiaryCategories()).isEqualTo(savedDiaryCategories);
    }

    @Test
    @DisplayName("내가 쓴 일기 최신순으로 5개씩")
    public void findAllByUsersOrderByDateDesc() {
        // given

        // when
        Page<Diaries> diariesPage = diaryRepository.findAllByUsersOrderByDateDesc(savedUser, PageRequest.of(0, 5));

        // then
        assertThat(diariesPage.getContent().size()).isEqualTo(5); // 5개만 가져왔는지 확인
        for (int i = 0; i < diariesPage.getSize(); i++) {
            if (i < diariesPage.getSize() - 1) {
                assertThat(diariesPage.getContent().get(i).getDate()).isAfter(diariesPage.getContent().get(i + 1).getDate()); // 최신순 정렬 확인
            }
            assertThat(diariesPage.getContent().get(i).getUsers()).isEqualTo(savedUser); //내가 쓴 일기인지 확인
        }
    }

    @Test
    @DisplayName("내가 쓴 특정 날짜 일기 최신순으로 5개씩")
    void findAllByUsersAndDateOrderByDateDesc() {
        // given

        // when
        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndDateOrderByDateDesc(savedUser, LocalDateTime.now(), PageRequest.of(0, 5));

        // then
        assertThat(diariesPage.getContent().size()).isEqualTo(5); // 5개
        for (int i = 0; i < diariesPage.getSize(); i++) {
            assertThat(diariesPage.getContent().get(i).getUsers()).isEqualTo(savedUser); // 내가 쓴 일기
            assertThat(diariesPage.getContent().get(i).getDate().toLocalDate()).isEqualTo(LocalDate.now()); // 특정 날짜
            if (i < diariesPage.getSize() - 1) {
                assertThat(diariesPage.getContent().get(i).getDate()).isAfter(diariesPage.getContent().get(i + 1).getDate()); //최신순
            }
        }
    }

    @Test
    @DisplayName("내가 쓴 일기 검색")
    void findAllByUsersAndContent() {
        // given

        // when
        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndContent(savedUser, "테스트", PageRequest.of(0, 5));

        // then
        assertThat(diariesPage.getContent().size()).isEqualTo(5); // 5개
        for (int i = 0; i < diariesPage.getSize(); i++) {
            assertThat(diariesPage.getContent().get(i).getUsers()).isEqualTo(savedUser); // 내가 쓴 일기
            assertThat(diariesPage.getContent().get(i).getContent()).contains("테스트"); // 포함 O
            assertThat(diariesPage.getContent().get(i).getContent()).doesNotContain("메롱"); // 포함 X
            if (i < diariesPage.getSize() - 1) {
                assertThat(diariesPage.getContent().get(i).getDate()).isAfter(diariesPage.getContent().get(i + 1).getDate()); //최신순
            }
        }
    }

    @Test
    @DisplayName("내가 다른 장소에서 쓴 모든 일기")
    void findAllByUsers() {
        // given

        // when
        List<Diaries> diariesList = diaryRepository.findAllByUsers(savedUser);

        // then
        assertThat(diariesList.size()).isEqualTo(6); // 6개
        for (int i = 0; i < diariesList.size(); i++) {
            assertThat(diariesList.get(i).getUsers()).isEqualTo(savedUser); // 내가 쓴 일기
        }
    }

    @Test
    @DisplayName("카테고리 별 일기 모아보기 - 발자국 찍기 용")
    void findDiariesByUsersAndCategories() {
        // given
        DiaryCategories categoryBlue = DiaryCategories.builder()
                .name("testCategoryName")
                .color(CategoryColor.BLUE)
                .diariesList(new ArrayList<>())
                .build();

        categoryBlue.setUsers(savedUser);
        categoryBlue = diaryCategoryRepository.save(categoryBlue);

        Diaries diaries = Diaries.builder()
                .content("테스트 일기 저장")
                .date(LocalDateTime.now())
                .locationName("테스트 위치")
                .latitude(151.666666)
                .longitude(341.777777)
                .clusterId(8L)
                .diaryPhotosList(new ArrayList<>())
                .build();

        diaries.setUsers(savedUser);
        diaries.setDiaryCategories(categoryBlue);

        Diaries savedDiary = diaryRepository.save(diaries);

        DiaryPhotos diaryPhotos = DiaryPhotos.builder()
                .imageUrl("테스트 s3 url")
                .build();

        diaryPhotos.setDiaries(savedDiary);

        diaryPhotosRepository.save(diaryPhotos);

        // when
        List<Diaries> orangeList = diaryRepository.findDiariesByUsersAndCategories(savedUser, savedDiaryCategories);
        List<Diaries> blueList = diaryRepository.findDiariesByUsersAndCategories(savedUser, categoryBlue);

        // then
        assertThat(orangeList.size()).isEqualTo(6);
        for (int i = 0; i < orangeList.size(); i++) {
            assertThat(orangeList.get(i).getUsers()).isEqualTo(savedUser);
        }
        assertThat(blueList.size()).isEqualTo(1);
        for (int i = 0; i < blueList.size(); i++) {
            assertThat(blueList.get(i).getUsers()).isEqualTo(savedUser);
        }
    }

    @Test
    @DisplayName("clusterId 검색")
    void findFirstClusterIdByUsersAndLatitudeAndLongitude() {
        // given
        double latitude = 153.666666;
        double longitude = 345.777777;

        // when
        Optional<Long> clusterId = diaryRepository.findFirstClusterIdByUsersAndLatitudeAndLongitude(savedUser, latitude, longitude);
        Optional<Long> clusterId2 = diaryRepository.findFirstClusterIdByUsersAndLatitudeAndLongitude(savedUser, latitude + 100, longitude + 200);

        // then
        assertThat(clusterId.get()).isEqualTo(0L);
        assertThat(clusterId2.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("마지막 clusterId 파악")
    void findTop1ClusterIdByUsersOrderByClusterIdDesc() {
        // given

        // when
        Optional<Diaries> diaries = diaryRepository.findTop1ClusterIdByUsersOrderByClusterIdDesc(savedUser);

        // then
        assertThat(diaries.get().getClusterId()).isEqualTo(5);
    }

    @Test
    @DisplayName("동일한 clusterId 일기 최신순 반환")
    void findAllByUsersAndClusterId() {
        // given
        for (int i = 0; i < 3; i++) {
            Diaries diaries = Diaries.builder()
                    .content("테스트 일기 저장")
                    .date(LocalDateTime.now().minusHours(i))
                    .locationName("테스트 위치")
                    .latitude(153.666666)
                    .longitude(345.777777)
                    .clusterId(0L)
                    .diaryPhotosList(new ArrayList<>())
                    .build();

            diaries.setUsers(savedUser);
            diaries.setDiaryCategories(savedDiaryCategories);

            Diaries savedDiary = diaryRepository.save(diaries);

            DiaryPhotos diaryPhotos = DiaryPhotos.builder()
                    .imageUrl("테스트 s3 url")
                    .build();
            diaryPhotos.setDiaries(savedDiary);

            diaryPhotosRepository.save(diaryPhotos);
        }

        // when
        Page<Diaries> diaries = diaryRepository.findAllByUsersAndClusterId(savedUser, 0L, PageRequest.of(0, 3));

        // then
        assertThat(diaries.getSize()).isEqualTo(3);
        for (int i = 0; i < diaries.getSize(); i++) {
            assertThat(diaries.getContent().get(i).getUsers()).isEqualTo(savedUser);
            assertThat(diaries.getContent().get(i).getClusterId()).isEqualTo(0L);
            if (i < diaries.getSize() - 1) {
                assertThat(diaries.getContent().get(i).getDate()).isAfter(diaries.getContent().get(i + 1).getDate());
            }
        }
    }

    @Test
    @DisplayName("카테고리 별 발자국 표시")
    void findAllByUsersAndClusterIdAndCategories() {
        // given

        // when
        Page<Diaries> diaries = diaryRepository.findAllByUsersAndClusterIdAndCategories(savedUser, 0L, savedDiaryCategories, PageRequest.of(0, 1));

        // then
        assertThat(diaries.getContent().get(0).getUsers()).isEqualTo(savedUser);
        assertThat(diaries.getContent().get(0).getClusterId()).isEqualTo(0L);
        assertThat(diaries.getSize()).isEqualTo(1);
        assertThat(diaries.getContent().get(0).getDiaryCategories()).isEqualTo(savedDiaryCategories);
    }
}
