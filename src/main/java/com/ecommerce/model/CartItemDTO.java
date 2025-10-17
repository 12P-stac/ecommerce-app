package com.ecommerce.model;

import java.math.BigDecimal;

public class CartItemDTO {
    private Product product;
    private Integer quantity;
    
    // Default constructor
    public CartItemDTO() {}
    
    // Constructor with parameters
    public CartItemDTO(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product; 
    }
    
    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }
    
    // Calculate subtotal
    public BigDecimal getSubtotal() {
        if (product != null && product.getPrice() != null && quantity != null) {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    // Optional: Add toString for debugging
    @Override
    public String toString() {
        return "CartItemDTO{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}