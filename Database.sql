/* ================================
   RESET DATABASE DATN
================================ */

IF DB_ID('DATN') IS NOT NULL
BEGIN
    ALTER DATABASE DATN SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE DATN;
END
GO

CREATE DATABASE DATN;
GO

USE DATN;
GO

/* ================================
   USERS
================================ */
CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('ADMIN','SALES','INVENTORY')),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (trang_thai IN ('ACTIVE','INACTIVE','LOCKED')),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ================================
   AUDIT LOG
================================ */
CREATE TABLE Audit_Log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NULL,
    action NVARCHAR(100) NOT NULL,
    entity NVARCHAR(50) NOT NULL,
    entity_id INT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    detail NVARCHAR(500) NULL,
    CONSTRAINT FK_AuditLog_User
        FOREIGN KEY (user_id) REFERENCES Users(id)
);
GO

CREATE INDEX IX_AuditLog_Time ON Audit_Log(created_at DESC);
GO

/* ================================
   CATEGORIES
================================ */
CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_danh_muc NVARCHAR(150) NOT NULL UNIQUE,
    mo_ta NVARCHAR(500) NULL,
    hinh_anh NVARCHAR(255) NULL,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* ================================
   TAGS
================================ */
CREATE TABLE Tags (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_tag NVARCHAR(100) NOT NULL UNIQUE,
    mo_ta NVARCHAR(255) NULL,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* ================================
   PRODUCTS
================================ */
CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_san_pham NVARCHAR(200) NOT NULL,
    sku_chuan VARCHAR(100) NOT NULL UNIQUE,
    thuong_hieu NVARCHAR(100) NULL,
    mua_bo_suu_tap NVARCHAR(100) NULL,
    loai_san_pham VARCHAR(30) NULL,
    doi_tuong VARCHAR(30) NULL,
    chat_lieu NVARCHAR(100) NULL,
    nam_phien_ban INT NULL,
    mo_ta NVARCHAR(MAX) NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'HIENTHI'
        CHECK (trang_thai IN ('HIENTHI','AN','NGUNG_BAN')),
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

CREATE INDEX IX_Products_Ten ON Products(ten_san_pham);
GO

/* ================================
   PRODUCT - CATEGORY
================================ */
CREATE TABLE Product_Categories (
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT PK_ProductCategories PRIMARY KEY (product_id, category_id),
    CONSTRAINT FK_PC_Product
        FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT FK_PC_Category
        FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_PC_Category ON Product_Categories(category_id);
GO

/* ================================
   PRODUCT - TAG
================================ */
CREATE TABLE Product_Tags (
    product_id INT NOT NULL,
    tag_id INT NOT NULL,
    CONSTRAINT PK_ProductTags PRIMARY KEY (product_id, tag_id),
    CONSTRAINT FK_PT_Product
        FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT FK_PT_Tag
        FOREIGN KEY (tag_id) REFERENCES Tags(id) ON DELETE CASCADE
);
GO

/* ================================
   PRODUCT IMAGES
================================ */
CREATE TABLE Product_Images (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    url NVARCHAR(255) NOT NULL,
    is_avatar BIT NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT FK_PI_Product
        FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_PI_Product ON Product_Images(product_id);
GO

/* ================================
   PRODUCT VARIANTS
================================ */
CREATE TABLE Product_Variants (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    sku VARCHAR(120) NOT NULL UNIQUE,
    size VARCHAR(10) NOT NULL,
    mau_sac NVARCHAR(50) NOT NULL,
    gia_ban DECIMAL(12,2) NOT NULL,
    gia_khuyen_mai DECIMAL(12,2) NULL,
    ton_kho INT NOT NULL DEFAULT 0,
    trang_thai VARCHAR(20) NOT NULL
        CHECK (trang_thai IN ('CON_HANG','HET_HANG','PRE_ORDER')),
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT FK_PV_Product
        FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_PV_Product ON Product_Variants(product_id);
GO

/* ================================
   PRODUCT HISTORY
================================ */
CREATE TABLE Product_History (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    action VARCHAR(30) NOT NULL,
    changed_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    changed_by INT NULL,
    note NVARCHAR(500) NULL,
    CONSTRAINT FK_PH_Product
        FOREIGN KEY (product_id) REFERENCES Products(id),
    CONSTRAINT FK_PH_User
        FOREIGN KEY (changed_by) REFERENCES Users(id)
);
GO

CREATE INDEX IX_PH_Product ON Product_History(product_id);
GO

CREATE TABLE Product_Attributes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    ten_thuoc_tinh NVARCHAR(100) NOT NULL,
    gia_tri NVARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT FK_PA_Product
        FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

/* ================================
   CUSTOMERS
================================ */
CREATE TABLE Customers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten NVARCHAR(150) NOT NULL,
    email VARCHAR(100) NULL,
    sdt VARCHAR(20) NULL,
    ngay_sinh DATE NULL,
    loai_khach VARCHAR(10) NOT NULL DEFAULT 'THUONG'
        CHECK (loai_khach IN ('VIP','THUONG')),
    diem_tich_luy INT NOT NULL DEFAULT 0,
    ghi_chu NVARCHAR(500) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

CREATE INDEX IX_Customers_Type ON Customers(loai_khach);
GO

CREATE INDEX IX_Customers_Points ON Customers(diem_tich_luy DESC);
GO

/* ================================
   ORDERS
================================ */
CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    staff_id INT NULL,
    order_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('MOI','DANG_XU_LY','DANG_GIAO','HOAN_TAT','HUY','TRA_HANG')),
    payment_method VARCHAR(20) NULL,
    channel VARCHAR(10) NULL,
    total DECIMAL(14,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_Orders_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(id),
    CONSTRAINT FK_Orders_Staff
        FOREIGN KEY (staff_id) REFERENCES Users(id)
);
GO

CREATE INDEX IX_Orders_Status ON Orders(status);
GO

CREATE INDEX IX_Orders_Date ON Orders(order_date DESC);
GO

/* ================================
   ORDER ITEMS
================================ */
CREATE TABLE Order_Items (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    CONSTRAINT FK_OI_Order
        FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_OI_Variant
        FOREIGN KEY (variant_id) REFERENCES Product_Variants(id)
);
GO

CREATE INDEX IX_OI_Order ON Order_Items(order_id);
GO

/* ================================
   RETURNS
================================ */
CREATE TABLE Returns (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    return_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    reason NVARCHAR(500) NULL,
    refund_total DECIMAL(14,2) NOT NULL DEFAULT 0,
    note NVARCHAR(500) NULL,
    CONSTRAINT FK_Returns_Order
        FOREIGN KEY (order_id) REFERENCES Orders(id)
);
GO

/* ================================
   SEED USERS
================================ */
DECLARE @HASH VARCHAR(255) = '$2a$10$LRVazmfhnMx4b4fbcYm8KeEFY8xEDayH2w952xpSr9v7u.Zu5Meoi';

INSERT INTO Users(username, email, password, role, trang_thai)
VALUES
('admin',   'admin@gmail.com',   @HASH, 'ADMIN',     'ACTIVE'),
('sales01', 'sales01@gmail.com', @HASH, 'SALES',     'ACTIVE'),
('inv01',   'inv01@gmail.com',   @HASH, 'INVENTORY', 'ACTIVE');
GO

/* ================================
   SEED THÊM CATEGORIES
================================ */
INSERT INTO Categories (ten_danh_muc, mo_ta, hinh_anh)
VALUES
(N'Áo tập luyện', N'Danh mục áo tập luyện', '/images/category/aotapluyen.jpg'),
(N'Áo khoác thể thao', N'Danh mục áo khoác thể thao', '/images/category/aokhoac.jpg'),
(N'Phụ kiện bóng đá', N'Danh mục phụ kiện bóng đá', '/images/category/phukien.jpg');
GO

/* ================================
   SEED THÊM TAGS
================================ */
INSERT INTO Tags (ten_tag, mo_ta)
VALUES
(N'Mới về', N'Sản phẩm mới nhập'),
(N'Bán chạy', N'Sản phẩm bán chạy'),
(N'Hot deal', N'Sản phẩm giảm giá mạnh'),
(N'Thủ môn', N'Sản phẩm dành cho thủ môn');
GO

/* ================================
   SEED THÊM PRODUCTS
================================ */
INSERT INTO Products
(ten_san_pham, sku_chuan, thuong_hieu, mua_bo_suu_tap, loai_san_pham, doi_tuong, chat_lieu, nam_phien_ban, mo_ta, trang_thai, is_deleted, updated_at)
VALUES
(N'Puma Ultra Training Tee 2026', 'PUMA-ULTRA-2026', N'Puma', N'2026', 'AO', 'NAM', N'Polyester', 2026, N'Áo tập luyện Puma Ultra thoáng khí', 'HIENTHI', 0, SYSDATETIME()),
(N'GoalKeeper Pro Gloves 2025', 'GK-GLOVES-2025', N'GoalStore', N'2025', 'GANG_TAY', 'NAM', N'Cao su + Latex', 2025, N'Găng tay thủ môn chuyên dụng', 'HIENTHI', 0, SYSDATETIME()),
(N'Active Sport Socks 2026', 'SOCKS-SPORT-2026', N'GoalStore', N'2026', 'VO', 'UNISEX', N'Cotton Blend', 2026, N'Vớ thể thao co giãn, thấm hút tốt', 'HIENTHI', 0, SYSDATETIME()),
(N'Nike Academy Pants 2025', 'NIKE-PANTS-2025', N'Nike', N'2025', 'QUAN', 'NAM', N'Polyester', 2025, N'Quần tập Nike Academy', 'AN', 0, SYSDATETIME()),
(N'Adidas Bench Jacket 2024', 'ADIDAS-JACKET-2024', N'Adidas', N'2024', 'KHAC', 'UNISEX', N'Windbreaker', 2024, N'Áo khoác nhẹ cho vận động viên', 'NGUNG_BAN', 0, SYSDATETIME());
GO

/* ================================
   SEED THÊM PRODUCT - CATEGORY
   ID cũ:
   Categories: 1..4
   Thêm mới:   5..7
   Products cũ: 1..2
   Thêm mới:    3..7
================================ */
INSERT INTO Product_Categories (product_id, category_id)
VALUES
(1, 1), -- Puma Ultra Training Tee -> Áo tập luyện
(2, 3), -- GoalKeeper Pro Gloves -> Phụ kiện bóng đá
(3, 3), -- Active Sport Socks -> Phụ kiện bóng đá
(4, 2), -- Nike Academy Pants -> Áo khoác thể thao (tạm)
(5, 2); -- Adidas Bench Jacket -> Áo khoác thể thao
GO

/* ================================
   SEED THÊM PRODUCT - TAG
   Tags cũ: 1..5
   Thêm mới: 6..9
================================ */
INSERT INTO Product_Tags (product_id, tag_id)
VALUES
(1, 1), -- Mới về
(1, 2), -- Bán chạy
(2, 4), -- Thủ môn
(2, 2), -- Bán chạy
(3, 1), -- Mới về
(3, 3), -- Hot deal
(4, 3), -- Hot deal
(5, 2); -- Bán chạy
GO

/* ================================
   SEED THÊM PRODUCT VARIANTS
   Variant cũ đang có id 1..5
================================ */
INSERT INTO Product_Variants
(product_id, sku, size, mau_sac, gia_ban, gia_khuyen_mai, ton_kho, trang_thai, is_deleted, updated_at)
VALUES
(1, 'PUMA-ULTRA-2026-S-WHITE', 'S', N'Trắng', 650000, 590000, 12, 'CON_HANG', 0, SYSDATETIME()),
(1, 'PUMA-ULTRA-2026-M-WHITE', 'M', N'Trắng', 650000, 590000, 7,  'CON_HANG', 0, SYSDATETIME()),
(1, 'PUMA-ULTRA-2026-L-WHITE', 'L', N'Trắng', 650000, NULL,   0,  'HET_HANG', 0, SYSDATETIME()),

(2, 'GK-GLOVES-2025-8-BLACK', '8', N'Đen', 550000, 520000, 6, 'CON_HANG', 0, SYSDATETIME()),
(2, 'GK-GLOVES-2025-9-BLACK', '9', N'Đen', 550000, 520000, 2, 'CON_HANG', 0, SYSDATETIME()),

(3, 'SOCKS-SPORT-2026-FREE-WHITE', 'FREE', N'Trắng', 120000, 99000, 30, 'CON_HANG', 0, SYSDATETIME()),
(3, 'SOCKS-SPORT-2026-FREE-BLACK', 'FREE', N'Đen',   120000, 99000, 18, 'CON_HANG', 0, SYSDATETIME()),

(4, 'NIKE-PANTS-2025-M-BLACK', 'M', N'Đen', 700000, 650000, 0, 'HET_HANG', 0, SYSDATETIME()),
(4, 'NIKE-PANTS-2025-L-BLACK', 'L', N'Đen', 700000, 650000, 0, 'HET_HANG', 0, SYSDATETIME()),

(5, 'ADIDAS-JACKET-2024-M-NAVY', 'M', N'Xanh navy', 1100000, 950000, 1, 'CON_HANG', 0, SYSDATETIME());
GO

/* ================================
   SEED THÊM PRODUCT IMAGES
================================ */
INSERT INTO Product_Images (product_id, url, is_avatar, sort_order, is_deleted, updated_at)
VALUES
(1, '/images/products/puma1.jpg',   1, 0, 0, SYSDATETIME()),
(1, '/images/products/puma2.jpg',   0, 1, 0, SYSDATETIME()),
(2, '/images/products/gloves1.jpg', 1, 0, 0, SYSDATETIME()),
(2, '/images/products/gloves2.jpg', 0, 1, 0, SYSDATETIME()),
(3, '/images/products/socks1.jpg',  1, 0, 0, SYSDATETIME()),
(4, '/images/products/pants1.jpg',  1, 0, 0, SYSDATETIME()),
(5, '/images/products/jacket1.jpg', 1, 0, 0, SYSDATETIME());
GO

/* ================================
   SEED PRODUCT ATTRIBUTES
================================ */
INSERT INTO Product_Attributes (product_id, ten_thuoc_tinh, gia_tri, sort_order, is_deleted, updated_at)
VALUES
(1, N'Dòng sản phẩm', N'Training Tee', 1, 0, SYSDATETIME()),
(1, N'Tính năng', N'Thoáng khí, nhanh khô', 2, 0, SYSDATETIME()),

(2, N'Vị trí sử dụng', N'Thủ môn', 1, 0, SYSDATETIME()),
(2, N'Độ bám', N'Cao', 2, 0, SYSDATETIME()),

(3, N'Độ co giãn', N'Tốt', 1, 0, SYSDATETIME()),
(3, N'Chất liệu', N'Cotton Blend', 2, 0, SYSDATETIME()),

(4, N'Phong cách', N'Tập luyện', 1, 0, SYSDATETIME()),
(5, N'Tính năng', N'Chống gió nhẹ', 1, 0, SYSDATETIME());
GO

SELECT * FROM Users;
SELECT * FROM Categories;
SELECT * FROM Tags;
SELECT * FROM Products;
SELECT * FROM Product_Variants;
SELECT * FROM Product_Images;
SELECT * FROM Product_History;
GO
