package com.ecommerce.controller;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Object> cartItems = cartService.getCartItemsWithProductDetails(user);
        Integer cartItemCount = cartService.getCartItemCount(user);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItemCount", cartItemCount);
        model.addAttribute("user", user);
        
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                          @RequestParam(defaultValue = "1") Integer quantity,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add product to cart: " + e.getMessage());
        }
        
        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.removeFromCart(user, productId);
            redirectAttributes.addFlashAttribute("success", "Product removed from cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove product from cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartQuantity(@RequestParam Long productId,
                                   @RequestParam Integer quantity,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.updateCartItemQuantity(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("success", "Cart cleared!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
}