package com.example.demo.product_category.common.mapper;

import com.example.demo.product_category.attribute.dto.ProductAttributeResponse;
import com.example.demo.product_category.attribute.entity.ProductAttribute;
import com.example.demo.product_category.category.dto.CategoryResponse;
import com.example.demo.product_category.category.entity.Category;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.image.dto.ProductImageResponse;
import com.example.demo.product_category.image.entity.ProductImage;
import com.example.demo.product_category.product.dto.ProductDetailResponse;
import com.example.demo.product_category.product.dto.ProductSummaryResponse;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product_public.dto.PublicProductDetailResponse;
import com.example.demo.product_category.product_public.dto.PublicProductSummaryResponse;
import com.example.demo.product_category.tag.dto.TagResponse;
import com.example.demo.product_category.tag.entity.Tag;
import com.example.demo.product_category.variant.dto.ProductVariantResponse;
import com.example.demo.product_category.variant.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ProductMapper {
    private ProductMapper() {}

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .productCount(category.getProducts() == null ? 0 : category.getProducts().stream().filter(p -> Boolean.FALSE.equals(p.getDeleted())).count())
                .build();
    }

    public static TagResponse toTagResponse(Tag tag) {
        return TagResponse.builder().id(tag.getId()).name(tag.getName()).description(tag.getDescription()).build();
    }


    public static ProductAttributeResponse toAttributeResponse(ProductAttribute attribute) {
        return ProductAttributeResponse.builder()
                .id(attribute.getId())
                .productId(attribute.getProduct() == null ? null : attribute.getProduct().getId())
                .name(attribute.getName())
                .value(attribute.getValue())
                .sortOrder(attribute.getSortOrder())
                .createdAt(attribute.getCreatedAt())
                .build();
    }

    public static ProductImageResponse toImageResponse(ProductImage image) {
        return ProductImageResponse.builder().id(image.getId()).imageUrl(image.getImageUrl()).avatar(image.getAvatar()).sortOrder(image.getSortOrder()).build();
    }

    public static ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId()).sku(variant.getSku()).size(variant.getSize()).color(variant.getColor())
                .price(variant.getPrice()).salePrice(variant.getSalePrice()).stockQuantity(variant.getStockQuantity())
                .stockStatus(variant.getStockStatus()).build();
    }

    public static ProductSummaryResponse toSummary(Product product) {
        return toSummary(product, 0L);
    }

    public static ProductSummaryResponse toSummary(Product product, Long soldQuantity) {
        List<ProductVariant> variants = product.getVariants() == null ? List.of() : product.getVariants().stream().toList();
        BigDecimal minPrice = variants.stream().map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice()).filter(java.util.Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
        BigDecimal maxPrice = variants.stream().map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice()).filter(java.util.Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
        int totalStock = variants.stream().map(ProductVariant::getStockQuantity).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).sum();
        String thumbnail = product.getImages() == null ? null : product.getImages().stream().filter(i -> Boolean.TRUE.equals(i.getAvatar())).map(ProductImage::getImageUrl).findFirst().orElse(product.getImages().stream().findFirst().map(ProductImage::getImageUrl).orElse(null));
        VariantStockStatus stockStatus = variants.stream().anyMatch(v -> v.getStockStatus() == VariantStockStatus.PRE_ORDER) ? VariantStockStatus.PRE_ORDER : totalStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG;
        return ProductSummaryResponse.builder()
                .id(product.getId()).name(product.getName()).baseSku(product.getBaseSku()).brand(product.getBrand())
                .season(product.getSeason()).productType(product.getProductType()).targetGender(product.getTargetGender())
                .material(product.getMaterial()).displayStatus(product.getDisplayStatus()).thumbnailUrl(thumbnail)
                .categoryNames(product.getCategories().stream().map(Category::getName).collect(Collectors.toCollection(java.util.LinkedHashSet::new)))
                .minPrice(minPrice).maxPrice(maxPrice).totalStock(totalStock).stockStatus(stockStatus)
                .soldQuantity(soldQuantity == null ? 0L : soldQuantity).createdAt(product.getCreatedAt()).build();
    }


    public static PublicProductSummaryResponse toPublicSummary(Product product) {
        List<ProductVariant> variants = product.getVariants() == null ? List.of() : product.getVariants().stream().toList();
        BigDecimal minPrice = variants.stream().map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice()).filter(java.util.Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
        BigDecimal maxPrice = variants.stream().map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice()).filter(java.util.Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
        ProductVariant firstVariant = variants.stream().sorted(Comparator.comparing(ProductVariant::getId)).findFirst().orElse(null);
        int totalStock = variants.stream().map(ProductVariant::getStockQuantity).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).sum();
        String thumbnail = product.getImages() == null ? null : product.getImages().stream().filter(i -> Boolean.TRUE.equals(i.getAvatar())).map(ProductImage::getImageUrl).findFirst().orElse(product.getImages().stream().findFirst().map(ProductImage::getImageUrl).orElse(null));
        VariantStockStatus stockStatus = variants.stream().anyMatch(v -> v.getStockStatus() == VariantStockStatus.PRE_ORDER) ? VariantStockStatus.PRE_ORDER : totalStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG;
        return PublicProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .baseSku(product.getBaseSku())
                .brand(product.getBrand())
                .imageUrl(thumbnail)
                .price(firstVariant != null ? firstVariant.getPrice() : minPrice)
                .salePrice(firstVariant != null ? firstVariant.getSalePrice() : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock)
                .stockStatus(stockStatus)
                .productType(product.getProductType())
                .targetGender(product.getTargetGender())
                .categoryNames(product.getCategories().stream().map(Category::getName).collect(Collectors.toCollection(java.util.LinkedHashSet::new)))
                .build();
    }

    public static PublicProductDetailResponse toPublicDetail(Product product) {
        ProductDetailResponse detail = toDetail(product);
        return PublicProductDetailResponse.builder()
                .id(detail.getId())
                .name(detail.getName())
                .baseSku(detail.getBaseSku())
                .brand(detail.getBrand())
                .season(detail.getSeason())
                .productType(detail.getProductType())
                .targetGender(detail.getTargetGender())
                .material(detail.getMaterial())
                .description(detail.getDescription())
                .releaseYear(detail.getReleaseYear())
                .displayStatus(detail.getDisplayStatus())
                .thumbnailUrl(detail.getThumbnailUrl())
                .createdAt(detail.getCreatedAt())
                .categories(detail.getCategories())
                .tags(detail.getTags())
                .images(detail.getImages())
                .variants(detail.getVariants())
                .attributes(detail.getAttributes())
                .build();
    }

    public static ProductDetailResponse toDetail(Product product) {
        String thumbnail = product.getImages() == null ? null : product.getImages().stream().filter(i -> Boolean.TRUE.equals(i.getAvatar())).map(ProductImage::getImageUrl).findFirst().orElse(product.getImages().stream().findFirst().map(ProductImage::getImageUrl).orElse(null));
        return ProductDetailResponse.builder()
                .id(product.getId()).name(product.getName()).baseSku(product.getBaseSku()).brand(product.getBrand())
                .season(product.getSeason()).productType(product.getProductType()).targetGender(product.getTargetGender())
                .material(product.getMaterial()).description(product.getDescription()).releaseYear(product.getReleaseYear())
                .displayStatus(product.getDisplayStatus()).thumbnailUrl(thumbnail).createdAt(product.getCreatedAt())
                .categories(product.getCategories().stream().map(ProductMapper::toCategoryResponse).toList())
                .tags(product.getTags().stream().map(ProductMapper::toTagResponse).toList())
                .images(product.getImages().stream().sorted(Comparator.comparing(ProductImage::getSortOrder).thenComparing(ProductImage::getId)).map(ProductMapper::toImageResponse).toList())
                .variants(product.getVariants().stream().sorted(Comparator.comparing(ProductVariant::getId)).map(ProductMapper::toVariantResponse).toList())
                .attributes(product.getAttributes().stream().sorted(Comparator.comparing(ProductAttribute::getSortOrder).thenComparing(ProductAttribute::getId)).map(ProductMapper::toAttributeResponse).toList())
                .build();
    }
}
