package com.example.demo.product_category.category.controller;

import com.example.demo.product_category.category.dto.CategoryRequest;
import com.example.demo.product_category.category.dto.CategoryResponse;
import com.example.demo.product_category.category.service.CategoryService;
import com.example.demo.product_category.product.dto.ProductSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        categoryService.delete(id);
    }

    @GetMapping
    public List<CategoryResponse> findAll(@RequestParam(required = false) String keyword) {
        return (keyword == null || keyword.isBlank()) ? categoryService.findAll() : categoryService.search(keyword);
    }

    @GetMapping("/{id}")
    public CategoryResponse getDetail(@PathVariable Integer id) {
        return categoryService.getDetail(id);
    }

    @GetMapping("/{id}/products")
    public List<ProductSummaryResponse> getProducts(@PathVariable Integer id) {
        return categoryService.findProductsByCategory(id);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse uploadImage(@PathVariable Integer id, @RequestPart("file") MultipartFile file) {
        return categoryService.uploadImage(id, file);
    }
}
