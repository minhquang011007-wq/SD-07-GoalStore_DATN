package com.example.demo.product_category.image.service.impl;

import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.common.storage.FileStorageService;
import com.example.demo.product_category.image.dto.ProductImageResponse;
import com.example.demo.product_category.image.dto.UpdateImageRequest;
import com.example.demo.product_category.image.entity.ProductImage;
import com.example.demo.product_category.image.repository.ProductImageRepository;
import com.example.demo.product_category.image.service.ProductImageService;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<ProductImageResponse> upload(Integer productId, List<MultipartFile> files, boolean avatarFirst) {
        Product product = productRepository.findById(productId)
                .filter(p -> Boolean.FALSE.equals(p.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product id = " + productId));

        List<ProductImage> existing = imageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId);
        int nextOrder = existing.size() + 1;
        boolean hasAvatar = existing.stream().anyMatch(i -> Boolean.TRUE.equals(i.getAvatar()));

        List<ProductImageResponse> responses = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String url = fileStorageService.store(files.get(i), "products");
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .avatar(!hasAvatar && avatarFirst && i == 0)
                    .sortOrder(nextOrder++)
                    .build();
            responses.add(ProductMapper.toImageResponse(imageRepository.save(image)));
        }
        return responses;
    }

    @Override
    public ProductImageResponse update(Integer imageId, UpdateImageRequest request) {
        ProductImage image = getEntity(imageId);
        if (request.getAvatar() != null && request.getAvatar()) {
            List<ProductImage> images = imageRepository.findByProductIdOrderBySortOrderAscIdAsc(image.getProduct().getId());
            images.forEach(i -> {
                if (!i.getId().equals(imageId)) {
                    i.setAvatar(false);
                    imageRepository.save(i);
                }
            });
            image.setAvatar(true);
        } else if (request.getAvatar() != null) {
            image.setAvatar(false);
        }

        if (request.getSortOrder() != null) {
            image.setSortOrder(request.getSortOrder());
        }
        return ProductMapper.toImageResponse(imageRepository.save(image));
    }

    @Override
    public void delete(Integer imageId) {
        ProductImage image = getEntity(imageId);
        fileStorageService.deleteByRelativeUrl(image.getImageUrl());
        imageRepository.delete(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> findByProduct(Integer productId) {
        return imageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId).stream()
                .map(ProductMapper::toImageResponse)
                .toList();
    }

    private ProductImage getEntity(Integer imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy image id = " + imageId));
    }
}
