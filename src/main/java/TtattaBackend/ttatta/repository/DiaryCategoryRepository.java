package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.validation.annotation.ExistDiaryCategory;
import TtattaBackend.ttatta.validation.annotation.ExistUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryCategoryRepository extends JpaRepository<DiaryCategories, Long> {
    List<DiaryCategories> findCategoriesByUsersId(Long userId);
    DiaryCategories findDiaryCategoriesById(Long diaryCategoriesId);
    Optional<DiaryCategories> findByName(@ExistDiaryCategory @Param("name")String name);
}