package com.ecommerce.repository;

import com.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Role, Long> {
}



