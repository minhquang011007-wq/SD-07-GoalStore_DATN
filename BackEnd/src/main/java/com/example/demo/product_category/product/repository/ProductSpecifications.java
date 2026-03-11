package com.example.demo.product_category.product.repository;

import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.entity.Product;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public final class ProductSpecifications {
    private ProductSpecifications() {}

    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Product> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String like = "%" + keyword.trim().toLowerCase() + "%";
            query.distinct(true);
            var variantJoin = root.join("variants", JoinType.LEFT);
            var tagJoin = root.join("tags", JoinType.LEFT);
            var attributeJoin = root.join("attributes", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("baseSku")), like),
                    cb.like(cb.lower(root.get("brand")), like),
                    cb.like(cb.lower(root.get("material")), like),
                    cb.like(cb.lower(variantJoin.get("sku")), like),
                    cb.like(cb.lower(variantJoin.get("color")), like),
                    cb.like(cb.lower(variantJoin.get("size")), like),
                    cb.like(cb.lower(tagJoin.get("name")), like),
                    cb.like(cb.lower(attributeJoin.get("name")), like),
                    cb.like(cb.lower(attributeJoin.get("value")), like)
            );
        };
    }

    public static Specification<Product> categoryIds(Set<Integer> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) return cb.conjunction();
            query.distinct(true);
            var join = root.join("categories", JoinType.INNER);
            return join.get("id").in(categoryIds);
        };
    }

    public static Specification<Product> tagIds(Set<Integer> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) return cb.conjunction();
            query.distinct(true);
            var join = root.join("tags", JoinType.INNER);
            return join.get("id").in(tagIds);
        };
    }

    public static Specification<Product> displayStatus(ProductDisplayStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("displayStatus"), status);
    }

    public static Specification<Product> brand(String brand) {
        return (root, query, cb) -> (brand == null || brand.isBlank()) ? cb.conjunction() : cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase());
    }

    public static Specification<Product> material(String material) {
        return (root, query, cb) -> (material == null || material.isBlank()) ? cb.conjunction() : cb.equal(cb.lower(root.get("material")), material.trim().toLowerCase());
    }

    public static Specification<Product> onlyInStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) return cb.conjunction();
            query.distinct(true);
            var join = root.join("variants", JoinType.LEFT);
            return inStock ? cb.greaterThan(join.get("stockQuantity"), 0) : cb.equal(join.get("stockQuantity"), 0);
        };
    }

    public static Specification<Product> stockStatus(VariantStockStatus stockStatus) {
        return (root, query, cb) -> {
            if (stockStatus == null) return cb.conjunction();
            query.distinct(true);
            var join = root.join("variants", JoinType.LEFT);
            return cb.equal(join.get("stockStatus"), stockStatus);
        };
    }

    public static Specification<Product> hideOutOfStock(Boolean hideOutOfStock) {
        return (root, query, cb) -> {
            if (hideOutOfStock == null || !hideOutOfStock) return cb.conjunction();
            query.distinct(true);
            var join = root.join("variants", JoinType.LEFT);
            return cb.greaterThan(join.get("stockQuantity"), 0);
        };
    }

    public static Specification<Product> createdWithinDays(Integer createdWithinDays) {
        return (root, query, cb) -> (createdWithinDays == null || createdWithinDays <= 0)
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.now().minusDays(createdWithinDays));
    }

    public static Specification<Product> productType(ProductType productType) {
        return (root, query, cb) -> productType == null ? cb.conjunction() : cb.equal(root.get("productType"), productType);
    }

    public static Specification<Product> targetGender(TargetGender targetGender) {
        return (root, query, cb) -> targetGender == null ? cb.conjunction() : cb.equal(root.get("targetGender"), targetGender);
    }

    public static Specification<Product> releaseYear(Integer releaseYear) {
        return (root, query, cb) -> releaseYear == null ? cb.conjunction() : cb.equal(root.get("releaseYear"), releaseYear);
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            query.distinct(true);

            var join = root.join("variants", JoinType.INNER);

            jakarta.persistence.criteria.Expression<BigDecimal> effectivePrice =
                    cb.<BigDecimal>coalesce(join.get("salePrice"), join.get("price"));

            if (minPrice != null && maxPrice != null) {
                return cb.between(effectivePrice, minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(effectivePrice, minPrice);
            }
            return cb.lessThanOrEqualTo(effectivePrice, maxPrice);
        };
    }
}
