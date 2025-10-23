package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private UserService userService;

    // 🟢 View for all users (only approved + active)
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllApprovedProducts();
        model.addAttribute("products", products);
        return "products"; // users view (products.html)
    }

    // 🟡 Seller dashboard — pending + approved
    @GetMapping("/seller")
    public String sellerProducts(Model model, HttpSession session) {
        User seller = (User) session.getAttribute("loggedInUser");
        List<Product> pending = productService.getPendingProductsBySeller(seller);
        List<Product> approved = productService.getApprovedProductsBySeller(seller);
        model.addAttribute("pendingProducts", pending);
        model.addAttribute("approvedProducts", approved);
        return "seller/dashboard"; // seller dashboard
    }

    // 🟢 Seller adds a product (pending approval)
    @GetMapping("/seller/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "seller/add-product";
    }

    @PostMapping("/seller/add")
    public String saveProduct(@ModelAttribute("product") Product product, HttpSession session) {
        User seller = (User) session.getAttribute("loggedInUser");
        product.setSeller(seller);
        product.setApproved(false); // pending until admin approval
        productService.saveProduct(product);
        return "redirect:/products/seller";
    }

    // 🟣 Admin — all pending products
    @GetMapping("/admin/pending")
    public String viewPendingProducts(Model model) {
        List<Product> pending = productService.getAllPendingProducts();
        model.addAttribute("pendingProducts", pending);
        return "admin/pending-products";
    }

    // 🟢 Admin approves product
    @PostMapping("/admin/approve/{id}")
    public String approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
        return "redirect:/products/admin/pending";
    }

    // 🔴 Admin deletes a product
    @GetMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/admin/pending";
    }

    // 🟡 Buyer clicks “Buy Now”
    @GetMapping("/buy/{id}")
    public String buyProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return "buyer/buy-form"; // form to upload ID and phone
    }

    // 🟢 Buyer confirms purchase (uploads ID + phone)
    @PostMapping("/buy/confirm/{id}")
    public String confirmPurchase(@PathVariable Long id,
                                  @RequestParam("idNumber") String idNumber,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  HttpSession session, Model model) {
        User buyer = (User) session.getAttribute("loggedInUser");
        productService.handlePurchase(id, buyer, idNumber, phoneNumber);
        model.addAttribute("message", "Payment is after delivery. Welcome customer!");
        return "buyer/confirmation";
    }

    // 🟣 Admin — total orders
    @GetMapping("/admin/orders")
    public String viewOrders(Model model) {
        List<Product> sold = productService.getSoldProducts();
        model.addAttribute("soldProducts", sold);
        return "admin/orders";
    }
}
