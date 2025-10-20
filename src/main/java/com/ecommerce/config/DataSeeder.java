package com.ecommerce.config;

import com.ecommerce.model.Role;
import com.ecommerce.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        createRoleIfNotExists("ROLE_USER", "Default role for registered users");
        createRoleIfNotExists("ROLE_ADMIN", "Administrator with full access");
        createRoleIfNotExists("ROLE_SELLER", "Seller with access to manage products");
    }

    private void createRoleIfNotExists(String name, String description) {
        roleRepository.findByName(name).ifPresentOrElse(
                role -> {}, // role exists, do nothing
                () -> {
                    Role newRole = new Role();
                    newRole.setName(name);
                    newRole.setDescription(description);
                    roleRepository.save(newRole);
                }
        );
    }
}
