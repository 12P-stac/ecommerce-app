package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("totalAmount") BigDecimal totalAmount) {
        User user = userService.findByUsername(userDetails.getUsername())
                               .orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        orderService.createOrder(user, totalAmount);
        return "redirect:/orders";
    }

    @GetMapping
    public String viewOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                               .orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        return "orders"; // orders.html
    }

    @GetMapping("/admin")
    public String adminOrders(Model model) {
        List<Order> orders = orderService.getOrdersByStatus("Pending");
        model.addAttribute("orders", orders);
        return "admin/orders"; // admin/orders.html
    }

    @PostMapping("/admin/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status) {
        
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/orders/admin";
    }
}
