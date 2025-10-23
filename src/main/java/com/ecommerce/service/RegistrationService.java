package com.ecommerce.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user, String roleName) {
        // ✅ Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // ✅ Fetch the correct role (must exist in DB)
        Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // ✅ Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ Set active flag
        user.setActive(true);

        // ✅ Save user
        return userRepository.save(user);
    }
}
