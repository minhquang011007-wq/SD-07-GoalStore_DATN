package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
}