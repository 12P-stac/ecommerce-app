package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User admin = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // Get statistics
        List<User> allUsers = userService.findAll();
        Long totalUsers = (long) allUsers.size();
        Long totalSellers = allUsers.stream()
                .filter(user -> user.hasRole("ROLE_SELLER"))
                .count();
        Long totalAdmins = allUsers.stream()
                .filter(user -> user.hasRole("ROLE_ADMIN"))
                .count();
        
        // Get recent users (last 5)
        List<User> recentUsers = allUsers.stream()
                .limit(5)
                .toList();
        
        model.addAttribute("username", admin.getUsername());
        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalSellers", totalSellers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("recentUsers", recentUsers);
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Authentication authentication, Model model) {
        String username = authentication.getName();
        User admin = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        model.addAttribute("admin", admin);
        
        return "admin/users";
    }

    @PostMapping("/users/{userId}/toggle-status")
    public String toggleUserStatus(@PathVariable Long userId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Use getActive() instead of getIsActive()
            user.setActive(!user.getActive());
            userService.save(user);
            
            String status = user.getActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("success", "User " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user status: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @GetMapping("/sellers")
    public String manageSellers(Authentication authentication, Model model) {
        String username = authentication.getName();
        User admin = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<User> sellers = userService.findAll().stream()
                .filter(user -> user.hasRole("ROLE_SELLER"))
                .toList();
        
        model.addAttribute("sellers", sellers);
        model.addAttribute("admin", admin);
        
        return "admin/sellers";
    }

    // Method to manually create admin users (for testing)
    @GetMapping("/create-admin")
    public String createAdminUser(RedirectAttributes redirectAttributes) {
        try {
            // Check if admin already exists
            if (userService.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setUsername("admin");
                admin.setEmail("admin@ecommercepro.com");
                admin.setPassword("admin123"); // This will be encoded
                
                // You'll need to set the admin role here
                // This is just for testing - in production, create admins manually
                userService.save(admin);
                redirectAttributes.addFlashAttribute("success", "Admin user created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("info", "Admin user already exists!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
}