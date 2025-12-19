package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.domain.LocationAccessLogs;
import TtattaBackend.ttatta.service.SuperAdminPageService.SuperAdminPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class SuperAdminPageController {

    private final SuperAdminPageQueryService superAdminPageQueryService;

    @GetMapping("/super/admin/home")
    public String superAdminHomePage() {
        return "superAdminPages/home";
    }

    @GetMapping("/super/admin/admin-log")
    public String superAdminAdminLogPage(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate toDate,
                       Model model) {
        int pageSize = 10;
        System.out.println("page: " + page);

        // 1) 조회
        var resultPage = superAdminPageQueryService.search(page, pageSize, keyword, fromDate, toDate);

        System.out.println(resultPage.getContent());
        for (LocationAccessLogs log : resultPage.getContent()) {
            System.out.println("Log ID: " + log.getId());
        }

        // 2) 모델에 "다시" 넣기 (템플릿에서 th:value, th:href에 사용)
        model.addAttribute("logs", resultPage.getContent());
        model.addAttribute("totalPage", resultPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "superAdminPages/admin-log";
    }
}
