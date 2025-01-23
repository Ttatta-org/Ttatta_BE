package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryCategoryRepository extends JpaRepository<DiaryCategories, Long> {
    DiaryCategories findDiaryCategoriesById(Long diaryCategoriesId);
}
