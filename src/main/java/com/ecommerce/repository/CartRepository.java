package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
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

    // Your existing working methods
    List<CartItem> findByUser_Id(Long userId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteAllByUser_Id(Long userId);
    Page<CartItem> findByUser_Id(Long userId, Pageable pageable);

    // Fixed methods with proper query annotations (using IDs instead of entities)
    @Query("SELECT c FROM CartItem c WHERE c.user.id = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // DELETE queries must use IDs, not entities
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.id = :userId")
    Integer countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user.id = :userId")
    Integer sumQuantityByUserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Convenience method that uses the ID-based method internally
    default List<CartItem> findByUser(User user) {
        return findByUserId(user.getId());
    }
    
    default Optional<CartItem> findByUserAndProductId(User user, Long productId) {
        return findByUserIdAndProductId(user.getId(), productId);
    }
    
    default void deleteByUser(User user) {
        deleteByUserId(user.getId());
    }
}