package com.example.demo.product_category.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatchUpdateRequest {
    @Valid
    @NotEmpty
    private List<ProductBatchUpdateItemRequest> items;
}
