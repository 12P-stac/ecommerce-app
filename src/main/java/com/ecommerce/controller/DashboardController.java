package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("user/dashboard")
    public String userDashboard() {
        return "user/dashboard"; // points to templates/user/dashboard.html
    }
}
