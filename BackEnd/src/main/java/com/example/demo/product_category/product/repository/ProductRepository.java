package com.example.demo.product_category.product.repository;

import com.example.demo.product_category.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    boolean existsByBaseSkuIgnoreCase(String baseSku);

    boolean existsByBaseSkuIgnoreCaseAndIdNot(String baseSku, Integer id);

    @EntityGraph(attributePaths = {"categories", "tags", "images", "variants"})
    @Query("select p from Product p where p.id = :id and p.deleted = false")
    Optional<Product> findDetailById(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"categories", "tags", "images", "variants"})
    @Query("select distinct p from Product p where p.deleted = false order by p.createdAt desc")
    List<Product> findNewestProducts(Pageable pageable);

    @Query(value = """
        select top 10 p.*
        from Products p
        join Product_Variants pv on pv.product_id = p.id
        left join Order_Items oi on oi.variant_id = pv.id
        where p.is_deleted = 0
        group by p.id, p.ten_san_pham, p.sku_chuan, p.thuong_hieu, p.mua_bo_suu_tap, p.loai_san_pham, p.doi_tuong,
                 p.chat_lieu, p.mo_ta, p.nam_phien_ban, p.trang_thai, p.is_deleted, p.created_at
        order by coalesce(sum(oi.quantity), 0) desc, p.created_at desc
        """, nativeQuery = true)
    List<Product> findTopSellingProducts();

    @Query(value = """
        select p.id as product_id, coalesce(sum(oi.quantity), 0) as sold_quantity
        from Products p
        join Product_Variants pv on pv.product_id = p.id
        left join Order_Items oi on oi.variant_id = pv.id
        where p.is_deleted = 0
        group by p.id
        """, nativeQuery = true)
    List<Object[]> findProductSales();
}