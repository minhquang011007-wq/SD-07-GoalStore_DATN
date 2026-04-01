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