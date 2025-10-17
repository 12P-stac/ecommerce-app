package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellerAndActiveTrue(User seller);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);
    
    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.active = true")
    List<Product> findActiveProductsBySellerId(@Param("sellerId") Long sellerId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller.id = :sellerId AND p.active = true")
    Long countActiveProductsBySeller(@Param("sellerId") Long sellerId);
}