package com.ecommerce.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        // Check if username/email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        } else {
            throw new RuntimeException("Email already registered");
        }
    
        // ✅ Assign default role
        Set<com.ecommerce.model.Role> roles = new HashSet<>();
        // Assuming Role has a constructor that accepts a role name
        user.setRoles(roles);
    
        // ✅ Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    
        // ✅ Save user
        return userRepository.save(user);
    }
}