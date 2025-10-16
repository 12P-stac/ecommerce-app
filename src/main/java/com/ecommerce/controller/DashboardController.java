package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard"; // points to templates/admin/dashboard.html
    }

    @GetMapping("user/dashboard")
    public String userDashboard() {
        return "user/dashboard"; // points to templates/user/dashboard.html
    }
}
