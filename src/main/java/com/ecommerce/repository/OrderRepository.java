package com.ecommerce.repository;

import com.ecommerce.model.Order;
import com.ecommerce.model.Order.OrderStatus;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countOrdersByUser(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);
    
    @Query("SELECT COUNT(o) FROM Order o JOIN o.orderItems oi WHERE oi.product.seller.id = :sellerId AND o.status = :status")
    Long countOrdersBySellerAndStatus(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status);
}