package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    // Spring Data JPA will automatically create these queries
    List<CartItem> findByUser_Id(Long userId);
    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteAllByUser_Id(Long userId);
    Page<CartItem> findByUser_Id(Long userId, Pageable pageable);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    
    // Count and sum can be calculated in service layer
    // Or use these if needed:
    long countByUser_Id(Long userId);
    
    // Default methods for convenience
    default List<CartItem> findByUser(com.ecommerce.model.User user) {
        return findByUser_Id(user.getId());
    }
    
    default Optional<CartItem> findByUserAndProductId(com.ecommerce.model.User user, Long productId) {
        return findByUser_IdAndProduct_Id(user.getId(), productId);
    }
    
    default void deleteByUser(com.ecommerce.model.User user) {
        deleteAllByUser_Id(user.getId());
    }
    
    default Integer getTotalQuantityByUserId(Long userId) {
        List<CartItem> cartItems = findByUser_Id(userId);
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}