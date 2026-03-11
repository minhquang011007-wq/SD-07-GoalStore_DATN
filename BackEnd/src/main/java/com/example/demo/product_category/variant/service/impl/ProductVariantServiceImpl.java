package com.example.demo.product_category.variant.service.impl;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.common.exception.BadRequestException;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import com.example.demo.product_category.variant.dto.ProductVariantRequest;
import com.example.demo.product_category.variant.dto.ProductVariantResponse;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_category.variant.repository.ProductVariantRepository;
import com.example.demo.product_category.variant.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductVariantResponse create(Integer productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .filter(p -> Boolean.FALSE.equals(p.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product id = " + productId));

        validateSku(request.getSku(), null);

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.getSku().trim().toUpperCase())
                .size(request.getSize().trim().toUpperCase())
                .color(request.getColor().trim().toUpperCase())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .stockQuantity(request.getStockQuantity())
                .stockStatus(resolveStatus(request.getStockStatus(), request.getStockQuantity()))
                .build();

        return ProductMapper.toVariantResponse(variantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse update(Integer id, ProductVariantRequest request) {
        ProductVariant variant = getEntity(id);
        validateSku(request.getSku(), id);

        variant.setSku(request.getSku().trim().toUpperCase());
        variant.setSize(request.getSize().trim().toUpperCase());
        variant.setColor(request.getColor().trim().toUpperCase());
        variant.setPrice(request.getPrice());
        variant.setSalePrice(request.getSalePrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setStockStatus(resolveStatus(request.getStockStatus(), request.getStockQuantity()));

        return ProductMapper.toVariantResponse(variantRepository.save(variant));
    }

    @Override
    public void delete(Integer id) {
        variantRepository.delete(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getDetail(Integer id) {
        return ProductMapper.toVariantResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> findByProduct(Integer productId) {
        return variantRepository.findByProductIdOrderByIdAsc(productId).stream()
                .map(ProductMapper::toVariantResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> search(Integer productId, String keyword, VariantStockStatus stockStatus) {
        List<ProductVariant> variants;
        if (keyword != null && !keyword.isBlank()) {
            variants = variantRepository.findByProductIdAndSkuContainingIgnoreCaseOrProductIdAndColorContainingIgnoreCaseOrProductIdAndSizeContainingIgnoreCase(
                    productId, keyword.trim(), productId, keyword.trim(), productId, keyword.trim());
        } else if (stockStatus != null) {
            variants = variantRepository.findByProductIdAndStockStatusOrderByIdAsc(productId, stockStatus);
        } else {
            variants = variantRepository.findByProductIdOrderByIdAsc(productId);
        }

        return variants.stream()
                .filter(v -> stockStatus == null || v.getStockStatus() == stockStatus)
                .sorted(Comparator.comparing(ProductVariant::getId))
                .map(ProductMapper::toVariantResponse)
                .toList();
    }

    private void validateSku(String sku, Integer id) {
        if (sku == null || sku.isBlank()) {
            throw new BadRequestException("SKU variant không được để trống");
        }
        boolean exists = id == null
                ? variantRepository.existsBySkuIgnoreCase(sku.trim())
                : variantRepository.existsBySkuIgnoreCaseAndIdNot(sku.trim(), id);
        if (exists) {
            throw new BadRequestException("SKU variant đã tồn tại");
        }
    }

    private ProductVariant getEntity(Integer id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy variant id = " + id));
    }

    private VariantStockStatus resolveStatus(VariantStockStatus input, Integer stock) {
        if (input != null) return input;
        return stock != null && stock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG;
    }
}
