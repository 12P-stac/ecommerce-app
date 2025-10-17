package com.ecommerce.controller;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartItemController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RoleRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /** ------------------ VIEW USER CART ------------------ **/
    @GetMapping("/user/{userId}")
    public String viewCart(@PathVariable Long userId, Model model) {
        Optional<User> userOpt = Optional.empty();
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found!");
            return "user/dashboard";
        }

        List<CartItem> cartItems = cartRepository.findByUser_Id(userId);
        model.addAttribute("user", userOpt.get());
        model.addAttribute("cartItems", cartItems);
        return "user/cart"; // create user/cart.html template
    }

    /** ------------------ ADD PRODUCT TO CART ------------------ **/
    @PostMapping("/add")
    public String addToCart(@RequestParam Long userId,
                            @RequestParam Long productId,
                            @RequestParam int quantity) {

        Optional<User> userOpt = Optional.empty();
        Optional<User> productOpt = Optional.empty();

        if (userOpt.isEmpty() || productOpt.isEmpty() || quantity <= 0) {
            return "redirect:/error";
        }

        CartItem cartItem = new CartItem(null, quantity);
        cartRepository.save(cartItem);

        return "redirect:/cart/user/" + userId;
    }

    /** ------------------ UPDATE CART ITEM QUANTITY ------------------ **/
    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(@PathVariable Long cartItemId,
                                 @RequestParam int quantity) {
        Optional<CartItem> cartItemOpt = cartRepository.findById(cartItemId);
        if (cartItemOpt.isPresent() && quantity > 0) {
            CartItem cartItem = cartItemOpt.get();
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);
            return "redirect:/cart/user/" + cartItem.getUser().getId();
        }
        return "redirect:/error";
    }

    /** ------------------ REMOVE CART ITEM ------------------ **/
    @GetMapping("/remove/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId) {
        Optional<CartItem> cartItemOpt = cartRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            Long userId = cartItemOpt.get().getUser().getId();
            cartRepository.deleteById(cartItemId);
            return "redirect:/cart/user/" + userId;
        }
        return "redirect:/error";
    }
}
