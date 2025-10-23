package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.ProductService;
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

    @Autowired
    private ProductService productService;

    // View all products
    @GetMapping("/products")
    public String manageProducts(Authentication authentication, Model model) {
        String username = authentication.getName();
        User admin = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Product> allProducts = productService.getAllApprovedProducts(); 
        model.addAttribute("products", allProducts);
        model.addAttribute("admin", admin);
        return "admin/products";
    }

    // Approve a product
    @PostMapping("/products/approve/{productId}")
    public String approveProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setApproved(true);
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Product approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Reject a product
    @PostMapping("/products/reject/{productId}")
    public String rejectProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setApproved(false);
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Product rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Toggle product active/inactive
    @PostMapping("/products/toggle/{productId}")
    public String toggleProductStatus(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setActive(!product.getActive());
            productService.save(product);
            String status = product.getActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("success", "Product " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update product status: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Delete a product
    @PostMapping("/products/delete/{productId}")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
