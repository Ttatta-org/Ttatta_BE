package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.DiaryCategories;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryOnePhotoService implements DiaryPhotoService{
    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryOnePhotoService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public Diaries save(DiaryRequestDTO.DiaryPostDTO diaryPostDTO) {
        Diaries diaries = diaryPostDTO.toEntity();

        diaryRepository.save(diaries);
        return diaries;
   }
}
