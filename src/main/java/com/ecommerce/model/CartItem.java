package com.ecommerce.model;

import java.math.BigDecimal;

public class CartItem {
    private Product product;
    private Integer quantity;
    
    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public User getUser() {
    
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }
}