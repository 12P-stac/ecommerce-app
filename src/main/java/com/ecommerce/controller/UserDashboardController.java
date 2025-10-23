package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.Order.OrderStatus;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public String userDashboard(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String category, // New: optional category filter
            Model model) {
    
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    
        Pageable pageable = PageRequest.of(page, 12, Sort.by("id").descending());
    
        Page<Product> allProductsPage;
    
        if (category != null && !category.isEmpty()) {
            allProductsPage = (Page<Product>) productService.getProductsByCategory(category, pageable);
        } else {
            allProductsPage = productService.getAllProducts(pageable);
        }
    
        List<Order> userOrders = orderService.getUserOrders(user);
        long totalOrders = orderService.countUserOrders(user);
        long pendingOrders = userOrders.stream()
                                       .filter(o -> o.getStatus().name().equals("PENDING") ||
                                                    o.getStatus().name().equals("PROCESSING"))
                                       .count();
    
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("orders", userOrders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("wishlistCount", 8);
        model.addAttribute("reviewCount", 5);
    
        model.addAttribute("allProductsPage", allProductsPage);
        model.addAttribute("allProducts", allProductsPage.getContent());
        model.addAttribute("selectedCategory", category);
    
        // For category filter dropdown
        model.addAttribute("categories", productService.getAllActiveProducts()
                .stream()
                .map(Product::getCategory)
                .distinct()
                .toList());
    
        return "user/dashboard";
    }
    

    @GetMapping("/orders")
    public String userOrders(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Order> orders = orderService.getUserOrders(user);

        model.addAttribute("orders", orders);
        model.addAttribute("pendingOrders", orders.stream()
                .filter(o -> o.getStatus().name().equals("PENDING") ||
                             o.getStatus().name().equals("PROCESSING"))
                .count());
        model.addAttribute("totalOrders", orders.size());

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
    @GetMapping("/buy-product/{productId}")
public String buyProduct(@PathVariable Long productId,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

    String username = authentication.getName();
    User user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));

    Product product = productService.getProductById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

    if (product.getStockQuantity() > 0) {
        // Reduce stock by 1
        product.setStockQuantity(product.getStockQuantity() - 1);
        productService.saveProduct(product);

        // Create order and order item (optional)
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // or PROCESSING
        orderService.saveOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setPrice(product.getPrice());
        orderService.saveOrderItem(orderItem);

        redirectAttributes.addFlashAttribute("success", "Product added to your orders!");
    } else {
        redirectAttributes.addFlashAttribute("error", "Product is out of stock!");
    }

    return "redirect:/user/dashboard";
}
}
