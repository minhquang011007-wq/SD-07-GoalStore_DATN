package com.example.demo.cart.repository;

import com.example.demo.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndVariantId(Integer cartId, Integer variantId);
}
