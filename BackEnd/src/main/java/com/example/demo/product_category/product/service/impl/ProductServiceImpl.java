package com.example.demo.product_category.product.service.impl;

import com.example.demo.product_category.category.entity.Category;
import com.example.demo.product_category.category.repository.CategoryRepository;
import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.common.exception.BadRequestException;
import com.example.demo.product_category.common.exception.ResourceNotFoundException;
import com.example.demo.product_category.common.mapper.ProductMapper;
import com.example.demo.product_category.history.service.ProductHistoryService;
import com.example.demo.product_category.product.dto.*;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.product.repository.ProductRepository;
import com.example.demo.product_category.product.repository.ProductSpecifications;
import com.example.demo.product_category.product.service.ProductService;
import com.example.demo.product_category.history.repository.ProductHistoryRepository;
import com.example.demo.product_category.tag.entity.Tag;
import com.example.demo.product_category.tag.repository.TagRepository;
import com.example.demo.product_category.variant.entity.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductHistoryService productHistoryService;
    private final ProductHistoryRepository productHistoryRepository;

    @Override
    public ProductDetailResponse create(ProductRequest request) {
        validateBaseSku(request.getBaseSku(), null);
        Product product = Product.builder()
                .name(request.getName().trim())
                .baseSku(request.getBaseSku().trim().toUpperCase())
                .brand(request.getBrand())
                .season(request.getSeason())
                .productType(request.getProductType())
                .targetGender(request.getTargetGender())
                .material(request.getMaterial())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .displayStatus(request.getDisplayStatus() == null ? ProductDisplayStatus.HIENTHI : request.getDisplayStatus())
                .build();
        product.setCategories(resolveCategories(request.getCategoryIds()));
        product.setTags(resolveTags(request.getTagIds()));
        Product saved = productRepository.save(product);
        productHistoryService.log(saved, "CREATE", "Tạo sản phẩm mới: " + saved.getName());
        return ProductMapper.toDetail(saved);
    }

    @Override
    public ProductDetailResponse update(Integer id, ProductRequest request) {
        Product product = getEntity(id);
        validateBaseSku(request.getBaseSku(), id);
        product.setName(request.getName().trim());
        product.setBaseSku(request.getBaseSku().trim().toUpperCase());
        product.setBrand(request.getBrand());
        product.setSeason(request.getSeason());
        product.setProductType(request.getProductType());
        product.setTargetGender(request.getTargetGender());
        product.setMaterial(request.getMaterial());
        product.setDescription(request.getDescription());
        product.setReleaseYear(request.getReleaseYear());
        product.setDisplayStatus(request.getDisplayStatus() == null ? ProductDisplayStatus.HIENTHI : request.getDisplayStatus());
        product.getCategories().clear();
        product.getCategories().addAll(resolveCategories(request.getCategoryIds()));
        product.getTags().clear();
        product.getTags().addAll(resolveTags(request.getTagIds()));
        Product saved = productRepository.save(product);
        productHistoryService.log(saved, "UPDATE", "Cập nhật đầy đủ thông tin sản phẩm");
        return ProductMapper.toDetail(saved);
    }

    @Override
    public ProductDetailResponse quickUpdate(Integer id, ProductQuickUpdateRequest request) {
        Product product = getEntity(id);
        if (request.getName() != null && !request.getName().isBlank()) product.setName(request.getName().trim());
        if (request.getDisplayStatus() != null) product.setDisplayStatus(request.getDisplayStatus());
        if (request.getBrand() != null) product.setBrand(request.getBrand().trim());
        if (request.getMaterial() != null) product.setMaterial(request.getMaterial().trim());
        if (request.getSeason() != null) product.setSeason(request.getSeason().trim());
        Product saved = productRepository.save(product);
        productHistoryService.log(saved, "QUICK_UPDATE", "Chỉnh sửa nhanh sản phẩm");
        return ProductMapper.toDetail(saved);
    }

    @Override
    public List<ProductDetailResponse> batchUpdate(ProductBatchUpdateRequest request) {
        List<ProductDetailResponse> responses = new ArrayList<>();
        for (ProductBatchUpdateItemRequest item : request.getItems()) {
            Product product = getEntity(item.getId());
            if (item.getDisplayStatus() != null) product.setDisplayStatus(item.getDisplayStatus());
            if (item.getBrand() != null) product.setBrand(item.getBrand().trim());
            if (item.getSeason() != null) product.setSeason(item.getSeason().trim());
            if (item.getMaterial() != null) product.setMaterial(item.getMaterial().trim());
            Product saved = productRepository.save(product);
            productHistoryService.log(saved, "BATCH_UPDATE", "Cập nhật hàng loạt sản phẩm");
            responses.add(ProductMapper.toDetail(saved));
        }
        return responses;
    }

    @Override
    public void delete(Integer id) {
        Product product = getEntity(id);
        product.setDeleted(true);
        product.setDisplayStatus(ProductDisplayStatus.AN);
        Product saved = productRepository.save(product);
        productHistoryService.log(saved, "DELETE", "Ẩn/xóa mềm sản phẩm");
    }

    @Override
    public void hardDelete(Integer id) {
        Product product = getEntity(id);
        productHistoryRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    @Override
    public ProductDetailResponse hideWhenOutOfStock(Integer id) {
        Product product = getEntity(id);
        int totalStock = product.getVariants() == null ? 0 : product.getVariants().stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getStockQuantity() != null)
                .filter(v -> v.getStockQuantity() > 0)
                .mapToInt(ProductVariant::getStockQuantity)
                .sum();
        if (totalStock > 0) {
            throw new BadRequestException("Sản phẩm vẫn còn hàng, không thể ẩn tự động");
        }
        product.setDisplayStatus(ProductDisplayStatus.AN);
        Product saved = productRepository.save(product);
        productHistoryService.log(saved, "HIDE_OUT_OF_STOCK", "Ẩn sản phẩm do hết hàng");
        return ProductMapper.toDetail(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getDetail(Integer id) {
        return ProductMapper.toDetail(productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product id = " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> search(int page, int size, String keyword, Set<Integer> categoryIds,
                                                       Set<Integer> tagIds, ProductDisplayStatus displayStatus,
                                                       String brand, String material, Boolean inStock,
                                                       VariantStockStatus stockStatus, Boolean hideOutOfStock,
                                                       Integer createdWithinDays, String sort,
                                                       ProductType productType, TargetGender targetGender,
                                                       Integer releaseYear, BigDecimal minPrice, BigDecimal maxPrice,
                                                       Boolean categoryMatchAll) {
        Specification<Product> specification = Specification.where(ProductSpecifications.notDeleted())
                .and(ProductSpecifications.keyword(keyword))
                .and(ProductSpecifications.categoryIds(categoryIds))
                .and(ProductSpecifications.tagIds(tagIds))
                .and(ProductSpecifications.displayStatus(displayStatus))
                .and(ProductSpecifications.brand(brand))
                .and(ProductSpecifications.material(material))
                .and(ProductSpecifications.onlyInStock(inStock))
                .and(ProductSpecifications.stockStatus(stockStatus))
                .and(ProductSpecifications.hideOutOfStock(hideOutOfStock))
                .and(ProductSpecifications.createdWithinDays(createdWithinDays))
                .and(ProductSpecifications.productType(productType))
                .and(ProductSpecifications.targetGender(targetGender))
                .and(ProductSpecifications.releaseYear(releaseYear))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice));

        Map<Integer, Long> soldMap = buildSoldMap();
        List<ProductSummaryResponse> all = productRepository.findAll(specification).stream()
                .filter(p -> !Boolean.TRUE.equals(categoryMatchAll) || categoryIds == null || categoryIds.isEmpty() ||
                        p.getCategories().stream().map(c -> c.getId()).collect(java.util.stream.Collectors.toSet()).containsAll(categoryIds))
                .map(p -> ProductMapper.toSummary(p, soldMap.getOrDefault(p.getId(), 0L)))
                .sorted(buildComparator(sort))
                .toList();

        int fromIndex = Math.min(page * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());
        List<ProductSummaryResponse> content = fromIndex >= toIndex ? List.of() : all.subList(fromIndex, toIndex);

        return PageResponse.<ProductSummaryResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(all.size())
                .totalPages((int) Math.ceil(all.isEmpty() ? 0 : (double) all.size() / size))
                .last(toIndex >= all.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> topSelling() {
        Map<Integer, Long> soldMap = buildSoldMap();
        return productRepository.findTopSellingProducts().stream()
                .map(p -> ProductMapper.toSummary(p, soldMap.getOrDefault(p.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> newest(int limit) {
        Map<Integer, Long> soldMap = buildSoldMap();
        return productRepository.findNewestProducts(org.springframework.data.domain.PageRequest.of(0, limit)).stream()
                .map(p -> ProductMapper.toSummary(p, soldMap.getOrDefault(p.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findByCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Không tìm thấy category id = " + categoryId);
        }
        Map<Integer, Long> soldMap = buildSoldMap();
        return productRepository.findAll(Specification.where(ProductSpecifications.notDeleted())
                .and(ProductSpecifications.categoryIds(Set.of(categoryId))))
                .stream()
                .map(p -> ProductMapper.toSummary(p, soldMap.getOrDefault(p.getId(), 0L)))
                .sorted(buildComparator("newest"))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findByTag(Integer tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Không tìm thấy tag id = " + tagId);
        }
        Map<Integer, Long> soldMap = buildSoldMap();
        return productRepository.findAll(Specification.where(ProductSpecifications.notDeleted())
                .and(ProductSpecifications.tagIds(Set.of(tagId))))
                .stream()
                .map(p -> ProductMapper.toSummary(p, soldMap.getOrDefault(p.getId(), 0L)))
                .sorted(buildComparator("newest"))
                .toList();
    }

    private Comparator<ProductSummaryResponse> buildComparator(String sort) {
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("newest")) {
            return Comparator.comparing(ProductSummaryResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }
        if (sort.equalsIgnoreCase("oldest")) {
            return Comparator.comparing(ProductSummaryResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if (sort.equalsIgnoreCase("name_asc")) {
            return Comparator.comparing(ProductSummaryResponse::getName, String.CASE_INSENSITIVE_ORDER);
        }
        if (sort.equalsIgnoreCase("name_desc")) {
            return Comparator.comparing(ProductSummaryResponse::getName, String.CASE_INSENSITIVE_ORDER).reversed();
        }
        if (sort.equalsIgnoreCase("price_asc")) {
            return Comparator.comparing(ProductSummaryResponse::getMinPrice, Comparator.nullsLast(BigDecimal::compareTo));
        }
        if (sort.equalsIgnoreCase("price_desc")) {
            return Comparator.comparing(ProductSummaryResponse::getMinPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed();
        }
        if (sort.equalsIgnoreCase("best_selling")) {
            return Comparator.comparing(ProductSummaryResponse::getSoldQuantity, Comparator.nullsLast(Long::compareTo)).reversed()
                    .thenComparing(ProductSummaryResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }
        return Comparator.comparing(ProductSummaryResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
    }

    private Map<Integer, Long> buildSoldMap() {
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : productRepository.findProductSales()) {
            map.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return map;
    }

    private void validateBaseSku(String baseSku, Integer id) {
        if (baseSku == null || baseSku.isBlank()) throw new BadRequestException("SKU chuẩn không được để trống");
        boolean exists = id == null ? productRepository.existsByBaseSkuIgnoreCase(baseSku.trim())
                : productRepository.existsByBaseSkuIgnoreCaseAndIdNot(baseSku.trim(), id);
        if (exists) throw new BadRequestException("SKU chuẩn đã tồn tại");
    }

    private Set<Category> resolveCategories(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new LinkedHashSet<>();
        List<Category> categories = categoryRepository.findByIdIn(ids);
        if (categories.size() != ids.size()) {
            Set<Integer> foundIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
            Set<Integer> missing = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new ResourceNotFoundException("Không tìm thấy category ids: " + missing);
        }
        return new LinkedHashSet<>(categories);
    }

    private Set<Tag> resolveTags(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new LinkedHashSet<>();
        List<Tag> tags = tagRepository.findByIdIn(ids);
        if (tags.size() != ids.size()) {
            Set<Integer> foundIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
            Set<Integer> missing = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new ResourceNotFoundException("Không tìm thấy tag ids: " + missing);
        }
        return new LinkedHashSet<>(tags);
    }

    private Product getEntity(Integer id) {
        return productRepository.findById(id)
                .filter(p -> Boolean.FALSE.equals(p.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy product id = " + id));
    }
}
