# Quy ước chia module frontend

## Code chung - hạn chế sửa nhiều người cùng lúc
- src/router/index.ts
- src/shared/layouts/MainLayout.vue
- src/shared/components/*
- src/shared/lib/api.ts
- src/App.vue
- src/main.ts

## Người 1 - Product
- src/modules/product/*

## Người 2 - Order + Sales
- src/modules/order/*
- src/modules/sales/*

## Người 3 - Customer
- src/modules/customer/*

## Người 4 - User + Audit + merge chung
- src/modules/user/*
- src/modules/audit/*
- phần code chung ở trên

## Ghi chú
- Không tạo lại Footer/Navbar/MainLayout ở ngoài `src/shared`.
- Không tạo lại view cũ trong `src/views` nữa.
- Mỗi module chỉ sửa trong thư mục module của mình.
