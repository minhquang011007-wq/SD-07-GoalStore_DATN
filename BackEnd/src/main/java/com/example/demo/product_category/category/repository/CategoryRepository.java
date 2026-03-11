package com.example.demo.product_category.category.repository;

import com.example.demo.product_category.category.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    List<Category> findByIdIn(Collection<Integer> ids);
    List<Category> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    @EntityGraph(attributePaths = {"products", "products.categories", "products.tags", "products.images", "products.variants"})
    Optional<Category> findWithProductsById(Integer id);
}
