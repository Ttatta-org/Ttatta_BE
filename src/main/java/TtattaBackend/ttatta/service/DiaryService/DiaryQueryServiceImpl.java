package TtattaBackend.ttatta.service.DiaryService;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Override
    public List<Diaries> getDiaryList(Long userId) {
        Users user = userRepository.findById(userId).get();

        List<Diaries> allDiaries = diaryRepository.findAllByUsers(user);

        return allDiaries;
    }
}
