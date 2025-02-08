package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diaries, Long> {
    Integer countDiariesByDiaryCategoriesId(Long diaryCategoryId);
    Integer countDiariesByUsersId(Long userId);
    List<Diaries> findAllByDiaryCategories(DiaryCategories diaryCategories);

    Page<Diaries> findAllByUsersOrderByDateDesc(Users user, PageRequest pageRequest);

    @Query("SELECT d FROM Diaries d WHERE d.users = :user AND FUNCTION('DATE', d.date) = FUNCTION('DATE', :date) ORDER BY d.date DESC")
    Page<Diaries> findAllByUsersAndDateOrderByDateDesc(@Param("user") Users user, @Param("date") LocalDateTime date, PageRequest pageRequest);

    @Query("SELECT d FROM Diaries d WHERE d.users = :user AND d.content LIKE %:content% ORDER BY d.date DESC")
    Page<Diaries> findAllByUsersAndContent(@Param("user")Users user, @Param("content") String content, PageRequest pageRequest);


    @Query("SELECT d " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "AND d.date = ( " +
            "    SELECT MAX(d2.date) " +
            "    FROM Diaries d2 " +
            "    WHERE d2.users = :user " +
            "    AND d2.clusterId = d.clusterId " +
            ")")
    List<Diaries> findAllByUsers(@Param("user") Users user);

    @Query("SELECT d " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "AND d.diaryCategories = :diaryCategories " +
            "AND d.date = ( " +
            "    SELECT MAX(d2.date) " +
            "    FROM Diaries d2 " +
            "    WHERE d2.users = :user " +
            "    AND d2.clusterId = d.clusterId " +
            ")")
    List<Diaries> findDiariesByUsersAndCategories(@Param("user") Users user, @Param("diaryCategories") DiaryCategories diaryCategories);

    @Query("SELECT d.clusterId " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "AND FLOOR(d.latitude * 100000.0) = FLOOR(:latitude * 100000.0) " +
            "AND FLOOR(d.longitude * 100000.0) = FLOOR(:longitude * 100000.0)")
    Optional<Long> findFirstClusterIdByUsersAndLatitudeAndLongitude(@Param("user") Users user, @Param("latitude") double latitude, @Param("longitude") double longitude);

    Optional<Diaries> findTop1ClusterIdByUsersOrderByClusterIdDesc(@Param("user") Users user);


    @Query("SELECT d FROM Diaries d WHERE d.users = :user AND d.clusterId = :clusterId ORDER BY d.date DESC" )
    Page<Diaries> findAllByUsersAndClusterId(@Param("user") Users user, @Param("clusterId") Long clusterId, PageRequest pageRequest);

    @Query("SELECT d FROM Diaries d WHERE d.users = :user AND d.clusterId = :clusterId AND d.diaryCategories = :diaryCategories ORDER BY d.date DESC" )
    Page<Diaries> findAllByUsersAndClusterIdAndCategories(@Param("user") Users user, @Param("clusterId") Long clusterId, @Param("diaryCategories") DiaryCategories diaryCategories, PageRequest pageRequest);

    @Query("SELECT DISTINCT d.date FROM Diaries d WHERE d.users = :user ORDER BY d.date DESC")
    List<LocalDateTime> findDistinctDatesByUser(@Param("user") Users user);
}