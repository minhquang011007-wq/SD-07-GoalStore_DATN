package com.example.demo.product_category.product.dto;

import com.example.demo.product_category.attribute.dto.ProductAttributeResponse;
import com.example.demo.product_category.category.dto.CategoryResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.image.dto.ProductImageResponse;
import com.example.demo.product_category.tag.dto.TagResponse;
import com.example.demo.product_category.variant.dto.ProductVariantResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String baseSku;
    private String brand;
    private String season;
    private ProductType productType;
    private TargetGender targetGender;
    private String material;
    private String description;
    private Integer releaseYear;
    private ProductDisplayStatus displayStatus;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private List<CategoryResponse> categories;
    private List<TagResponse> tags;
    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;
    private List<ProductAttributeResponse> attributes;
}
