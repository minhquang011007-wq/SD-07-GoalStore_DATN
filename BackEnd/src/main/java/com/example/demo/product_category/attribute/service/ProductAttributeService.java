package com.example.demo.product_category.attribute.service;

import com.example.demo.product_category.attribute.dto.ProductAttributeRequest;
import com.example.demo.product_category.attribute.dto.ProductAttributeResponse;

import java.util.List;

public interface ProductAttributeService {
    ProductAttributeResponse create(Integer productId, ProductAttributeRequest request);
    ProductAttributeResponse update(Integer id, ProductAttributeRequest request);
    void delete(Integer id);
    ProductAttributeResponse getDetail(Integer id);
    List<ProductAttributeResponse> findByProduct(Integer productId);
}
