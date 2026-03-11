package com.example.demo.product_category.history.service;

import com.example.demo.product_category.history.dto.ProductHistoryResponse;
import com.example.demo.product_category.product.entity.Product;

import java.util.List;

public interface ProductHistoryService {
    void log(Product product, String action, String note);
    List<ProductHistoryResponse> findByProduct(Integer productId);
}
