package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.CartItemDTO;
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

    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity());
        }
        
        Optional<CartItem> existingCartItem = cartRepository.findByUser_IdAndProduct_Id(user.getId(), productId);
        
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity() + 
                                         ", Requested: " + newQuantity);
            }
            
            cartItem.setQuantity(newQuantity);
            return cartRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            return cartRepository.save(cartItem);
        }
    }

    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUser_Id(user.getId());
    }

    public List<CartItemDTO> getCartItemsAsDTO(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .map(item -> new CartItemDTO(item.getProduct(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public void removeFromCart(User user, Long productId) {
        cartRepository.deleteByUser_IdAndProduct_Id(user.getId(), productId);
    }

    public void updateCartItemQuantity(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(user, productId);
            return;
        }
        
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity());
        }
        
        Optional<CartItem> cartItem = cartRepository.findByUser_IdAndProduct_Id(user.getId(), productId);
        if (cartItem.isPresent()) {
            CartItem item = cartItem.get();
            item.setQuantity(quantity);
            cartRepository.save(item);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    public void clearCart(User user) {
        cartRepository.deleteAllByUser_Id(user.getId());
    }

    public Integer getCartItemCount(User user) {
        return (int) cartRepository.countByUser_Id(user.getId());
    }

    public Integer getTotalCartQuantity(User user) {
        return cartRepository.getTotalQuantityByUserId(user.getId());
    }

    public boolean isProductInCart(User user, Long productId) {
        return cartRepository.existsByUser_IdAndProduct_Id(user.getId(), productId);
    }

    public BigDecimal getCartTotal(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<CartItem> getCartItemByProduct(User user, Long productId) {
        return cartRepository.findByUser_IdAndProduct_Id(user.getId(), productId);
    }

    public Page<CartItem> getCartItemsPaginated(User user, Pageable pageable) {
        return cartRepository.findByUser_Id(user.getId(), pageable);
    }

    public boolean validateCart(User user) {
        List<CartItem> cartItems = cartRepository.findByUser_Id(user.getId());
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.getActive() || product.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public CartSummary getCartSummary(User user) {
        Integer itemCount = getCartItemCount(user);
        Integer totalQuantity = getTotalCartQuantity(user);
        BigDecimal totalAmount = getCartTotal(user);
        return new CartSummary(itemCount, totalQuantity, totalAmount);
    }

    public static class CartSummary {
        private final Integer itemCount;
        private final Integer totalQuantity;
        private final BigDecimal totalAmount;

        public CartSummary(Integer itemCount, Integer totalQuantity, BigDecimal totalAmount) {
            this.itemCount = itemCount;
            this.totalQuantity = totalQuantity;
            this.totalAmount = totalAmount;
        }

        public Integer getItemCount() { return itemCount; }
        public Integer getTotalQuantity() { return totalQuantity; }
        public BigDecimal getTotalAmount() { return totalAmount; }
    }

    public List<Object> getCartItemsWithProductDetails(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCartItemsWithProductDetails'");
    }
}