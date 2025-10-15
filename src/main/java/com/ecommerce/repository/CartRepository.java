package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser_Id(Long userId);

    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);

    void deleteAllByUser_Id(Long userId);

    Page<CartItem> findByUser_Id(Long userId, Pageable pageable);
}
