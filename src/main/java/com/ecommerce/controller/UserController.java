package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        // points to src/main/resources/templates/user/dashboard.html
        return "user/dashboard";
    }
}
