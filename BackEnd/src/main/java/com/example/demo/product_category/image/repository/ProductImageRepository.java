package com.example.demo.product_category.image.repository;

import com.example.demo.product_category.image.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductIdOrderBySortOrderAscIdAsc(Integer productId);
}
