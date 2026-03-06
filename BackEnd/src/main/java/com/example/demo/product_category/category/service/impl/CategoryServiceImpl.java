package com.example.demo.product_category.category.service.impl;

import com.example.demo.product_category.category.dto.CategoryRequest;
import com.example.demo.product_category.category.dto.CategoryResponse;
import com.example.demo.product_category.category.entity.Category;
import com.example.demo.product_category.category.repository.CategoryRepository;
import com.example.demo.product_category.category.service.CategoryService;
import com.example.demo.product_category.common.exception.BadRequestException;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        categoryRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(c -> { throw new BadRequestException("Tên danh mục đã tồn tại"); });

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
        return ProductMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = getEntity(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new BadRequestException("Tên danh mục đã tồn tại");
        }
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        return ProductMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Integer id) {
        Category category = getEntity(id);
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new BadRequestException("Danh mục đang có sản phẩm, không thể xóa");
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(ProductMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse uploadImage(Integer id, MultipartFile file) {
        Category category = getEntity(id);
        String imageUrl = fileStorageService.store(file, "categories");
        category.setImageUrl(imageUrl);
        return ProductMapper.toCategoryResponse(categoryRepository.save(category));
    }

    private Category getEntity(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy category id = " + id));
    }
}
