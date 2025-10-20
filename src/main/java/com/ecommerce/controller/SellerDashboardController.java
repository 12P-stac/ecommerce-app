package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.Order.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/seller")
public class SellerDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String sellerDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // Get seller's products and orders
        List<Product> sellerProducts = productService.getActiveProductsBySeller(seller);
        List<Order> sellerOrders = orderService.getSellerOrders(seller);
        
        // Calculate statistics
        Long totalProducts = productService.countActiveProductsBySeller(seller);
        Long totalOrders = (long) sellerOrders.size();
        Long pendingOrders = orderService.countPendingOrdersBySeller(seller);
        
        // Calculate total revenue
        BigDecimal totalRevenue = sellerOrders.stream()
                .filter(order -> order.getStatus().name().equals("DELIVERED"))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("username", seller.getUsername());
        model.addAttribute("seller", seller);
        model.addAttribute("products", sellerProducts);
        model.addAttribute("orders", sellerOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        
        return "seller/dashboard";
    }

    @GetMapping("/products")
public String sellerProducts(@RequestParam(required = false, defaultValue = "all") String status,
                             Authentication authentication, Model model) {
    String username = authentication.getName();
    User seller = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
    
    List<Product> products = "all".equals(status)
            ? productService.getActiveProductsBySeller(seller)
            : productService.getProductsByStatus(seller, status);
    
    model.addAttribute("products", products);
    model.addAttribute("seller", seller);
    model.addAttribute("status", status); // for tabs active highlighting
    
    return "seller/products";
}

    

    @GetMapping("/products/new")
    public String showAddProductForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        model.addAttribute("product", new Product());
        model.addAttribute("seller", seller);
        return "seller/add-product";
    }

    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product, 
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        product.setSeller(seller);
        productService.saveProduct(product);
        
        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        return "redirect:/seller/products";
    }

    @GetMapping("/orders")
    public String sellerOrders(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<Order> orders = orderService.getSellerOrders(seller);
        model.addAttribute("orders", orders);
        model.addAttribute("seller", seller);
        
        return "seller/orders";
    }

    @PostMapping("/orders/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                  @RequestParam String status,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update order status: " + e.getMessage());
        }
        
        return "redirect:/seller/orders";
    }
}