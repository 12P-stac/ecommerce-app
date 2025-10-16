package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ Fetch all users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Save a new user or update existing one
    public void save(User user) {
        userRepository.save(user);
    }

    // ✅ Check if email already exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ✅ Check if username already exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ❌ Unused — remove or keep if you plan to use it later
    public List<Product> getAllUsers() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }
}
