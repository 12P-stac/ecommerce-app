package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.Product.Status;
import com.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🟢 Seller’s products that are active and approved
    @Query("SELECT p FROM Product p WHERE p.seller = :seller AND p.active = true AND p.approved = true")
    List<Product> findApprovedProductsBySeller(@Param("seller") User seller);

    // 🟠 Seller’s products pending approval
    @Query("SELECT p FROM Product p WHERE p.seller = :seller AND p.approved = false")
    List<Product> findPendingProductsBySeller(@Param("seller") User seller);

    // 🟢 Admin view: all pending products for approval
    @Query("SELECT p FROM Product p WHERE p.approved = false")
    List<Product> findAllPendingProducts();

    // 🟢 Admin view: all approved products
    @Query("SELECT p FROM Product p WHERE p.approved = true")
    List<Product> findAllApprovedProducts();

    // 🟣 User view: only approved and active products
    @Query("SELECT p FROM Product p WHERE p.approved = true AND p.active = true")
    List<Product> findAllVisibleToUsers();

    // 🔵 Category-based filtering
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.approved = true AND p.active = true")
    List<Product> findByCategoryVisibleToUsers(@Param("category") String category);

    // 🟢 Pagination support
    Page<Product> findByApprovedTrueAndActiveTrue(Pageable pageable);

    // 🟡 Seller stats
    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller = :seller AND p.approved = true")
    Long countApprovedProductsBySeller(@Param("seller") User seller);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller = :seller AND p.approved = false")
    Long countPendingProductsBySeller(@Param("seller") User seller);

    // 🧾 Find sold products (active = false means sold/removed)
    @Query("SELECT p FROM Product p WHERE p.seller = :seller AND p.active = false")
    List<Product> findSoldProductsBySeller(@Param("seller") User seller);

    // ✅ Derived queries
    Long countBySellerAndApprovedFalse(User seller);
    Long countBySellerAndStockQuantityEquals(User seller, int stock);
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);
    List<Product> findBySellerAndActiveTrue(User seller);
    List<Product> findBySellerAndStockQuantityEquals(User seller, int stock);
    List<Product> findBySellerAndApprovedFalse(User seller);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);

    // 🚫 FIX: This method had no implementation — corrected with @Query
    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller.id = :sellerId AND p.active = true")
    Long countActiveProductsBySeller(@Param("sellerId") Long sellerId);

    List<Product> findByApprovedFalse();

    List<Product> findByApprovedTrue();

    public List<Product> findBySellerAndApprovedTrue(User seller);

    List<Product> findByCategoryAndApprovedTrue(String category);

    List<Product> findByStatus(Status sold);
}
