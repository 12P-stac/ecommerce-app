package com.ecommerce.controller;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

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
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(defaultValue = "user") String userType,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String taxId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {

        // 1️⃣ Password validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // 2️⃣ Check username/email existence
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        try {
            // 3️⃣ Create user object
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setActive(true);
            user.setEmailVerified(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // 4️⃣ Set seller-specific fields
            if ("seller".equalsIgnoreCase(userType)) {
                user.setStoreName(storeName);
                user.setBusinessType(businessType);
                user.setTaxId(taxId);
            }

            // 5️⃣ Assign role
            Set<Role> roles = new HashSet<>();
            Role role = roleRepository.findByName(
                    "seller".equalsIgnoreCase(userType) ? "ROLE_SELLER" : "ROLE_USER"
            ).orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(role);
            user.setRoles(roles);

            // 6️⃣ Save user
            userService.save(user);

            // 7️⃣ Redirect after registration
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}
