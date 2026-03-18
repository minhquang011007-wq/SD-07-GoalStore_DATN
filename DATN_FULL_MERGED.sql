/* =====================================================
   DATN - FULL DATABASE SETUP + SEED DATA FOR API TEST
   Gộp từ MOI.sql + ADDVAO.sql và bổ sung dữ liệu test
   ===================================================== */

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
   LOYALTY POINT HISTORY
================================ */
CREATE TABLE Loyalty_Point_History (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    points INT NOT NULL,
    type VARCHAR(20) NOT NULL
        CHECK (type IN ('ADD','SUBTRACT','BONUS','REFUND')),
    note NVARCHAR(255) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_LPH_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(id)
);
GO

CREATE INDEX IX_LPH_Customer ON Loyalty_Point_History(customer_id);
GO

/* ================================
   VIP PROGRAMS
================================ */
CREATE TABLE Vip_Programs (
    id INT IDENTITY(1,1) PRIMARY KEY,
    level_name VARCHAR(30) NOT NULL UNIQUE,
    min_points INT NOT NULL,
    min_spending DECIMAL(14,2) NOT NULL DEFAULT 0,
    benefit NVARCHAR(500) NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ================================
   VIP HISTORY
================================ */
CREATE TABLE Vip_History (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    old_level VARCHAR(30) NULL,
    new_level VARCHAR(30) NOT NULL,
    reason NVARCHAR(255) NULL,
    changed_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_VH_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(id)
);
GO

CREATE INDEX IX_VH_Customer ON Vip_History(customer_id);
GO

/* ================================
   REWARD RULES
================================ */
CREATE TABLE Reward_Rules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    reward_name NVARCHAR(150) NOT NULL,
    required_points INT NOT NULL,
    discount_value DECIMAL(12,2) NOT NULL DEFAULT 0,
    description NVARCHAR(500) NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ================================
   BIRTHDAY NOTIFICATION LOG
================================ */
CREATE TABLE Birthday_Notification_Log (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    sent_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    note NVARCHAR(255) NULL,
    CONSTRAINT FK_BNL_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(id)
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
('sales02', 'sales02@gmail.com', @HASH, 'SALES',     'ACTIVE'),
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

/* ================================
   SEED PRODUCTS
================================ */
INSERT INTO Products
(ten_san_pham, sku_chuan, thuong_hieu, mua_bo_suu_tap, loai_san_pham, doi_tuong, chat_lieu, nam_phien_ban, mo_ta, trang_thai, is_deleted)
VALUES
(N'Nike Dri-FIT Legend 2026', 'NIKE-LEGEND-2026', N'Nike', N'2026', 'AO', 'NAM', N'Dri-FIT', 2026, N'Áo thể thao Nike chính hãng', 'HIENTHI', 0),
(N'Adidas Predator Pro 2025', 'ADIDAS-PREDATOR-2025', N'Adidas', N'2025', 'AO', 'NAM', N'Polyester', 2025, N'Áo thể thao Adidas Predator', 'HIENTHI', 0),
(N'Puma Ultra Match Shorts', 'PUMA-SHORT-2025', N'Puma', N'2025', 'QUAN', 'NAM', N'Polyester', 2025, N'Quần thể thao Puma', 'HIENTHI', 0),
(N'Nike Match Socks', 'NIKE-SOCK-2026', N'Nike', N'2026', 'VO', 'UNISEX', N'Cotton Blend', 2026, N'Vớ thể thao thấm hút tốt', 'HIENTHI', 0),
(N'Adidas Predator Gloves', 'ADIDAS-GLOVE-2025', N'Adidas', N'2025', 'GANGTAY', 'UNISEX', N'Latex', 2025, N'Găng tay thủ môn Adidas', 'HIENTHI', 0);
GO

/* ================================
   SEED PRODUCT - CATEGORY
================================ */
INSERT INTO Product_Categories (product_id, category_id)
VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 3),
(5, 4);
GO

/* ================================
   SEED PRODUCT - TAG
================================ */
INSERT INTO Product_Tags (product_id, tag_id)
VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 5),
(3, 5),
(4, 3),
(5, 1);
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
(2, 'ADIDAS-PREDATOR-2025-L-BLACK', 'L', N'Đen', 900000, 850000, 4, 'CON_HANG', 0),
(3, 'PUMA-SHORT-2025-M-WHITE', 'M', N'Trắng', 350000, 320000, 20, 'CON_HANG', 0),
(3, 'PUMA-SHORT-2025-L-WHITE', 'L', N'Trắng', 350000, NULL, 15, 'CON_HANG', 0),
(4, 'NIKE-SOCK-2026-FREE-WHITE', 'FREE', N'Trắng', 120000, 100000, 30, 'CON_HANG', 0),
(5, 'ADIDAS-GLOVE-2025-8-BLUE', '8', N'Xanh', 650000, 600000, 7, 'CON_HANG', 0),
(5, 'ADIDAS-GLOVE-2025-9-BLUE', '9', N'Xanh', 650000, NULL, 3, 'CON_HANG', 0);
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
(2, '/images/products/adidas2.jpg', 0, 1, 0),
(3, '/images/products/puma1.jpg',   1, 0, 0),
(4, '/images/products/sock1.jpg',   1, 0, 0),
(5, '/images/products/glove1.jpg',  1, 0, 0);
GO

/* ================================
   SEED PRODUCT HISTORY
================================ */
INSERT INTO Product_History(product_id, action, changed_by, note)
VALUES
(1, 'CREATE', 1, N'Tạo sản phẩm Nike Dri-FIT Legend 2026'),
(2, 'CREATE', 1, N'Tạo sản phẩm Adidas Predator Pro 2025'),
(3, 'CREATE', 1, N'Tạo sản phẩm Puma Ultra Match Shorts'),
(4, 'CREATE', 1, N'Tạo sản phẩm Nike Match Socks'),
(5, 'CREATE', 1, N'Tạo sản phẩm Adidas Predator Gloves');
GO

/* ================================
   SEED CUSTOMERS
================================ */
INSERT INTO Customers (ten, email, sdt, ngay_sinh, loai_khach, diem_tich_luy, ghi_chu)
VALUES
(N'Nguyễn Văn An', 'an.nguyen@gmail.com', '0900000001', '1999-05-12', 'VIP',    1250, N'Khách thân thiết mua thường xuyên'),
(N'Trần Thị Bình', 'binh.tran@gmail.com', '0900000002', '2000-08-20', 'THUONG', 180,  N'Khách mới'),
(N'Lê Minh Cường', 'cuong.le@gmail.com', '0900000003', '1997-12-05', 'THUONG', 420,  N'Hay mua online'),
(N'Phạm Thu Dung', 'dung.pham@gmail.com', '0900000004', '1995-03-18', 'VIP',    980,  N'Khách sinh nhật hôm nay để test birthday'),
(N'Hoàng Gia Huy', 'huy.hoang@gmail.com', '0900000005', '2001-11-11', 'THUONG', 60,   N'Ít mua hàng');
GO

/* ================================
   SEED VIP PROGRAMS
================================ */
INSERT INTO Vip_Programs(level_name, min_points, min_spending, benefit, is_active)
VALUES
('THUONG', 0, 0, N'Khách hàng thường', 1),
('SILVER', 200, 2000000, N'Giảm 3%', 1),
('GOLD', 500, 5000000, N'Giảm 5%', 1),
('VIP', 1000, 10000000, N'Giảm 10%', 1);
GO

/* ================================
   SEED REWARD RULES
================================ */
INSERT INTO Reward_Rules(reward_name, required_points, discount_value, description, is_active)
VALUES
(N'Voucher 20K', 100, 20000, N'Đổi 100 điểm lấy voucher 20K', 1),
(N'Voucher 50K', 250, 50000, N'Đổi 250 điểm lấy voucher 50K', 1),
(N'Voucher 100K', 500, 100000, N'Đổi 500 điểm lấy voucher 100K', 1),
(N'Voucher 200K', 1000, 200000, N'Đổi 1000 điểm lấy voucher 200K', 1);
GO

/* ================================
   SEED ORDERS
================================ */
INSERT INTO Orders (customer_id, staff_id, order_date, status, payment_method, channel, total)
VALUES
(1, 2, '2026-03-01T10:15:00', 'HOAN_TAT',   'CASH',     'SHOP',   1170000),
(1, 2, '2026-03-05T14:20:00', 'HOAN_TAT',   'BANK',     'ONLINE', 850000),
(2, 2, '2026-03-08T09:00:00', 'MOI',        'COD',      'ONLINE', 100000),
(3, 3, '2026-03-10T16:45:00', 'DANG_XU_LY', 'BANK',     'SHOP',   1550000),
(4, 2, '2026-03-12T11:30:00', 'DANG_GIAO',  'CASH',     'ONLINE', 600000),
(5, 3, '2026-03-14T19:10:00', 'HUY',        'COD',      'SHOP',   320000),
(3, 2, '2026-02-20T08:30:00', 'TRA_HANG',   'BANK',     'ONLINE', 750000);
GO

/* ================================
   SEED ORDER ITEMS
================================ */
INSERT INTO Order_Items (order_id, variant_id, quantity, unit_price)
VALUES
(1, 1, 1, 750000),
(1, 8, 2, 210000),
(2, 4, 1, 850000),
(3, 8, 1, 100000),
(4, 5, 1, 850000),
(4, 9, 1, 600000),
(4, 8, 1, 100000),
(5, 9, 1, 600000),
(6, 6, 1, 320000),
(7, 2, 1, 750000);
GO

/* ================================
   SEED RETURNS
================================ */
INSERT INTO Returns (order_id, return_date, reason, refund_total, note)
VALUES
(7, '2026-02-23T10:00:00', N'Sản phẩm không vừa size', 750000, N'Hoàn tiền qua chuyển khoản');
GO

/* ================================
   SEED LOYALTY POINT HISTORY
================================ */
INSERT INTO Loyalty_Point_History (customer_id, points, type, note, created_at)
VALUES
(1, 500, 'ADD',      N'Cộng điểm từ đơn hàng #1', '2026-03-01T10:30:00'),
(1, 250, 'ADD',      N'Cộng điểm từ đơn hàng #2', '2026-03-05T14:40:00'),
(1, 500, 'BONUS',    N'Thưởng khách VIP',         '2026-03-06T09:00:00'),
(2, 100, 'ADD',      N'Cộng điểm đơn hàng chờ xử lý', '2026-03-08T09:10:00'),
(2, 80,  'BONUS',    N'Tặng điểm khách mới',      '2026-03-08T09:15:00'),
(3, 300, 'ADD',      N'Cộng điểm từ đơn hàng #4', '2026-03-10T17:00:00'),
(3, 120, 'ADD',      N'Cộng thêm theo chương trình', '2026-03-10T17:05:00'),
(4, 600, 'ADD',      N'Cộng điểm từ đơn hàng #5', '2026-03-12T11:45:00'),
(4, 380, 'BONUS',    N'Thưởng sinh nhật',         '2026-03-18T08:00:00'),
(5, 60,  'ADD',      N'Cộng điểm tích lũy cũ',    '2026-03-01T08:00:00'),
(3, 750, 'REFUND',   N'Hoàn điểm do trả hàng #7', '2026-02-23T10:10:00'),
(3, 750, 'SUBTRACT', N'Trừ điểm vì hoàn tiền đơn #7', '2026-02-23T10:15:00');
GO

/* ================================
   SEED VIP HISTORY
================================ */
INSERT INTO Vip_History (customer_id, old_level, new_level, reason, changed_at)
VALUES
(1, 'GOLD',   'VIP',    N'Đạt trên 1000 điểm',      '2026-03-06T09:10:00'),
(3, 'SILVER', 'THUONG', N'Hoàn trả đơn hàng lớn',   '2026-02-23T10:20:00'),
(4, 'GOLD',   'VIP',    N'Thưởng sinh nhật và doanh số', '2026-03-18T08:10:00');
GO

/* ================================
   SEED BIRTHDAY NOTIFICATION LOG
================================ */
INSERT INTO Birthday_Notification_Log (customer_id, sent_date, channel, note)
VALUES
(4, '2026-03-18T08:05:00', 'EMAIL', N'Gửi email chúc mừng sinh nhật'),
(4, '2026-03-18T08:06:00', 'SMS',   N'Gửi SMS voucher sinh nhật');
GO

/* ================================
   SEED AUDIT LOG
================================ */
INSERT INTO Audit_Log (user_id, action, entity, entity_id, detail)
VALUES
(1, N'CREATE', N'PRODUCT', 1, N'Tạo sản phẩm Nike Dri-FIT Legend 2026'),
(1, N'CREATE', N'PRODUCT', 2, N'Tạo sản phẩm Adidas Predator Pro 2025'),
(2, N'CREATE', N'ORDER',   1, N'Tạo đơn hàng cho khách Nguyễn Văn An'),
(2, N'UPDATE', N'CUSTOMER', 1, N'Cập nhật khách lên VIP'),
(3, N'CREATE', N'ORDER',   4, N'Tạo đơn hàng cho khách Lê Minh Cường');
GO

PRINT N'✅ DATABASE DATN đã tạo xong đầy đủ schema + dữ liệu test API.';
GO

/* ================================
   QUICK CHECK DATA
================================ */
SELECT * FROM Users;
SELECT * FROM Categories;
SELECT * FROM Tags;
SELECT * FROM Products;
SELECT * FROM Product_Variants;
SELECT * FROM Product_Images;
SELECT * FROM Product_History;
SELECT * FROM Customers;
SELECT * FROM Orders;
SELECT * FROM Order_Items;
SELECT * FROM Returns;
SELECT * FROM Loyalty_Point_History;
SELECT * FROM Vip_Programs;
SELECT * FROM Vip_History;
SELECT * FROM Reward_Rules;
SELECT * FROM Birthday_Notification_Log;
SELECT * FROM Audit_Log;
GO
