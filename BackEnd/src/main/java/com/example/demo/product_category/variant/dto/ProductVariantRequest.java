package com.example.demo.product_category.variant.dto;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    @NotBlank(message = "SKU variant không được để trống")
    private String sku;

    @NotBlank(message = "Size không được để trống")
    private String size;

    @NotBlank(message = "Màu không được để trống")
    private String color;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0", inclusive = true, message = "Giá bán phải >= 0")
    private BigDecimal price;

    @DecimalMin(value = "0", inclusive = true, message = "Giá khuyến mãi phải >= 0")
    private BigDecimal salePrice;

    @NotNull(message = "Tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho phải >= 0")
    private Integer stockQuantity;

    private VariantStockStatus stockStatus;
}
