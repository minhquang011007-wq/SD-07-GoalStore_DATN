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
DECLARE @HASH VARCHAR(255) = '$2a$10$ealL0Qbu7OfvmSe6lQVcRu1Oq1kEobvnwfP8R0RFPrdWrQ3FebfXu';

INSERT INTO Users(username, email, password, role, trang_thai)
VALUES
('admin',   'admin@gmail.com',   @HASH, 'ADMIN',     'ACTIVE'),
('sales01', 'sales01@gmail.com', @HASH, 'SALES',     'ACTIVE'),
('inv01',   'inv01@gmail.com',   @HASH, 'INVENTORY', 'ACTIVE');
GO

/* ================================
   SEED CATEGORIES
================================ */
INSERT INTO Categories (ten_danh_muc, mo_ta, hinh_anh)
VALUES
(N'Áo bóng đá', N'Danh mục áo bóng đá', '/images/category/ao.jpg'),
(N'Quần bóng đá', N'Danh mục quần bóng đá', '/images/category/quan.jpg'),
(N'Vớ thể thao', N'Danh mục vớ thể thao', '/images/category/vo.jpg'),
(N'Găng tay thủ môn', N'Danh mục găng tay', '/images/category/gangtay.jpg');
GO

/* ================================
   SEED TAGS
================================ */
INSERT INTO Tags (ten_tag, mo_ta)
VALUES
(N'Câu lạc bộ', N'Tag câu lạc bộ'),
(N'Đội tuyển', N'Tag đội tuyển'),
(N'Giải đấu', N'Tag giải đấu'),
(N'Sân vận động', N'Tag sân vận động'),
(N'Khuyến mãi', N'Tag khuyến mãi');
GO

SELECT id, username, email, role, trang_thai, created_at FROM Users;
GO

/* ================================
   PERMISSIONS
================================ */
CREATE TABLE Permissions (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name NVARCHAR(150) NOT NULL,
    module VARCHAR(50) NOT NULL
);
GO

/* ================================
   ROLE_PERMISSIONS
================================ */
CREATE TABLE Role_Permissions (
    id INT IDENTITY PRIMARY KEY,
    role VARCHAR(20) NOT NULL,
    permission_id INT NOT NULL,
    CONSTRAINT FK_RolePermissions_Permission
        FOREIGN KEY (permission_id) REFERENCES Permissions(id) ON DELETE CASCADE,
    CONSTRAINT UQ_RolePermissions UNIQUE (role, permission_id),
    CONSTRAINT CK_RolePermissions_Role CHECK (role IN ('ADMIN','SALES','INVENTORY'))
);
GO

CREATE INDEX IX_RolePermissions_Role ON Role_Permissions(role);
GO

/* ================================
   SEED PERMISSIONS
================================ */
INSERT INTO Permissions(code, name, module)
VALUES
('USER_VIEW',           N'Xem danh sách user',         'USER'),
('USER_CREATE',         N'Tạo user',                   'USER'),
('USER_UPDATE',         N'Cập nhật user',              'USER'),
('USER_DELETE',         N'Xóa user',                   'USER'),
('USER_CHANGE_STATUS',  N'Đổi trạng thái user',        'USER'),
('USER_RESET_PASSWORD', N'Reset mật khẩu user',        'USER'),

('AUDIT_VIEW',          N'Xem audit log',              'AUDIT'),

('PRODUCT_VIEW',        N'Xem sản phẩm',               'PRODUCT'),
('PRODUCT_CREATE',      N'Tạo sản phẩm',               'PRODUCT'),
('PRODUCT_UPDATE',      N'Cập nhật sản phẩm',          'PRODUCT'),
('PRODUCT_DELETE',      N'Xóa sản phẩm',               'PRODUCT'),

('ORDER_VIEW',          N'Xem đơn hàng',               'ORDER'),
('ORDER_CREATE',        N'Tạo đơn hàng',               'ORDER'),
('ORDER_UPDATE',        N'Cập nhật đơn hàng',          'ORDER'),
('ORDER_CHANGE_STATUS', N'Đổi trạng thái đơn hàng',    'ORDER'),

('CUSTOMER_VIEW',       N'Xem khách hàng',             'CUSTOMER'),
('CUSTOMER_CREATE',     N'Tạo khách hàng',             'CUSTOMER'),
('CUSTOMER_UPDATE',     N'Cập nhật khách hàng',        'CUSTOMER'),

('ROLE_PERMISSION_VIEW',   N'Xem phân quyền',          'ROLE'),
('ROLE_PERMISSION_UPDATE', N'Cập nhật phân quyền',     'ROLE');
GO

/* ================================
   SEED ROLE PERMISSIONS
================================ */
INSERT INTO Role_Permissions(role, permission_id)
SELECT 'ADMIN', id FROM Permissions;
GO

INSERT INTO Role_Permissions(role, permission_id)
SELECT 'SALES', id
FROM Permissions
WHERE code IN (
    'ORDER_VIEW','ORDER_CREATE','ORDER_UPDATE','ORDER_CHANGE_STATUS',
    'CUSTOMER_VIEW','CUSTOMER_CREATE','CUSTOMER_UPDATE',
    'PRODUCT_VIEW'
);
GO

INSERT INTO Role_Permissions(role, permission_id)
SELECT 'INVENTORY', id
FROM Permissions
WHERE code IN (
    'PRODUCT_VIEW','PRODUCT_CREATE','PRODUCT_UPDATE','PRODUCT_DELETE'
);
GO

/* ================================ LOGIN ATTEMPTS ================================ */
CREATE TABLE Login_Attempts (
    id BIGINT IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    user_id INT NULL,
    success BIT NOT NULL,
    ip_address VARCHAR(50) NULL,
    user_agent NVARCHAR(255) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_LoginAttempts_User FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO

CREATE INDEX IX_LoginAttempts_Time ON Login_Attempts(created_at DESC);
CREATE INDEX IX_LoginAttempts_Username ON Login_Attempts(username);
GO

/* ================================ SECURITY ALERTS ================================ */
CREATE TABLE Security_Alerts (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id INT NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('INFO','WARNING','CRITICAL')),
    message NVARCHAR(500) NOT NULL,
    ip_address VARCHAR(50) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    resolved BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_SecurityAlerts_User FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO

CREATE INDEX IX_SecurityAlerts_Time ON Security_Alerts(created_at DESC);
CREATE INDEX IX_SecurityAlerts_Resolved ON Security_Alerts(resolved);
GO

/* ================================
   SEED PRODUCTS
================================ */
INSERT INTO Products
(ten_san_pham, sku_chuan, thuong_hieu, mua_bo_suu_tap, loai_san_pham, doi_tuong, chat_lieu, nam_phien_ban, mo_ta, trang_thai, is_deleted)
VALUES
(N'Nike Dri-FIT Legend 2026', 'NIKE-LEGEND-2026', N'Nike', N'2026', 'AO', 'NAM', N'Dri-FIT', 2026, N'Áo thể thao Nike chính hãng', 'HIENTHI', 0),
(N'Adidas Predator Pro 2025', 'ADIDAS-PREDATOR-2025', N'Adidas', N'2025', 'AO', 'NAM', N'Polyester', 2025, N'Áo thể thao Adidas Predator', 'HIENTHI', 0);
GO

/* ================================
   SEED PRODUCT - CATEGORY
================================ */
INSERT INTO Product_Categories (product_id, category_id)
VALUES
(1, 1),
(2, 1);
GO

/* ================================
   SEED PRODUCT - TAG
================================ */
INSERT INTO Product_Tags (product_id, tag_id)
VALUES
(1, 1),
(1, 3),
(2, 2);
GO

/* ================================
   SEED PRODUCT VARIANTS
================================ */
INSERT INTO Product_Variants
(product_id, sku, size, mau_sac, gia_ban, gia_khuyen_mai, ton_kho, trang_thai, is_deleted)
VALUES
(1, 'NIKE-LEGEND-2026-M-BLUE', 'M', N'Xanh', 800000, 750000, 10, 'CON_HANG', 0),
(1, 'NIKE-LEGEND-2026-L-BLUE', 'L', N'Xanh', 800000, 750000, 5,  'CON_HANG', 0),
(1, 'NIKE-LEGEND-2026-M-RED',  'M', N'Đỏ',   800000, NULL,   0,  'HET_HANG', 0),
(2, 'ADIDAS-PREDATOR-2025-M-BLACK', 'M', N'Đen', 900000, 850000, 8, 'CON_HANG', 0),
(2, 'ADIDAS-PREDATOR-2025-L-BLACK', 'L', N'Đen', 900000, 850000, 4, 'CON_HANG', 0);
GO

/* ================================
   SEED PRODUCT IMAGES
================================ */
INSERT INTO Product_Images (product_id, url, is_avatar, sort_order, is_deleted)
VALUES
(1, '/images/products/nike1.jpg',   1, 0, 0),
(1, '/images/products/nike2.jpg',   0, 1, 0),
(1, '/images/products/nike3.jpg',   0, 2, 0),
(2, '/images/products/adidas1.jpg', 1, 0, 0),
(2, '/images/products/adidas2.jpg', 0, 1, 0);
GO

PRINT N'✅ DATABASE DATN recreated successfully and matched with backend.';
GO

SELECT * FROM Users;
SELECT * FROM Categories;
SELECT * FROM Tags;
SELECT * FROM Products;
SELECT * FROM Product_Variants;
SELECT * FROM Product_Images;
SELECT * FROM Product_History;
GO