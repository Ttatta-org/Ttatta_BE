package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.repository.LocationLogRepository;
import TtattaBackend.ttatta.service.AdminPageService.AdminPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final AdminPageQueryService adminPageQueryService;
    private final LocationLogRepository locationLogRepository;

//    @GetMapping("/admin/location-log")
//    public String adminLocationLogPage(Model model) {
//        List<LocationLogs> locationLogList = locationLogRepository.findAllByOrderByCreatedAtDesc();
//        model.addAttribute("logs", locationLogList); // 템플릿에서 ${logs}로 사용
//        return "adminPages/location-log"; // templates/adminPages/location-log.html 렌더링
//    }

    @GetMapping("/admin/location-log")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate toDate,
                       Model model) {
        int pageSize = 10;

        // 1) 조회
        var resultPage = adminPageQueryService.search(page, pageSize, keyword, fromDate, toDate);

        // 2) 모델에 "다시" 넣기 (템플릿에서 th:value, th:href에 사용)
        model.addAttribute("logs", resultPage.getContent());
        model.addAttribute("totalPage", resultPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "adminPages/location-log";
    }
}
