package com.ecommerce.controller;

import com.ecommerce.model.Order;
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

    // ✅ Seller dashboard summary
    @GetMapping("/dashboard")
    public String sellerDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + username));

        List<Product> sellerProducts = productService.getActiveProductsBySeller(seller);
        List<Order> sellerOrders = orderService.getSellerOrders(seller);

        Long totalProducts = productService.countActiveProductsBySeller(seller);
        Long totalOrders = (long) sellerOrders.size();
        Long pendingOrders = orderService.countPendingOrdersBySeller(seller);

        BigDecimal totalRevenue = sellerOrders.stream()
                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus().name()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("seller", seller);
        model.addAttribute("products", sellerProducts);
        model.addAttribute("orders", sellerOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "seller/dashboard";
    }

    // ✅ Seller products view
    @GetMapping("/products")
    public String sellerProducts(@RequestParam(required = false, defaultValue = "all") String status,
                                 Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + username));

        List<Product> products = "all".equalsIgnoreCase(status)
                ? productService.getActiveProductsBySeller(seller)
                : productService.getProductsByStatus(seller, status);

        model.addAttribute("seller", seller);
        model.addAttribute("products", products);
        model.addAttribute("status", status);
        return "seller/products";
    }

    // ✅ Add product form
    @GetMapping("/products/new")
    public String showAddProductForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + username));

        model.addAttribute("seller", seller);
        model.addAttribute("product", new Product());
        return "seller/add-product";
    }

    // ✅ Handle product save
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + username));

        product.setSeller(seller);
        productService.saveProduct(product);

        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        return "redirect:/seller/products";
    }

    // ✅ View orders
    @GetMapping("/orders")
    public String sellerOrders(Authentication authentication, Model model) {
        String username = authentication.getName();
        User seller = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + username));

        List<Order> orders = orderService.getSellerOrders(seller);

        model.addAttribute("seller", seller);
        model.addAttribute("orders", orders);
        return "seller/orders";
    }

    // ✅ Update order status
    @PostMapping("/orders/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update order status: " + e.getMessage());
        }
        return "redirect:/seller/orders";
    }
}
