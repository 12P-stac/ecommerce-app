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
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // ✅ View all products
  //  @GetMapping
    //public String viewAllProducts(Authentication authentication, Model model) {
       // String username = authentication.getName();
       // User admin = userService.findByUsername(username)
              //  .orElseThrow(() -> new RuntimeException("Admin not found: " + username));

       // List<Product> allProducts = productService.getAllProducts(null).getContent(); // or productService.getAllApprovedProducts();
       // model.addAttribute("products", allProducts);
       // model.addAttribute("admin", admin);
       // return "admin/products";
   // }

    // ✅ Approve a pending product
    @PostMapping("/{id}/approve")
    public String approveProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.approveProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ✅ Toggle product active/inactive
    @PostMapping("/{id}/toggle-active")
    public String toggleActiveStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id)
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

    // ✅ Delete product
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
