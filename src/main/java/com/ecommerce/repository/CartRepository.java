package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    // Your existing methods
    List<CartItem> findByUser_Id(Long userId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteAllByUser_Id(Long userId);
    Page<CartItem> findByUser_Id(Long userId, Pageable pageable);

    // Additional methods for CartService compatibility
    List<CartItem> findByUser(User user);
    
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user = :user AND c.product = :product")
    void deleteByUserAndProduct(@Param("user") User user, @Param("product") Product product);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user = :user")
    Integer countByUser(@Param("user") User user);
    
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user = :user")
    Integer sumQuantityByUser(@Param("user") User user);
    
    boolean existsByUserAndProduct(User user, Product product);
}