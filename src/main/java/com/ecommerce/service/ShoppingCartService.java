package com.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class ShoppingCartService {

    private final Map<Long, Integer> cartItems = new HashMap<>();

    // ✅ Add a product to the cart
    public void addProduct(UserDTO product, int quantity) {
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

    // ✅ Calculate total cost safely
    public BigDecimal getTotalAmount(Map<Long, UserDTO> productMap) {
        return cartItems.entrySet().stream()
                .map(entry -> {
                    UserDTO product = productMap.get(entry.getKey());
                    BigDecimal price = (product != null && product.getPrice() != null)
                            ? product.getPrice()
                            : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ✅ Check if cart is empty
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
