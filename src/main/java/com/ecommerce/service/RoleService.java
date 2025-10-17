package com.ecommerce.service;

import com.ecommerce.model.Role;
import com.ecommerce.model.RoleName;
import com.ecommerce.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}