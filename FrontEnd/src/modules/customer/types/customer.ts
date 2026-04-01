export type CustomerType = "VIP" | "THUONG";

export interface Customer {
  id?: number;
  ten: string;
  email?: string;
  sdt?: string;
  ngay_sinh?: string | null;
  loai_khach: CustomerType;
  diem_tich_luy: number;
  ghi_chu?: string;
  created_at?: string | null;

  tong_chi_tieu?: number;
  tong_don_hang?: number;
  lan_mua_cuoi?: string | null;
  so_ngay_khong_mua?: number;
}

export const defaultCustomer: Customer = {
  ten: "",
  email: "",
  sdt: "",
  ngay_sinh: "",
  loai_khach: "THUONG",
  diem_tich_luy: 0,
  ghi_chu: "",
  tong_chi_tieu: 0,
  tong_don_hang: 0,
  lan_mua_cuoi: null,
  so_ngay_khong_mua: 0,
};