package com.ecommerce.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If user is logged in, redirect based on role
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()) {
            Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if (roles.contains("ROLE_SELLER")) return "redirect:/seller/dashboard";
            if (roles.contains("ROLE_USER")) return "redirect:/user/dashboard";
        }

        // Default page for guests
        return "home"; // maps to src/main/resources/templates/home.html
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If already logged in, redirect to dashboard
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()) {
            Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if (roles.contains("ROLE_SELLER")) return "redirect:/seller/dashboard";
            if (roles.contains("ROLE_USER")) return "redirect:/user/dashboard";
        }

        return "login"; // maps to templates/login.html
    }
}
