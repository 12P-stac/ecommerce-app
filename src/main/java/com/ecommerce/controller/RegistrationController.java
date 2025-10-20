package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.Role;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showRegistrationForm(Model model, Authentication auth) {
        // Redirect logged-in users to their dashboard
        if (auth != null && auth.isAuthenticated()) {
            return redirectByRole(auth);
        }

        // Add empty user object for form binding
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
            RedirectAttributes redirectAttributes) {

        // Password validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // Username and email validation
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        try {
            // Create a new user instance
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setActive(true);
            user.setEmailVerified(false);

            // Assign appropriate role
            Set<Role> roles = new HashSet<>();
            Role role;

            if ("seller".equalsIgnoreCase(userType)) {
                role = roleRepository.findByName("ROLE_SELLER")
                        .orElseThrow(() -> new RuntimeException("Seller role not found"));
                user.setStoreName(storeName);
                user.setBusinessType(businessType);
                user.setTaxId(taxId);
            } else {
                role = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("User role not found"));
            }

            roles.add(role);
            user.setRoles(roles);

            // Save to DB
            userService.save(user);

            // Attempt auto-login
            try {
                request.login(username, password);
                return redirectByRole(userService.getCurrentAuthentication());
            } catch (ServletException e) {
                redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
                return "redirect:/login";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    private String redirectByRole(Authentication auth) {
        if (auth == null) return "redirect:/login";
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
        if (roles.contains("ROLE_SELLER")) return "redirect:/seller/dashboard";
        if (roles.contains("ROLE_USER")) return "redirect:/user/dashboard";
        return "redirect:/login";
    }
}
