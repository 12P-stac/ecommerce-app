package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // Get user's orders
        List<Order> userOrders = orderService.getUserOrders(user);
        
        // Calculate statistics
        Long totalOrders = orderService.countUserOrders(user);
        Long pendingOrders = userOrders.stream()
                .filter(order -> order.getStatus().name().equals("PENDING") || order.getStatus().name().equals("PROCESSING"))
                .count();
        
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("orders", userOrders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("wishlistCount", 8); // Placeholder for now
        model.addAttribute("reviewCount", 5);   // Placeholder for now
        
        return "user/dashboard";
    }

    @GetMapping("/orders")
    public String userOrders(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        
        return "user/orders";
    }

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        model.addAttribute("user", user);
        return "user/profile";
    }
}