package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diaries, Long> {
    Integer countDiariesByDiaryCategoriesId(Long diaryCategoryId);
    Integer countDiariesByUsersId(Long userId);
    List<Diaries> findAllByDiaryCategories(DiaryCategories diaryCategories);

    Optional<Diaries> findByIdAndUsers(Long diaryId, Users user);
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
  
    // 유저별 일기 개수 조회
    @Query("SELECT COUNT(d) FROM Diaries d WHERE d.users = :user")
    long countByUsers(@Param("user") Users user);

    // 위치 기반 일기 조회
    @Query(value = """
    SELECT d.* FROM diaries d 
    WHERE d.user_id = :#{#user.id}
    AND (6371000 * acos(
        cos(radians(:latitude)) * cos(radians(d.latitude)) * 
        cos(radians(d.longitude) - radians(:longitude)) + 
        sin(radians(:latitude)) * sin(radians(d.latitude))
    )) <= :radius
    ORDER BY (6371000 * acos(
        cos(radians(:latitude)) * cos(radians(d.latitude)) * 
        cos(radians(d.longitude) - radians(:longitude)) + 
        sin(radians(:latitude)) * sin(radians(d.latitude))
    )) ASC, d.date DESC
    """, nativeQuery = true)
    List<Diaries> findNearByDiaries(
            @Param("user") Users user,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") int radius
    );
      
    @Query("SELECT d FROM Diaries d WHERE d.users = :user AND d.date BETWEEN :start AND :end")
    List<Diaries> findAllByUserIdAndDate(@Param("user") Users user,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    // clusterId 개수 조회
    @Query("SELECT d.clusterId, COUNT(d) " +
            "FROM Diaries d " +
            "WHERE d.users = :user " +
            "GROUP BY d.clusterId")
    List<Object[]> countDiariesGroupByClusterId(@Param("user") Users user);

    @Query("SELECT d.clusterId, COUNT(d) " +
            "FROM Diaries d " +
            "WHERE d.users = :user AND d.diaryCategories = :category " +
            "GROUP BY d.clusterId")
    List<Object[]> countDiariesGroupByClusterIdAndCategory(@Param("user") Users user, @Param("category") DiaryCategories category);

    Long countByUsersAndClusterId(Users user, Long clusterId);

    /**
     * 회전된 뷰포트 네 모서리 좌표를 받아
     * SQL 내부에서 POLYGON WKT를 만들어 ST_Contains로 필터링합니다.
     */
    @Query(value = """
        SELECT *
        FROM diaries d
        WHERE ST_Contains(
          ST_GeomFromText(:wkt, 4326),
          d.location
        )
        AND d.user_id = :userId
        """, nativeQuery = true)
    List<Diaries> findAllByUserIdAndCoordinates(
            @Param("wkt") String wkt,
            @Param("userId") Long userId
    );


    @Query(value = """
        SELECT d
        FROM Diaries d
        WHERE d.users = :user
        AND d.clusterId IN (
            SELECT d2.clusterId
            FROM Diaries d2
            WHERE d2.users = :user
            GROUP BY d2.clusterId
            HAVING count(d2) = 1
        )
        AND d.createdAt = (
            SELECT max(d3.createdAt)
            FROM Diaries d3
            WHERE d3.users = :user
                AND d3.clusterId = d.clusterId
        )
        ORDER BY d.createdAt DESC 
""")
    List<Diaries> findLatestUniqueClusterIdDiary(
            @Param("user") Users user
    );

    @Query(value = """
        SELECT d.*
        FROM diaries d
        WHERE ST_Contains(
          ST_GeomFromText(:wkt, 4326),
          d.location
        )
        AND d.user_id = :userId
        AND d.memory_diary_alarm_cool_time = 0
        """, nativeQuery = true)
    List<Diaries> findNearDiariesCandidates(
            @Param("wkt") String wkt,
            @Param("userId") Long userId
    );

    List<Diaries> findAllByUsersAndCreatedAtBetween(Users user, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Modifying
    @Query("UPDATE Diaries d SET d.memoryDiaryAlarmCoolTime = d.memoryDiaryAlarmCoolTime - 1 WHERE d.memoryDiaryAlarmCoolTime > 0")
    void decreaseCoolTimeOnMemoryDiaries();
}