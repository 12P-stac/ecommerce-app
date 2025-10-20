package com.ecommerce.config;

import com.ecommerce.model.Role;
import com.ecommerce.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (!roleRepository.existsByName("ROLE_USER")) {
            roleRepository.save(new Role("ROLE_USER", "Default user role"));
        }
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            roleRepository.save(new Role("ROLE_ADMIN", "Administrator role"));
        }
        if (!roleRepository.existsByName("ROLE_SELLER")) {
            roleRepository.save(new Role("ROLE_SELLER", "Seller role"));
        }
    }
}
