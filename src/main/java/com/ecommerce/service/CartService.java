package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * Add product to cart or update quantity if already exists
     */
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        // Validate stock availability
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity());
        }
        
        // Check if item already in cart
        Optional<CartItem> existingCartItem = findByUserAndProduct(user, product);
        
        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            // Check stock for updated quantity
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity() + 
                                         ", Requested: " + newQuantity);
            }
            
            cartItem.setQuantity(newQuantity);
            return cartRepository.save(cartItem);
        } else {
            // Add new item
            CartItem cartItem = new CartItem();
            return cartRepository.save(cartItem);
        }
    }

    /**
     * Get all cart items for a user
     */
    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUser_Id(user.getId());
    }

    /**
     * Get cart items as DTOs for frontend display
     */
    public List<CartItem> getCartItemsAsDTO(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .map(item -> new CartItem())
                .collect(Collectors.toList());
    }

    /**
     * Remove specific product from user's cart
     */
    public void removeFromCart(User user, Long productId) {
        cartRepository.deleteByUser_IdAndProduct_Id(user.getId(), productId);
    }

    /**
     * Update quantity of a product in cart
     */
    public void updateCartItemQuantity(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(user, productId);
            return;
        }
        
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        // Validate stock availability
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity());
        }
        
        Optional<CartItem> cartItem = findByUserAndProduct(user, product);
        if (cartItem.isPresent()) {
            CartItem item = cartItem.get();
            item.setQuantity(quantity);
            cartRepository.save(item);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    /**
     * Clear all items from user's cart
     */
    public void clearCart(User user) {
        cartRepository.deleteAllByUser_Id(user.getId());
    }

    /**
     * Get count of distinct products in cart
     */
    public Integer getCartItemCount(User user) {
        return cartRepository.findByUser_Id(user.getId()).size();
    }

    /**
     * Get total quantity of all items in cart
     */
    public Integer getTotalCartQuantity(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Check if product is already in user's cart
     */
    public boolean isProductInCart(User user, Long productId) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    /**
     * Get total price of all items in cart
     */
    public BigDecimal getCartTotal(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get cart item by product ID for a user (custom implementation)
     */
    public Optional<CartItem> getCartItemByProduct(User user, Long productId) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    /**
     * Custom method to find cart item by user and product
     */
    private Optional<CartItem> findByUserAndProduct(User user, Product product) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
    }

    /**
     * Get paginated cart items
     */
    public Page<CartItem> getCartItemsPaginated(User user, Pageable pageable) {
        return cartRepository.findByUser_Id(user.getId(), pageable);
    }

    /**
     * Validate cart items (check stock, availability, etc.)
     */
    public boolean validateCart(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            
            // Check if product is still active
            if (!product.getActive()) {
                return false;
            }
            
            // Check stock availability
            if (product.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get cart summary for quick display (count and total)
     */
    public CartSummary getCartSummary(User user) {
        Integer itemCount = getCartItemCount(user);
        Integer totalQuantity = getTotalCartQuantity(user);
        BigDecimal totalAmount = getCartTotal(user);
        
        return new CartSummary(itemCount, totalQuantity, totalAmount);
    }

    /**
     * Inner class for cart summary
     */
    public static class CartSummary {
        private final Integer itemCount;
        private final Integer totalQuantity;
        private final BigDecimal totalAmount;

        public CartSummary(Integer itemCount, Integer totalQuantity, BigDecimal totalAmount) {
            this.itemCount = itemCount;
            this.totalQuantity = totalQuantity;
            this.totalAmount = totalAmount;
        }

        // Getters
        public Integer getItemCount() { return itemCount; }
        public Integer getTotalQuantity() { return totalQuantity; }
        public BigDecimal getTotalAmount() { return totalAmount; }
    }

    public List<Object> getCartItemsWithProductDetails(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCartItemsWithProductDetails'");
    }
}