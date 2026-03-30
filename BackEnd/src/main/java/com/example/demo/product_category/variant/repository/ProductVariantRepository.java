package com.example.demo.product_category.variant.repository;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Integer id);
    boolean existsByProductIdAndSizeIgnoreCaseAndColorIgnoreCase(Integer productId, String size, String color);
    boolean existsByProductIdAndSizeIgnoreCaseAndColorIgnoreCaseAndIdNot(Integer productId, String size, String color, Integer id);
    List<ProductVariant> findByProductIdOrderByIdAsc(Integer productId);
    Optional<ProductVariant> findByIdAndProductId(Integer id, Integer productId);
    List<ProductVariant> findByProductIdAndStockStatusOrderByIdAsc(Integer productId, VariantStockStatus stockStatus);
    List<ProductVariant> findByProductIdAndSkuContainingIgnoreCaseOrProductIdAndColorContainingIgnoreCaseOrProductIdAndSizeContainingIgnoreCase(
            Integer productId1, String sku,
            Integer productId2, String color,
            Integer productId3, String size
    );
}
