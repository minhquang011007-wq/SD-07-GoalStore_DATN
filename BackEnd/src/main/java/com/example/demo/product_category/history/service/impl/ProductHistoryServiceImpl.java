package com.example.demo.product_category.history.service.impl;

import com.example.demo.product_category.history.dto.ProductHistoryResponse;
import com.example.demo.product_category.history.entity.ProductHistory;
import com.example.demo.product_category.history.repository.ProductHistoryRepository;
import com.example.demo.product_category.history.service.ProductHistoryService;
import com.example.demo.product_category.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductHistoryServiceImpl implements ProductHistoryService {
    private final ProductHistoryRepository repository;

    @Override
    public void log(Product product, String action, String note) {
        repository.save(ProductHistory.builder()
                .product(product)
                .action(action)
                .note(note)
                .changedBy(resolveUsername())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductHistoryResponse> findByProduct(Integer productId) {
        return repository.findByProductIdOrderByChangedAtDesc(productId).stream()
                .map(h -> ProductHistoryResponse.builder()
                        .id(h.getId())
                        .productId(h.getProduct().getId())
                        .productName(h.getProduct().getName())
                        .action(h.getAction())
                        .note(h.getNote())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt())
                        .build())
                .toList();
    }

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) return "SYSTEM";
        return auth.getName();
    }
}
