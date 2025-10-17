package com.ecommerce.model;

import jakarta.persistence.*;
import java.util.List;

// @Entity  // REMOVE THIS LINE
// @Table(name = "carts")  // REMOVE THIS LINE
public class Cart {

    // @Id  // REMOVE THIS LINE
    // @GeneratedValue(strategy = GenerationType.IDENTITY)  // REMOVE THIS LINE
    private Long id;

    // @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)  // REMOVE THIS LINE
    private List<CartItem> items;

    // @ManyToOne  // REMOVE THIS LINE
    // @JoinColumn(name = "user_id")  // REMOVE THIS LINE
    private User user;

    // Getters and Setters remain
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}