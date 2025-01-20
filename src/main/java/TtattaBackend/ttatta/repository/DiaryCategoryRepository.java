package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryCategoryRepository extends JpaRepository<DiaryCategories, Long> {
    Optional<DiaryCategories> findByNameAndId(@Param("name")String name, @Param("userId")Long userId);
}
