package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // homepage
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }
}
