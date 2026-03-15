package com.example.demo.product_public.controller;

import com.example.demo.product_category.image.entity.ProductImage;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_public.dto.PublicProductSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/public/products")
public class PublicProductController {
    private final ProductRepository productRepository;

    public PublicProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<PublicProductSummaryResponse> getProducts() {
        return productRepository.findAll().stream()
                .filter(p -> Boolean.FALSE.equals(p.getDeleted()))
                .map(this::toSummary)
                .toList();
    }

    @GetMapping("/{id}")
    public Product getDetail(@PathVariable Integer id) {
        return productRepository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    private PublicProductSummaryResponse toSummary(Product product) {
        ProductVariant firstVariant = product.getVariants().stream()
                .sorted(Comparator.comparing(ProductVariant::getId))
                .findFirst()
                .orElse(null);
        ProductImage firstImage = product.getImages().stream()
                .sorted(Comparator.comparing(ProductImage::getId))
                .findFirst()
                .orElse(null);

        PublicProductSummaryResponse response = new PublicProductSummaryResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setImageUrl(firstImage != null ? firstImage.getImageUrl() : null);
        response.setPrice(firstVariant != null ? firstVariant.getPrice() : BigDecimal.ZERO);
        response.setSalePrice(firstVariant != null ? firstVariant.getSalePrice() : null);
        return response;
    }
}
