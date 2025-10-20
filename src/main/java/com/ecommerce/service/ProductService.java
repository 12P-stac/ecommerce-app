package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public List<Product> getActiveProductsBySeller(User seller) {
        return productRepository.findBySellerAndActiveTrue(seller);
    }
    
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public Long countActiveProductsBySeller(User seller) {
        return productRepository.countActiveProductsBySeller(seller.getId());
    }
    public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
}

    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }
    public List<Product> getProductsByStatus(User seller, String status) {
        switch (status.toLowerCase()) {
            case "active":
                return productRepository.findBySellerAndActiveTrue(seller);
            case "outofstock":
                return productRepository.findBySellerAndQuantityEquals(seller, 0);
            case "pending":
                return productRepository.findBySellerAndApprovedFalse(seller);
            default:
                return getActiveProductsBySeller(seller);
        }
    }
    public Long countProductsByStatus(User seller, String status) {
        switch (status.toLowerCase()) {
            case "active":
                return productRepository.countActiveProductsBySeller(seller.getId());
            case "outofstock":
                return productRepository.countBySellerAndQuantityEquals(seller, 0);
            case "pending":
                return productRepository.countBySellerAndApprovedFalse(seller);
            default:
                return countActiveProductsBySeller(seller);
        }
    }

    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductsByCategory'");
    }
   
    
}