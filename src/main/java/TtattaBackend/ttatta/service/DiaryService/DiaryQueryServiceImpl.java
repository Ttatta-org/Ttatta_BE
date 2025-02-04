package TtattaBackend.ttatta.service.DiaryService;


import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.repository.DiaryRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService{

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    @Override
    public List<Diaries> getFootprintDiaryList(){
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        List<Diaries> diariesList = diaryRepository.findAllByUsers(user);

        return diariesList;

    }

    @Override
    public Page<Diaries> getDiaryList(LocalDateTime date, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user =  userRepository.findById(userId).get();

        Page<Diaries> diariesPage;

        if(date == null) {
            diariesPage = diaryRepository.findAllByUsersOrderByDateDesc(user, PageRequest.of(requestNum, 5));
        } else {
            diariesPage = diaryRepository.findAllByUsersAndDateOrderByDateDesc(user, date, PageRequest.of(requestNum, 5));
        }

        return diariesPage;
    }

    @Override
    public Page<Diaries> getSearchDiaryList(String content, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndContent(user, content, PageRequest.of(requestNum, 5));

        return diariesPage;
    }

    @Override
    public Page<Diaries> getMapDiaryList(Long clusterId, int requestNum) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId).get();

        Page<Diaries> diariesPage = diaryRepository.findAllByUsersAndClusterId(user, clusterId, PageRequest.of(requestNum, 1));

        return diariesPage;
    }
}
