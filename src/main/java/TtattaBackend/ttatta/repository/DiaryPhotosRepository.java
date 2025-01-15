package TtattaBackend.ttatta.repository;

import TtattaBackend.ttatta.domain.DiaryPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryPhotosRepository extends JpaRepository<DiaryPhotos, Long> {
    List<DiaryPhotos> findByDiaries_Id(Long diaryId);
}
