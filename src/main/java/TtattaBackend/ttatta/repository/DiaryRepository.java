package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
            "AND d.createdAt = ( " +
            "    SELECT MAX(d2.createdAt) " +
            "    FROM Diaries d2 " +
            "    WHERE d2.users = :user " +
            "    AND d2.clusterId = d.clusterId " +
            ")")
    List<Diaries> findAllByUsers(@Param("user") Users user);


    @Query("SELECT d.clusterId " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "AND FLOOR(d.latitude * 100000.0) = FLOOR(:latitude * 100000.0) " +
            "AND FLOOR(d.longitude * 100000.0) = FLOOR(:longitude * 100000.0)")
    Optional<Long> findFirstClusterIdByUsersAndLatitudeAndLongitude(@Param("user") Users user, @Param("latitude") double latitude, @Param("longitude") double longitude);

    Optional<Diaries> findTop1ClusterIdByUsersOrderByClusterIdDesc(@Param("user") Users user);



}