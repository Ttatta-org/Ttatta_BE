package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaryCategoryRepository extends JpaRepository<DiaryCategories, Long> {
    List<DiaryCategories> findCategoriesByUsersId(Long userId);
}
