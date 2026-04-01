package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByTenContainingIgnoreCase(String ten);

    List<Customer> findByLoaiKhach(String loaiKhach);

    List<Customer> findByEmail(String email);

    List<Customer> findBySdt(String sdt);

    boolean existsByEmailIgnoreCase(String email);

    java.util.Optional<Customer> findFirstByEmailAndIsDeletedFalse(String email);

    java.util.Optional<Customer> findFirstBySdtAndIsDeletedFalse(String sdt);

    @Query("""
        SELECT c FROM Customer c
        WHERE
            lower(c.ten) LIKE lower(concat('%', :keyword, '%'))
            OR lower(coalesce(c.email, '')) LIKE lower(concat('%', :keyword, '%'))
            OR coalesce(c.sdt, '') LIKE concat('%', :keyword, '%')
    """)
    List<Customer> searchAll(String keyword);

    @Query(value = """
        SELECT
            c.id,
            c.ten,
            c.email,
            c.sdt,
            c.ngay_sinh,
            c.loai_khach,
            c.diem_tich_luy,
            c.ghi_chu,
            c.created_at,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN 1 ELSE 0 END), 0) AS tong_don_hang,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN o.total ELSE 0 END), 0) AS tong_chi_tieu,
            MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END) AS lan_mua_cuoi
        FROM Customers c
        LEFT JOIN Orders o ON o.customer_id = c.id
        GROUP BY
            c.id, c.ten, c.email, c.sdt, c.ngay_sinh, c.loai_khach,
            c.diem_tich_luy, c.ghi_chu, c.created_at
        ORDER BY c.id DESC
    """, nativeQuery = true)
    List<Object[]> getCustomerSummaryRows();

    @Query(value = """
        SELECT
            c.id,
            c.ten,
            c.email,
            c.sdt,
            c.loai_khach,
            c.diem_tich_luy,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN 1 ELSE 0 END), 0) AS tong_don_hang,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN o.total ELSE 0 END), 0) AS tong_chi_tieu,
            MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END) AS lan_mua_cuoi
        FROM Customers c
        LEFT JOIN Orders o ON o.customer_id = c.id
        GROUP BY c.id, c.ten, c.email, c.sdt, c.loai_khach, c.diem_tich_luy
        ORDER BY tong_chi_tieu DESC, c.id DESC
    """, nativeQuery = true)
    List<Object[]> getTopSpendingRows();

    @Query(value = """
        SELECT
            c.id,
            c.ten,
            c.email,
            c.sdt,
            c.loai_khach,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN 1 ELSE 0 END), 0) AS tong_don_hang,
            COALESCE(SUM(CASE WHEN o.status <> 'HUY' THEN o.total ELSE 0 END), 0) AS tong_chi_tieu,
            MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END) AS lan_mua_cuoi,
            CASE
                WHEN MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END) IS NULL
                    THEN DATEDIFF(DAY, c.created_at, GETDATE())
                ELSE DATEDIFF(DAY, MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END), GETDATE())
            END AS so_ngay_khong_mua
        FROM Customers c
        LEFT JOIN Orders o ON o.customer_id = c.id
        GROUP BY c.id, c.ten, c.email, c.sdt, c.loai_khach, c.created_at
        ORDER BY so_ngay_khong_mua DESC, c.id DESC
    """, nativeQuery = true)
    List<Object[]> getInactiveCustomerRows();

    @Query(value = """
        SELECT
            c.id,
            c.ten,
            c.loai_khach,
            MAX(CASE WHEN o.status <> 'HUY' THEN o.order_date ELSE NULL END) AS lan_mua_cuoi,
            COALESCE(SUM(
                CASE
                    WHEN o.status <> 'HUY'
                         AND o.order_date >= DATEADD(DAY, -:days, GETDATE())
                    THEN 1 ELSE 0
                END
            ), 0) AS so_don_trong_ky,
            COALESCE(SUM(
                CASE
                    WHEN o.status <> 'HUY'
                         AND o.order_date >= DATEADD(DAY, -:days, GETDATE())
                    THEN o.total ELSE 0
                END
            ), 0) AS chi_tieu_trong_ky
        FROM Customers c
        LEFT JOIN Orders o ON o.customer_id = c.id
        GROUP BY c.id, c.ten, c.loai_khach
        HAVING COALESCE(SUM(
            CASE
                WHEN o.status <> 'HUY'
                     AND o.order_date >= DATEADD(DAY, -:days, GETDATE())
                THEN 1 ELSE 0
            END
        ), 0) > 0
        ORDER BY chi_tieu_trong_ky DESC, so_don_trong_ky DESC, c.id DESC
    """, nativeQuery = true)
    List<Object[]> getCustomerActivityRows(Integer days);
}