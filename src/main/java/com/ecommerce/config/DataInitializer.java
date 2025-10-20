package com.ecommerce.config;

import com.ecommerce.model.Role;
import com.ecommerce.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        createRoleIfNotExists("ROLE_USER", "Default user role");
        createRoleIfNotExists("ROLE_ADMIN", "Administrator role");
        createRoleIfNotExists("ROLE_SELLER", "Seller role");
    }

    private void createRoleIfNotExists(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
            roleRepository.save(role);
        }
    }
}
