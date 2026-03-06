package com.example.demo.product_category.history.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistoryResponse {
    private Long id;
    private Integer productId;
    private String productName;
    private String action;
    private String note;
    private String changedBy;
    private LocalDateTime changedAt;
}
