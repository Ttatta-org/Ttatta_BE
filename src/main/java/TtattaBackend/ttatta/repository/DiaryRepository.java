package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.Diaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diaries, Long> {
    Integer countDiariesByDiaryCategoriesId(Long diaryCategoryId);
    Integer countDiariesByUsersId(Long userId);
}