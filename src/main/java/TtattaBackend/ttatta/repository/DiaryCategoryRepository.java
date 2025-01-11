package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCategoryRepository extends JpaRepository<DiaryCategories, Long> {
}
