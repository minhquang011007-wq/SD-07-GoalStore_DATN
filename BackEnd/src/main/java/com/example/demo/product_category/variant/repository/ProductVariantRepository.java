package com.example.demo.product_category.variant.repository;

import com.example.demo.product_category.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Integer id);
    List<ProductVariant> findByProductIdOrderByIdAsc(Integer productId);
    Optional<ProductVariant> findByIdAndProductId(Integer id, Integer productId);
}
