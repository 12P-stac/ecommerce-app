package com.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.ecommerce.model.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class ShoppingCartService {

    private final Map<Long, Integer> cartItems = new HashMap<>();

    // ✅ Add a product to the cart
    public void addProduct(User product, int quantity) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product or product ID cannot be null");
        }
        cartItems.put((Long) product.getId(), cartItems.getOrDefault(product.getId(), 0) + quantity);
    }

    // ✅ Remove product completely
    public void removeProduct(Long productId) {
        cartItems.remove(productId);
    }

    // ✅ Update quantity or remove if zero
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeProduct(productId);
        } else {
            cartItems.put(productId, quantity);
        }
    }

    // ✅ Clear entire cart
    public void clearCart() {
        cartItems.clear();
    }

    // ✅ Return all cart items
    public Map<Long, Integer> getCartItems() {
        return new HashMap<>(cartItems);
    }

    // ✅ Total number of items
    public int getTotalItems() {
        return cartItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    

    // ✅ Check if cart is empty
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
