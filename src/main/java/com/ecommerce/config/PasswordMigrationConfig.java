package com.ecommerce.config;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class PasswordMigrationConfig {

    @Bean
    CommandLineRunner encodeUserPasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<User> users = userRepository.findAll();
            for (User u : users) {
                String rawPassword = u.getPassword();
                if (!rawPassword.startsWith("$2a$")) { // avoid double encoding
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    u.setPassword(encodedPassword);
                }
            }
            userRepository.saveAll(users);
            System.out.println("Password migration completed!");
        };
    }
}
