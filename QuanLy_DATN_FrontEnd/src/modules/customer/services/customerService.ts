import api from "@/shared/lib/api";
import type { Customer } from "../types/customer";
import type { CustomerFilter } from "../types/customerFilter";
import type { CustomerStats } from "../types/customerStats";

const API = "/api/customers";

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function normalizeCustomer(raw: any): Customer {
  return {
    id: toNumber(raw?.id ?? raw?.customerId, 0),
    ten: raw?.ten ?? "",
    email: raw?.email ?? "",
    sdt: raw?.sdt ?? "",
    ngay_sinh: raw?.ngaySinh ?? raw?.ngay_sinh ?? null,
    loai_khach: (raw?.loaiKhach ?? raw?.loai_khach) === "VIP" ? "VIP" : "THUONG",
    diem_tich_luy: toNumber(raw?.diemTichLuy ?? raw?.diem_tich_luy, 0),
    ghi_chu: raw?.ghiChu ?? raw?.ghi_chu ?? "",
    created_at: raw?.createdAt ?? raw?.created_at ?? null,
    tong_chi_tieu: toNumber(raw?.tongChiTieu ?? raw?.tong_chi_tieu ?? raw?.totalSpending, 0),
    tong_don_hang: toNumber(raw?.tongDonHang ?? raw?.tong_don_hang ?? raw?.totalOrders, 0),
    lan_mua_cuoi: raw?.lanMuaCuoi ?? raw?.lan_mua_cuoi ?? raw?.lastOrderDate ?? null,
    so_ngay_khong_mua: toNumber(raw?.soNgayKhongMua ?? raw?.so_ngay_khong_mua, 0),
  };
}

function normalizeStats(raw: any): CustomerStats {
  return {
    totalCustomers: toNumber(raw?.totalCustomers, 0),
    totalVipCustomers: toNumber(raw?.totalVipCustomers, 0),
    totalRegularCustomers: toNumber(raw?.totalRegularCustomers, 0),
    totalPoints: toNumber(raw?.totalPoints, 0),
    totalRevenueFromCustomers: toNumber(raw?.totalRevenueFromCustomers, 0),
    active30Days: toNumber(raw?.active30Days, 0),
  };
}

function toRequestBody(data: Customer) {
  return {
    ten: data.ten?.trim() ?? "",
    email: data.email?.trim() || null,
    sdt: data.sdt?.trim() || null,
    ngaySinh: data.ngay_sinh || null,
    loaiKhach: data.loai_khach,
    diemTichLuy: toNumber(data.diem_tich_luy, 0),
    ghiChu: data.ghi_chu?.trim() || null,
  };
}

function unwrapList(payload: any): any[] {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.data)) return payload.data;
  return [];
}

function unwrapObject(payload: any): any {
  return payload?.data ?? payload;
}

export async function getCustomers(): Promise<Customer[]> {
  const res = await api.get(API);
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function getCustomerById(id: number): Promise<Customer> {
  const res = await api.get(`${API}/${id}`);
  return normalizeCustomer(unwrapObject(res.data));
}

export async function getCustomerDetail(id: number): Promise<Customer> {
  const res = await api.get(`${API}/${id}/detail`);
  return normalizeCustomer(unwrapObject(res.data));
}

export async function createCustomer(data: Customer): Promise<Customer> {
  const res = await api.post(API, toRequestBody(data));
  return normalizeCustomer(unwrapObject(res.data));
}

export async function updateCustomer(id: number, data: Customer): Promise<Customer> {
  const res = await api.put(`${API}/${id}`, toRequestBody(data));
  return normalizeCustomer(unwrapObject(res.data));
}

export async function deleteCustomer(id: number) {
  return api.delete(`${API}/${id}`);
}

export async function assignVip(id: number): Promise<Customer> {
  const res = await api.put(`${API}/${id}/assign-vip`);
  return normalizeCustomer(unwrapObject(res.data));
}

export async function searchCustomersByName(name: string): Promise<Customer[]> {
  const res = await api.get(`${API}/search`, { params: { name } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function searchCustomersByEmail(email: string): Promise<Customer[]> {
  const res = await api.get(`${API}/email`, { params: { email } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function searchCustomersByPhone(sdt: string): Promise<Customer[]> {
  const res = await api.get(`${API}/phone`, { params: { sdt } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function searchCustomersAll(keyword: string): Promise<Customer[]> {
  const res = await api.get(`${API}/search-all`, { params: { keyword } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function getCustomersByLoaiKhach(loaiKhach: string): Promise<Customer[]> {
  const res = await api.get(`${API}/filter`, { params: { loaiKhach } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function getTopSpendingCustomers(): Promise<Customer[]> {
  const res = await api.get(`${API}/top-spending`);
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function getInactiveCustomers(days = 30): Promise<Customer[]> {
  const res = await api.get(`${API}/inactive`, { params: { days } });
  return unwrapList(res.data).map(normalizeCustomer);
}

export async function getCustomerStats(): Promise<CustomerStats> {
  const res = await api.get(`${API}/stats`);
  return normalizeStats(unwrapObject(res.data));
}

export async function advancedFilterCustomers(filter: CustomerFilter): Promise<Customer[]> {
  const res = await api.post(`${API}/advanced-filter`, {
    keyword: filter.keyword || "",
    loaiKhach: filter.loai_khach || null,
    inactiveDays: filter.inactiveDays || null,
    minPoints: filter.minPoints ?? null,
    maxPoints: filter.maxPoints ?? null,
    minSpending: filter.minSpending ?? null,
    maxSpending: filter.maxSpending ?? null,
    sortBy: filter.sortBy || "newest",
  });
  return unwrapList(res.data).map(normalizeCustomer);
}