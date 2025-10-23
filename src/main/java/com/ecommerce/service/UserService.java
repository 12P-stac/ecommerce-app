package com.ecommerce.service;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        return userRepository.save(user);
    }
    
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Alias methods for compatibility with your existing code
    public boolean existsByUsername(String username) {
        return usernameExists(username);
    }

    public boolean existsByEmail(String email) {
        return emailExists(email);
    }
    // Add these methods to your existing UserService class
public Optional<User> findById(Long id) {
    return userRepository.findById(id);
}

public Authentication getCurrentAuthentication() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCurrentAuthentication'");
}
public void registerUserWithRole(User user, String roleName) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    Object roleRepository = null;
    Role role = ((com.ecommerce.repository.RoleRepository) roleRepository).findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

    user.setRoles(Collections.singleton(role));
    userRepository.save(user);
}

public Object getOrCreateRole(String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getOrCreateRole'");
}

public String encodePassword(String password) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'encodePassword'");
}


}