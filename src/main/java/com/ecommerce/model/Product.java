package com.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    public enum Status {
        PENDING,
        APPROVED,
        SOLD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "price_with_fee", precision = 10, scale = 2)
    private BigDecimal priceWithFee;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private String category;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private Boolean approved = false;

    private Boolean active = false; // new flag for display control

    private String buyerPhone;

    private String buyerIdUploadPath; // buyer uploads Kenyan ID

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Before saving, automatically add 5% fee and set initial state
    @PrePersist
    public void calculatePriceWithFee() {
        if (this.price != null) {
            this.priceWithFee = this.price.add(this.price.multiply(new BigDecimal("0.05")));
        }
        if (this.status == null) {
            this.status = Status.PENDING;
        }
        this.active = false;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getPriceWithFee() { return priceWithFee; }
    public void setPriceWithFee(BigDecimal priceWithFee) { this.priceWithFee = priceWithFee; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public String getBuyerIdUploadPath() { return buyerIdUploadPath; }
    public void setBuyerIdUploadPath(String buyerIdUploadPath) { this.buyerIdUploadPath = buyerIdUploadPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public boolean isApproved() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isApproved'");
    }

}
