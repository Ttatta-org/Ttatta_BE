package TtattaBackend.ttatta.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuperAdminPageController {

    @GetMapping("/super/admin/home")
    public String superAdminHomePage() {
        return "superAdminPages/home";
    }

    @GetMapping("/super/admin/admin-log")
    public String superAdminAdminLogPage() {
        return "superAdminPages/admin-log";
    }
}
