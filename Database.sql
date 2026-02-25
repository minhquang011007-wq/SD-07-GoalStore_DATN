/* ================================
   RESET DB
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
   USERS (1 user = 1 role)
================================ */
CREATE TABLE Users (
    id INT IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- hash
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','SALES','INVENTORY')),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (trang_thai IN ('ACTIVE','INACTIVE','LOCKED')),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ================================
   AUDIT LOG (đơn giản)
================================ */
CREATE TABLE Audit_Log (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id INT NULL,
    action NVARCHAR(100) NOT NULL,      -- VD: CREATE_ORDER, UPDATE_PRODUCT
    entity NVARCHAR(50) NOT NULL,       -- VD: ORDER, PRODUCT, CUSTOMER
    entity_id INT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    detail NVARCHAR(500) NULL,
    CONSTRAINT FK_AuditLog_User FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO
CREATE INDEX IX_AuditLog_Time ON Audit_Log(created_at DESC);
GO

/* ================================
   CATEGORIES
================================ */
CREATE TABLE Categories (
    id INT IDENTITY PRIMARY KEY,
    ten_danh_muc NVARCHAR(150) NOT NULL UNIQUE,
    mo_ta NVARCHAR(500) NULL,
    hinh_anh NVARCHAR(255) NULL -- avatar category
);
GO

/* ================================
   PRODUCTS
================================ */
CREATE TABLE Products (
    id INT IDENTITY PRIMARY KEY,
    ten_san_pham NVARCHAR(200) NOT NULL,
    thuong_hieu NVARCHAR(100) NULL,
    chat_lieu NVARCHAR(100) NULL,
    mo_ta NVARCHAR(MAX) NULL,
    nam_phien_ban INT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'HIENTHI'
        CHECK (trang_thai IN ('HIENTHI','AN','NGUNG_BAN')),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO
CREATE INDEX IX_Products_Ten ON Products(ten_san_pham);
GO

/* sản phẩm - danh mục (many-to-many) */
CREATE TABLE Product_Categories (
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT PK_ProductCategories PRIMARY KEY (product_id, category_id),
    CONSTRAINT FK_PC_Product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT FK_PC_Category FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);
GO
CREATE INDEX IX_PC_Category ON Product_Categories(category_id);
GO

/* ảnh sản phẩm */
CREATE TABLE Product_Images (
    id INT IDENTITY PRIMARY KEY,
    product_id INT NOT NULL,
    url NVARCHAR(255) NOT NULL,
    is_avatar BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_PI_Product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO
CREATE INDEX IX_PI_Product ON Product_Images(product_id);
GO

/* variant (sku + size + tồn kho + giá) */
CREATE TABLE Product_Variants (
    id INT IDENTITY PRIMARY KEY,
    product_id INT NOT NULL,
    sku VARCHAR(80) NOT NULL UNIQUE,
    size VARCHAR(10) NOT NULL,
    gia_ban DECIMAL(12,2) NOT NULL CHECK (gia_ban >= 0),
    gia_khuyen_mai DECIMAL(12,2) NULL CHECK (gia_khuyen_mai IS NULL OR gia_khuyen_mai >= 0),
    ton_kho INT NOT NULL DEFAULT 0 CHECK (ton_kho >= 0),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (trang_thai IN ('ACTIVE','INACTIVE')),
    CONSTRAINT FK_PV_Product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO
CREATE INDEX IX_PV_Product ON Product_Variants(product_id);
GO

/* ================================
   CUSTOMERS (loyalty đơn giản: lưu trực tiếp điểm)
================================ */
CREATE TABLE Customers (
    id INT IDENTITY PRIMARY KEY,
    ten NVARCHAR(150) NOT NULL,
    email VARCHAR(100) NULL,
    sdt VARCHAR(20) NULL,
    ngay_sinh DATE NULL,
    loai_khach VARCHAR(10) NOT NULL DEFAULT 'THUONG'
        CHECK (loai_khach IN ('VIP','THUONG')),
    diem_tich_luy INT NOT NULL DEFAULT 0 CHECK (diem_tich_luy >= 0),
    ghi_chu NVARCHAR(500) NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO
CREATE INDEX IX_Customers_Type ON Customers(loai_khach);
CREATE INDEX IX_Customers_Points ON Customers(diem_tich_luy DESC);
GO

/* ================================
   ORDERS
================================ */
CREATE TABLE Orders (
    id INT IDENTITY PRIMARY KEY,
    customer_id INT NOT NULL,
    staff_id INT NULL, -- user xử lý đơn
    order_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    status VARCHAR(20) NOT NULL DEFAULT 'MOI'
        CHECK (status IN ('MOI','DANG_XU_LY','DANG_GIAO','HOAN_TAT','HUY','TRA_HANG')),
    payment_method VARCHAR(20) NULL
        CHECK (payment_method IS NULL OR payment_method IN ('COD','BANKING','MOMO','VNPAY')),
    channel VARCHAR(10) NOT NULL DEFAULT 'ONLINE'
        CHECK (channel IN ('ONLINE','OFFLINE')),
    total DECIMAL(14,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    CONSTRAINT FK_Orders_Customer FOREIGN KEY (customer_id) REFERENCES Customers(id),
    CONSTRAINT FK_Orders_Staff FOREIGN KEY (staff_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO
CREATE INDEX IX_Orders_Status ON Orders(status);
CREATE INDEX IX_Orders_Date ON Orders(order_date DESC);
GO

CREATE TABLE Order_Items (
    id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price >= 0), -- snapshot
    CONSTRAINT FK_OI_Order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_OI_Variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id)
);
GO
CREATE INDEX IX_OI_Order ON Order_Items(order_id);
GO

/* ================================
   RETURNS (đơn giản: trả theo đơn)
================================ */
CREATE TABLE Returns (
    id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    return_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    reason NVARCHAR(500) NULL,
    refund_total DECIMAL(14,2) NOT NULL DEFAULT 0 CHECK (refund_total >= 0),
    note NVARCHAR(500) NULL,
    CONSTRAINT FK_Returns_Order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE
);
GO

/* ================================
   SEED ADMIN
================================ */
INSERT INTO Users(username, email, [password], role)
VALUES ('admin','admin@gmail.com','123456','ADMIN');
GO

PRINT N'✅ DATN simplified database created successfully.';
GO

Select * from Users;
