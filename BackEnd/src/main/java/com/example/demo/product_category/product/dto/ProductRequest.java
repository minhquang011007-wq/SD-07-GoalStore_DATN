package com.example.demo.product_category.product.dto;

import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "SKU chuẩn không được để trống")
    private String baseSku;

    private String brand;
    private String season;

    @NotNull(message = "Loại sản phẩm không được để trống")
    private ProductType productType;

    @NotNull(message = "Đối tượng không được để trống")
    private TargetGender targetGender;

    private String material;
    private String description;
    private Integer releaseYear;
    private ProductDisplayStatus displayStatus;

    private Set<Integer> categoryIds = new LinkedHashSet<>();
    private Set<Integer> tagIds = new LinkedHashSet<>();
}
