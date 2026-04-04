USE DATN;
GO

/* ================================
   LOYALTY POINT HISTORY
================================ */
IF OBJECT_ID('Loyalty_Point_History', 'U') IS NULL
BEGIN
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
END
GO

CREATE INDEX IX_LPH_Customer ON Loyalty_Point_History(customer_id);
GO

/* ================================
   VIP PROGRAMS
================================ */
IF OBJECT_ID('Vip_Programs', 'U') IS NULL
BEGIN
    CREATE TABLE Vip_Programs (
        id INT IDENTITY(1,1) PRIMARY KEY,
        level_name VARCHAR(30) NOT NULL UNIQUE,
        min_points INT NOT NULL,
        min_spending DECIMAL(14,2) NOT NULL DEFAULT 0,
        benefit NVARCHAR(500) NULL,
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

/* ================================
   VIP HISTORY
================================ */
IF OBJECT_ID('Vip_History', 'U') IS NULL
BEGIN
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
END
GO

CREATE INDEX IX_VH_Customer ON Vip_History(customer_id);
GO

/* ================================
   REWARD RULES
================================ */
IF OBJECT_ID('Reward_Rules', 'U') IS NULL
BEGIN
    CREATE TABLE Reward_Rules (
        id INT IDENTITY(1,1) PRIMARY KEY,
        reward_name NVARCHAR(150) NOT NULL,
        required_points INT NOT NULL,
        discount_value DECIMAL(12,2) NOT NULL DEFAULT 0,
        description NVARCHAR(500) NULL,
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

/* ================================
   BIRTHDAY NOTIFICATION LOG
================================ */
IF OBJECT_ID('Birthday_Notification_Log', 'U') IS NULL
BEGIN
    CREATE TABLE Birthday_Notification_Log (
        id INT IDENTITY(1,1) PRIMARY KEY,
        customer_id INT NOT NULL,
        sent_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
        note NVARCHAR(255) NULL,
        CONSTRAINT FK_BNL_Customer
            FOREIGN KEY (customer_id) REFERENCES Customers(id)
    );
END
GO

/* ================================
   SEED VIP PROGRAMS
================================ */
IF NOT EXISTS (SELECT 1 FROM Vip_Programs)
BEGIN
    INSERT INTO Vip_Programs(level_name, min_points, min_spending, benefit, is_active)
    VALUES
    ('THUONG', 0, 0, N'Khách hàng thường', 1),
    ('SILVER', 200, 2000000, N'Giảm 3%', 1),
    ('GOLD', 500, 5000000, N'Giảm 5%', 1),
    ('VIP', 1000, 10000000, N'Giảm 10%', 1);
END
GO

/* ================================
   SEED REWARD RULES
================================ */
IF NOT EXISTS (SELECT 1 FROM Reward_Rules)
BEGIN
    INSERT INTO Reward_Rules(reward_name, required_points, discount_value, description, is_active)
    VALUES
    (N'Voucher 20K', 100, 20000, N'Đổi 100 điểm lấy voucher 20K', 1),
    (N'Voucher 50K', 250, 50000, N'Đổi 250 điểm lấy voucher 50K', 1),
    (N'Voucher 100K', 500, 100000, N'Đổi 500 điểm lấy voucher 100K', 1);
END
GO

/* =========================================================
   GOALSTORE - VOUCHER SETUP
   ========================================================= */

IF OBJECT_ID('dbo.Vouchers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Vouchers (
        id INT IDENTITY(1,1) PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(1000) NULL,
        discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
        min_order_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
        is_active BIT NOT NULL DEFAULT 1,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NULL
    );
END
GO

IF OBJECT_ID('dbo.Customer_Vouchers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Customer_Vouchers (
        id INT IDENTITY(1,1) PRIMARY KEY,
        customer_id INT NOT NULL,
        voucher_id INT NOT NULL,
        claimed_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        used_at DATETIME2 NULL,
        used_order_id INT NULL,
        used_order_code VARCHAR(50) NULL,
        CONSTRAINT FK_customer_vouchers_customer FOREIGN KEY (customer_id) REFERENCES dbo.Customers(id),
        CONSTRAINT FK_customer_vouchers_voucher FOREIGN KEY (voucher_id) REFERENCES dbo.Vouchers(id),
        CONSTRAINT UK_customer_voucher UNIQUE (customer_id, voucher_id)
    );
END
GO

IF COL_LENGTH('dbo.Orders', 'voucher_id') IS NULL
BEGIN
    ALTER TABLE dbo.Orders ADD voucher_id INT NULL;
END
GO

IF COL_LENGTH('dbo.Orders', 'voucher_code') IS NULL
BEGIN
    ALTER TABLE dbo.Orders ADD voucher_code VARCHAR(50) NULL;
END
GO

IF COL_LENGTH('dbo.Orders', 'voucher_name') IS NULL
BEGIN
    ALTER TABLE dbo.Orders ADD voucher_name NVARCHAR(255) NULL;
END
GO

IF COL_LENGTH('dbo.Orders', 'voucher_percent') IS NULL
BEGIN
    ALTER TABLE dbo.Orders ADD voucher_percent DECIMAL(5,2) NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Vouchers WHERE code = 'GOAL10')
BEGIN
    INSERT INTO dbo.Vouchers (code, name, description, discount_percent, min_order_amount, is_active, is_deleted, created_at, updated_at)
    VALUES ('GOAL10', N'Voucher giảm 10%', N'Giảm 10% giá trị sản phẩm trong đơn hàng.', 10, 0, 1, 0, SYSDATETIME(), SYSDATETIME());
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Vouchers WHERE code = 'GOAL20')
BEGIN
    INSERT INTO dbo.Vouchers (code, name, description, discount_percent, min_order_amount, is_active, is_deleted, created_at, updated_at)
    VALUES ('GOAL20', N'Voucher giảm 20%', N'Giảm 20% giá trị sản phẩm trong đơn hàng.', 20, 0, 1, 0, SYSDATETIME(), SYSDATETIME());
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Vouchers WHERE code = 'GOAL30')
BEGIN
    INSERT INTO dbo.Vouchers (code, name, description, discount_percent, min_order_amount, is_active, is_deleted, created_at, updated_at)
    VALUES ('GOAL30', N'Voucher giảm 30%', N'Giảm 30% giá trị sản phẩm trong đơn hàng.', 30, 0, 1, 0, SYSDATETIME(), SYSDATETIME());
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Vouchers WHERE code = 'GOAL40')
BEGIN
    INSERT INTO dbo.Vouchers (code, name, description, discount_percent, min_order_amount, is_active, is_deleted, created_at, updated_at)
    VALUES ('GOAL40', N'Voucher giảm 40%', N'Giảm 40% giá trị sản phẩm trong đơn hàng.', 40, 0, 1, 0, SYSDATETIME(), SYSDATETIME());
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Vouchers WHERE code = 'GOAL50')
BEGIN
    INSERT INTO dbo.Vouchers (code, name, description, discount_percent, min_order_amount, is_active, is_deleted, created_at, updated_at)
    VALUES ('GOAL50', N'Voucher giảm 50%', N'Giảm 50% giá trị sản phẩm trong đơn hàng.', 50, 0, 1, 0, SYSDATETIME(), SYSDATETIME());
END
GO

SELECT * FROM Vouchers;
SELECT * FROM Customer_Vouchers
SELECT * FROM Customers;