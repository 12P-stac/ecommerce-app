package com.ecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import com.ecommerce.service.RoleService;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ GET mapping to show the registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // templates/register.html
    }

    // ✅ POST mapping to handle registration submission
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model,
            HttpServletRequest request) {

        // Check password match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // Check if username or email exists
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // Create new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Assign default role USER
        Role userRole = roleService.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Save user
        userService.save(user);

        // Auto-login
        try {
            request.login(username, password);
        } catch (ServletException e) {
            throw new RuntimeException("Auto login failed after registration", e);
        }

        // Redirect to user dashboard
        return "redirect:/user/dashboard";
    }
}
