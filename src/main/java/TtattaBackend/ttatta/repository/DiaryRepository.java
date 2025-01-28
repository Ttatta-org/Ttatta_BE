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

import java.time.LocalDateTime;
import java.util.List;

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
}