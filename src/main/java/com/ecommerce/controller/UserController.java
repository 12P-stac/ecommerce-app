package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Display home page
    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html
    }

    // ✅ Display registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // templates/register.html
    }

    // ✅ Handle registration form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already taken!");
            return "register";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user to DB
        userService.save(user);

        model.addAttribute("success", "Registration successful! You can now log in.");
        return "login"; // Redirect to login page
    }

    // ✅ Display login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // templates/login.html
    }

    // ✅ Display user dashboard
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard"; // templates/user/dashboard.html
    }

    // ✅ Display admin dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard"; // templates/admin/dashboard.html
    }
}
