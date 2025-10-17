package com.ecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ecommerce.model.Role;
import com.ecommerce.model.RoleName;
import com.ecommerce.model.User;
import com.ecommerce.service.RoleService;
import com.ecommerce.service.UserService;

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

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

       // In the registerUser method, after creating the user:
User user = new User();
user.setFirstName(firstName);
user.setLastName(lastName);
user.setUsername(username);
user.setEmail(email);
String phone = null;
user.setPhone(phone);
user.setPassword(passwordEncoder.encode(password));
user.setActive(true); // Use setActive() instead of setIsActive()
user.setEmailVerified(false);
        // Assign USER role
        Role userRole = roleService.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userService.save(user);

        // Auto-login
        try {
            request.login(username, password);
        } catch (ServletException e) {
            throw new RuntimeException("Auto login failed", e);
        }

        return "redirect:/user/dashboard";
    }
}
