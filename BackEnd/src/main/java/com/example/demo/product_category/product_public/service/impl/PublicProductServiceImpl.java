package com.example.demo.product_category.product_public.service.impl;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import com.example.demo.product_category.product.repository.ProductSpecifications;
import com.example.demo.product_category.product_public.dto.PublicProductDetailResponse;
import com.example.demo.product_category.product_public.dto.PublicProductSummaryResponse;
import com.example.demo.product_category.product_public.service.PublicProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicProductServiceImpl implements PublicProductService {
    private final ProductRepository productRepository;

    @Override
    public PageResponse<PublicProductSummaryResponse> search(int page,
                                                             int size,
                                                             String keyword,
                                                             Set<Integer> categoryIds,
                                                             String brand,
                                                             ProductType productType,
                                                             TargetGender targetGender,
                                                             BigDecimal minPrice,
                                                             BigDecimal maxPrice,
                                                             Boolean inStock,
                                                             String sort) {
        Specification<Product> specification = Specification.where(ProductSpecifications.notDeleted())
                .and(ProductSpecifications.displayStatus(ProductDisplayStatus.HIENTHI))
                .and(ProductSpecifications.keyword(keyword))
                .and(ProductSpecifications.categoryIds(categoryIds))
                .and(ProductSpecifications.brand(brand))
                .and(ProductSpecifications.productType(productType))
                .and(ProductSpecifications.targetGender(targetGender))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice))
                .and(ProductSpecifications.onlyInStock(inStock));

        List<PublicProductSummaryResponse> all = productRepository.findAll(specification).stream()
                .map(ProductMapper::toPublicSummary)
                .filter(item -> Boolean.TRUE.equals(inStock) ? item.getTotalStock() != null && item.getTotalStock() > 0 : true)
                .sorted(buildComparator(sort))
                .toList();

        int fromIndex = Math.min(page * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());
        List<PublicProductSummaryResponse> content = fromIndex >= toIndex ? List.of() : all.subList(fromIndex, toIndex);

        return PageResponse.<PublicProductSummaryResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(all.size())
                .totalPages((int) Math.ceil(all.isEmpty() ? 0 : (double) all.size() / size))
                .last(toIndex >= all.size())
                .build();
    }

    @Override
    public PublicProductDetailResponse getDetail(Integer id) {
        Product product = productRepository.findDetailById(id)
                .filter(p -> p.getDisplayStatus() == ProductDisplayStatus.HIENTHI)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm đang bán"));
        return ProductMapper.toPublicDetail(product);
    }

    private Comparator<PublicProductSummaryResponse> buildComparator(String sort) {
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("newest")) {
            return Comparator.comparing(PublicProductSummaryResponse::getId, Comparator.nullsLast(Integer::compareTo)).reversed();
        }
        if (sort.equalsIgnoreCase("price_asc")) {
            return Comparator.comparing(PublicProductSummaryResponse::getMinPrice, Comparator.nullsLast(BigDecimal::compareTo));
        }
        if (sort.equalsIgnoreCase("price_desc")) {
            return Comparator.comparing(PublicProductSummaryResponse::getMinPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed();
        }
        if (sort.equalsIgnoreCase("name_asc")) {
            return Comparator.comparing(PublicProductSummaryResponse::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        if (sort.equalsIgnoreCase("name_desc")) {
            return Comparator.comparing(PublicProductSummaryResponse::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed();
        }
        return Comparator.comparing(PublicProductSummaryResponse::getId, Comparator.nullsLast(Integer::compareTo)).reversed();
    }
}
