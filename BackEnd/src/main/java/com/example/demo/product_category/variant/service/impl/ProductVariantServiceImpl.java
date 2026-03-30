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

import java.math.BigDecimal;
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

        validateBusiness(productId, request, null);

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
        Integer productId = variant.getProduct().getId();

        validateBusiness(productId, request, id);

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

    private void validateBusiness(Integer productId, ProductVariantRequest request, Integer id) {
        validateSku(request.getSku(), id);
        validateSizeColor(productId, request.getSize(), request.getColor(), id);
        validatePrice(request.getPrice(), request.getSalePrice());
        validateStock(request.getStockQuantity());
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

    private void validateSizeColor(Integer productId, String size, String color, Integer id) {
        if (size == null || size.isBlank()) {
            throw new BadRequestException("Size không được để trống");
        }
        if (color == null || color.isBlank()) {
            throw new BadRequestException("Màu không được để trống");
        }

        String normalizedSize = size.trim();
        String normalizedColor = color.trim();

        boolean exists = id == null
                ? variantRepository.existsByProductIdAndSizeIgnoreCaseAndColorIgnoreCase(productId, normalizedSize, normalizedColor)
                : variantRepository.existsByProductIdAndSizeIgnoreCaseAndColorIgnoreCaseAndIdNot(productId, normalizedSize, normalizedColor, id);

        if (exists) {
            throw new BadRequestException("Variant bị trùng size và màu trong cùng sản phẩm");
        }
    }

    private void validatePrice(BigDecimal price, BigDecimal salePrice) {
        if (price == null) {
            throw new BadRequestException("Giá bán không được để trống");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Giá bán phải lớn hơn hoặc bằng 0");
        }
        if (salePrice != null) {
            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Giá khuyến mãi phải lớn hơn hoặc bằng 0");
            }
            if (salePrice.compareTo(price) > 0) {
                throw new BadRequestException("Giá khuyến mãi không được lớn hơn giá bán");
            }
        }
    }

    private void validateStock(Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new BadRequestException("Tồn kho không được để trống");
        }
        if (stockQuantity < 0) {
            throw new BadRequestException("Tồn kho phải lớn hơn hoặc bằng 0");
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
