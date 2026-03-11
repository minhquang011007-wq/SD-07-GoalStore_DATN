package com.example.demo.product_category.history.repository;

import com.example.demo.product_category.history.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    List<ProductHistory> findByProductIdOrderByChangedAtDesc(Integer productId);

    void deleteByProductId(Integer productId);
}
