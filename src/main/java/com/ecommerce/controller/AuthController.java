package com.ecommerce.controller;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.service.RoleService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role defaultRole = roleService.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found!"));
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        userService.save(user);
        model.addAttribute("success", "Registration successful! Please log in.");
        return "login";
    }
}
