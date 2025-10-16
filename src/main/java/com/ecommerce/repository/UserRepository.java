package com.ecommerce.repository;

import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ Find user by username
    Optional<User> findByUsername(String username);

    // ✅ Find user by email
    Optional<User> findByEmail(String email);

    // ✅ Check if email exists
    boolean existsByEmail(String email);

    // ✅ Check if username exists
    boolean existsByUsername(String username);
}
