package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Save or update a product with auto 5% price increase.
     */
    public Product save(Product product) {
        if (product.getPrice() != null) {
            BigDecimal priceWithFee = product.getPrice().add(product.getPrice().multiply(new BigDecimal("0.05")));
            product.setPriceWithFee(priceWithFee);
        }
        return productRepository.save(product);
    }

    /**
     * Seller adds a new product (starts as PENDING until admin approval).
     */
    public Product saveProduct(Product product) {
        product.setApproved(false);
        product.setStatus(Product.Status.PENDING);
        return save(product);
    }

    /**
     * Admin approves a product (makes it visible to users).
     */
    public void approveProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setApproved(true);
            product.setStatus(Product.Status.APPROVED);
            save(product);
        } else {
            throw new RuntimeException("Product not found with ID: " + id);
        }
    }

    /**
     * Handle buyer purchase action (Payment after delivery).
     */
    public void handlePurchase(Long id, User buyer, String idNumber, String phoneNumber) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            if (product.getStatus() != Product.Status.APPROVED) {
                throw new RuntimeException("This product is not available for purchase yet.");
            }

            product.setBuyer(buyer);
            product.setBuyerIdUploadPath(idNumber); // Kenyan ID upload path
            product.setBuyerPhone(phoneNumber);
            product.setStatus(Product.Status.SOLD);

            save(product);
        } else {
            throw new RuntimeException("Product not found with ID: " + id);
        }
    }

    /**
     * Fetch all products visible to users.
     */
    public List<Product> getAllActiveProducts() {
        return productRepository.findAllVisibleToUsers();
    }

    /**
     * Fetch all approved products.
     */
    public List<Product> getAllApprovedProducts() {
        return productRepository.findByApprovedTrue();
    }

    /**
     * Fetch all pending (unapproved) products for admin.
     */
    public List<Product> getAllPendingProducts() {
        return productRepository.findByApprovedFalse();
    }

    /**
     * Get pending products for a specific seller.
     */
    public List<Product> getPendingProductsBySeller(User seller) {
        return productRepository.findBySellerAndApprovedFalse(seller);
    }

    /**
     * Get approved products for a specific seller.
     */
    public List<Product> getApprovedProductsBySeller(User seller) {
        return productRepository.findBySellerAndApprovedTrue(seller);
    }

    /**
     * Get all products for a seller that are active.
     */
    public List<Product> getActiveProductsBySeller(User seller) {
        return productRepository.findBySellerAndApprovedTrue(seller);
    }

    /**
     * Count all active products by a seller.
     */
    public Long countActiveProductsBySeller(User seller) {
        return productRepository.countActiveProductsBySeller(seller.getId());
    }

    /**
     * Get single product.
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Delete product by ID.
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Pagination for admin or user product listing.
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Category filter.
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndApprovedTrue(category);
    }

    public List<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndApprovedTrue(category);
    }

    /**
     * Fetch sold products (for reporting or admin stats).
     */
    public List<Product> getSoldProducts() {
        return productRepository.findByStatus(Product.Status.SOLD);
    }
    // Approve product (Admin)
public void approve(Long productId) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    product.setApproved(true);
    product.setStatus(Product.Status.APPROVED);
    save(product);
}

// Reject product (Admin)
public void reject(Long productId) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    product.setApproved(false);
    product.setStatus(Product.Status.PENDING);
    save(product);
}

// Toggle active/inactive (Admin)
public void toggleActive(Long productId) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    product.setActive(!product.getActive());
    save(product);
}

// Delete product (Admin)
public void deleteById(Long productId) {
    if (!productRepository.existsById(productId)) {
        throw new RuntimeException("Product not found with ID: " + productId);
    }
    productRepository.deleteById(productId);
}

// Get all products (Admin)
public List<Product> findAll() {
    return productRepository.findAll();
}

// Get product by ID (for controller)
public Optional<Product> findById(Long productId) {
    return productRepository.findById(productId);
}

public List<Product> findBySeller(User seller) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findBySeller'");
}

public List<Product> getProductsByStatus(User seller, String status) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getProductsByStatus'");
}

}
