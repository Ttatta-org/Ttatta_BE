package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diaries, Long> {
    Integer countDiariesByDiaryCategoriesId(Long diaryCategoryId);
    Integer countDiariesByUsersId(Long userId);

    @Modifying
    @Query("UPDATE Diaries d SET d.diaryCategories.id = :targetCategoryId WHERE d.diaryCategories.id = :sourceCategoryId")
    void updateCategoryForDiaries(@Param("sourceCategoryId") Long sourceCategoryId,
                                  @Param("targetCategoryId") Long targetCategoryId);


    @Query("SELECT d " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "AND d.date IN ( " +
            "SELECT MAX(d2.date) " +
            "FROM Diaries d2 " +
            "WHERE d2.users = :user " +
            "AND FLOOR(d2.latitude * 100000.0) = FLOOR(d.latitude * 100000.0) " +
            "AND FLOOR(d2.longitude * 100000.0) = FLOOR(d.longitude * 100000.0))")
    List<Diaries> findAllByUsers(Users user);
}