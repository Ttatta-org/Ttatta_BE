package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diaries, Long> {
    Integer countDiariesByDiaryCategoriesId(Long diaryCategoryId);
    Integer countDiariesByUsersId(Long userId);

    @Modifying
    @Query("UPDATE Diaries d SET d.diaryCategories.id = :targetCategoryId WHERE d.diaryCategories.id = :sourceCategoryId")
    void updateCategoryForDiaries(@Param("sourceCategoryId") Long sourceCategoryId,
                                  @Param("targetCategoryId") Long targetCategoryId);
}