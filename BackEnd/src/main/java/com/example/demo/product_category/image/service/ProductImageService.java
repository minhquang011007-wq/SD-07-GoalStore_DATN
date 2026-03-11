package com.example.demo.product_category.image.service;

import com.example.demo.product_category.image.dto.ProductImageResponse;
import com.example.demo.product_category.image.dto.UpdateImageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> upload(Integer productId, List<MultipartFile> files, boolean avatarFirst);
    ProductImageResponse update(Integer imageId, UpdateImageRequest request);
    void delete(Integer imageId);
    List<ProductImageResponse> findByProduct(Integer productId);
}
