package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.converter.DiaryConverter;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.web.dto.DiaryRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryPhotoServiceImpl implements DiaryPhotoService{
    private final DiaryRepository diaryRepository;

    @Override
    @Transactional
    public Diaries save(DiaryRequestDTO.DiaryPostDTO request) {
        Diaries diaries = DiaryConverter.toDiaries(request);

        return diaryRepository.save(diaries);
   }
}
