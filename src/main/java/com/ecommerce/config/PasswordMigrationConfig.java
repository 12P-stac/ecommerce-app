package com.ecommerce.config;

import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import com.ecommerce.model.Role;

@Configuration
public class PasswordMigrationConfig {

    @Bean
    CommandLineRunner encodeUserPasswords(RoleRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<Role> users = userRepository.findAll();
            for (Role u : users) {
                String rawPassword = u.getName();
                if (!rawPassword.startsWith("$2a$")) { // Not encoded yet
                    
                }
            }
            userRepository.saveAll(users);
            System.out.println("Password migration completed!");
        };
    }
}
