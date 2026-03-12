package com.example.demo.product_category.attribute.service.impl;

import com.example.demo.product_category.attribute.dto.ProductAttributeRequest;
import com.example.demo.product_category.attribute.dto.ProductAttributeResponse;
import com.example.demo.product_category.attribute.entity.ProductAttribute;
import com.example.demo.product_category.attribute.repository.ProductAttributeRepository;
import com.example.demo.product_category.attribute.service.ProductAttributeService;
import com.example.demo.product_category.common.exception.BadRequestException;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.history.service.ProductHistoryService;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductRepository productRepository;
    private final ProductHistoryService productHistoryService;

    @Override
    public ProductAttributeResponse create(Integer productId, ProductAttributeRequest request) {
        Product product = getProduct(productId);
        validateUnique(productId, request.getName(), request.getValue(), null);

        ProductAttribute attribute = ProductAttribute.builder()
                .product(product)
                .name(request.getName().trim())
                .value(request.getValue().trim())
                .sortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder())
                .build();

        ProductAttribute saved = attributeRepository.save(attribute);
        productHistoryService.log(product, "ADD_ATTRIBUTE", "Thêm thuộc tính: " + saved.getName() + " = " + saved.getValue());
        return toResponse(saved);
    }

    @Override
    public ProductAttributeResponse update(Integer id, ProductAttributeRequest request) {
        ProductAttribute attribute = getEntity(id);
        validateUnique(attribute.getProduct().getId(), request.getName(), request.getValue(), id);

        attribute.setName(request.getName().trim());
        attribute.setValue(request.getValue().trim());
        attribute.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        ProductAttribute saved = attributeRepository.save(attribute);
        productHistoryService.log(saved.getProduct(), "UPDATE_ATTRIBUTE", "Cập nhật thuộc tính: " + saved.getName() + " = " + saved.getValue());
        return toResponse(saved);
    }

    @Override
    public void delete(Integer id) {
        ProductAttribute attribute = getEntity(id);
        productHistoryService.log(attribute.getProduct(), "DELETE_ATTRIBUTE", "Xóa thuộc tính: " + attribute.getName() + " = " + attribute.getValue());
        attributeRepository.delete(attribute);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeResponse getDetail(Integer id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeResponse> findByProduct(Integer productId) {
        getProduct(productId);
        return attributeRepository.findByProductIdOrderBySortOrderAscIdAsc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateUnique(Integer productId, String name, String value, Integer id) {
        String n = name == null ? "" : name.trim();
        String v = value == null ? "" : value.trim();
        boolean exists = id == null
                ? attributeRepository.existsByProductIdAndNameIgnoreCaseAndValueIgnoreCase(productId, n, v)
                : attributeRepository.existsByProductIdAndNameIgnoreCaseAndValueIgnoreCaseAndIdNot(productId, n, v, id);
        if (exists) {
            throw new BadRequestException("Thuộc tính này đã tồn tại trong sản phẩm");
        }
    }

    private Product getProduct(Integer productId) {
        return productRepository.findById(productId)
                .filter(p -> Boolean.FALSE.equals(p.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product id = " + productId));
    }

    private ProductAttribute getEntity(Integer id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy attribute id = " + id));
    }

    private ProductAttributeResponse toResponse(ProductAttribute attribute) {
        return ProductAttributeResponse.builder()
                .id(attribute.getId())
                .productId(attribute.getProduct().getId())
                .name(attribute.getName())
                .value(attribute.getValue())
                .sortOrder(attribute.getSortOrder())
                .createdAt(attribute.getCreatedAt())
                .build();
    }
}
