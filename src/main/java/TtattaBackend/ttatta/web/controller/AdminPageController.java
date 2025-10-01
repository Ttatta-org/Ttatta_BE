package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.domain.LocationLogs;
import TtattaBackend.ttatta.repository.LocationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final LocationLogRepository locationLogRepository;

    @GetMapping("/admin/location-log")
    public String adminLocationLogPage(Model model) {
        List<LocationLogs> locationLogList = locationLogRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("logs", locationLogList); // 템플릿에서 ${logs}로 사용
        return "adminPages/location-log"; // templates/adminPages/location-log.html 렌더링
    }
}
