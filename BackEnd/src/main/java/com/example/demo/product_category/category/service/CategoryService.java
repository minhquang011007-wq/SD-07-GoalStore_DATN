package com.example.demo.product_category.category.service;

import com.example.demo.product_category.category.dto.CategoryRequest;
import com.example.demo.product_category.category.dto.CategoryResponse;
import com.example.demo.product_category.product.dto.ProductSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Integer id, CategoryRequest request);
    void delete(Integer id);
    List<CategoryResponse> findAll();
    CategoryResponse getDetail(Integer id);
    List<CategoryResponse> search(String keyword);
    List<ProductSummaryResponse> findProductsByCategory(Integer id);
    CategoryResponse uploadImage(Integer id, MultipartFile file);
}
