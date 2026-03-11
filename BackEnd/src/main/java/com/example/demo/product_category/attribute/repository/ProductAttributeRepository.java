package com.example.demo.product_category.attribute.repository;

import com.example.demo.product_category.attribute.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Integer> {
    List<ProductAttribute> findByProductIdOrderBySortOrderAscIdAsc(Integer productId);
    boolean existsByProductIdAndNameIgnoreCaseAndValueIgnoreCase(Integer productId, String name, String value);
    boolean existsByProductIdAndNameIgnoreCaseAndValueIgnoreCaseAndIdNot(Integer productId, String name, String value, Integer id);
}
