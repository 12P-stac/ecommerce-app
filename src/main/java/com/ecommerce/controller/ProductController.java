package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllActiveProducts();
        model.addAttribute("products", products);
        return "products"; // products.html
    }

    @GetMapping("/admin")
    public String adminProducts(Model model) {
        List<Product> products = productService.getAllActiveProducts();
        model.addAttribute("products", products);
        return "admin/products"; // admin/products.html
    }

    @GetMapping("/admin/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product"; // admin/add-product.html
    }

    @PostMapping("/admin/add")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @PostMapping("/admin/update")
    public String updateProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/admin";
    }
}
