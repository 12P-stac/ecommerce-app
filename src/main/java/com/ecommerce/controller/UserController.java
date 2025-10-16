package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard"; // This loads templates/user/dashboard.html
    }
}
