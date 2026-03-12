// customer.ts
export interface Customer {
  id?: number
  ten: string
  email: string
  sdt: string
  ngaySinh: string | null
  loaiKhach: string
  diemTichLuy: number
  ghiChu: string
  createdAt?: string
}

export interface CustomerOrderHistory {
  orderId: number
  orderDate: string
  status: string
  paymentMethod: string | null
  channel: string | null
  total: number
}

export interface CustomerSpending {
  customerId: number
  ten: string
  email: string
  sdt: string
  loaiKhach: string
  diemTichLuy: number
  tongChiTieu: number
}

export interface InactiveCustomer {
  customerId: number
  ten: string
  email: string
  sdt: string
  lastOrderDate: string
  soNgayKhongMua: number
}