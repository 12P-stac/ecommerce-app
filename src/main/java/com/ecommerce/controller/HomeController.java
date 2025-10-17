package com.ecommerce.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if (roles.contains("ROLE_SELLER")) return "redirect:/seller/dashboard";
            if (roles.contains("ROLE_USER")) return "redirect:/user/dashboard";
        }
        return "home";
    }

    @GetMapping("/login")
    public String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if (roles.contains("ROLE_SELLER")) return "redirect:/seller/dashboard";
            if (roles.contains("ROLE_USER")) return "redirect:/user/dashboard";
        }
        return "login";
    }
}
