<template>
  <div class="rounded-2xl border bg-white p-5 shadow-sm">
    <div class="mb-4 flex items-start justify-between gap-4">
      <div>
        <h3 class="text-xl font-bold text-slate-800">{{ customer.ten || "Khách hàng" }}</h3>
        <p class="mt-1 text-sm text-slate-500">
          Mã KH: #{{ customer.id || "-" }} · {{ customer.loai_khach }}
        </p>
      </div>

      <span
        class="inline-flex rounded-full px-3 py-1 text-xs font-semibold"
        :class="
          customer.loai_khach === 'VIP'
            ? 'bg-amber-100 text-amber-700'
            : 'bg-slate-100 text-slate-700'
        "
      >
        {{ customer.loai_khach }}
      </span>
    </div>

    <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-sm text-slate-500">Email</p>
        <p class="mt-1 break-all font-semibold text-slate-800">{{ customer.email || "-" }}</p>
      </div>

      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-sm text-slate-500">Số điện thoại</p>
        <p class="mt-1 font-semibold text-slate-800">{{ customer.sdt || "-" }}</p>
      </div>

      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-sm text-slate-500">Ngày sinh</p>
        <p class="mt-1 font-semibold text-slate-800">{{ formatDate(customer.ngay_sinh) }}</p>
      </div>

      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-sm text-slate-500">Ngày tạo</p>
        <p class="mt-1 font-semibold text-slate-800">{{ formatDate(customer.created_at) }}</p>
      </div>

      <div class="rounded-xl bg-emerald-50 p-4">
        <p class="text-sm text-emerald-700">Điểm loyalty</p>
        <p class="mt-1 text-xl font-bold text-emerald-700">{{ formatNumber(customer.diem_tich_luy) }}</p>
      </div>

      <div class="rounded-xl bg-blue-50 p-4">
        <p class="text-sm text-blue-700">Tổng chi tiêu</p>
        <p class="mt-1 text-xl font-bold text-blue-700">{{ formatCurrency(customer.tong_chi_tieu || 0) }}</p>
      </div>

      <div class="rounded-xl bg-violet-50 p-4">
        <p class="text-sm text-violet-700">Tổng đơn hàng</p>
        <p class="mt-1 text-xl font-bold text-violet-700">{{ customer.tong_don_hang || 0 }}</p>
      </div>

      <div class="rounded-xl bg-amber-50 p-4">
        <p class="text-sm text-amber-700">Lần mua cuối</p>
        <p class="mt-1 text-xl font-bold text-amber-700">{{ formatDate(customer.lan_mua_cuoi) }}</p>
      </div>
    </div>

    <div class="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
      <div class="rounded-xl bg-rose-50 p-4">
        <p class="text-sm text-rose-700">Số ngày không mua</p>
        <p class="mt-1 text-xl font-bold text-rose-700">{{ customer.so_ngay_khong_mua ?? 0 }} ngày</p>
      </div>

      <div class="rounded-xl bg-slate-50 p-4">
        <p class="mb-2 text-sm font-medium text-slate-700">Ghi chú</p>
        <p class="text-sm leading-6 text-slate-600">{{ customer.ghi_chu || "Không có ghi chú" }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Customer } from "../types/customer";

defineProps<{
  customer: Customer;
}>();

function formatNumber(value?: number | null) {
  return new Intl.NumberFormat("vi-VN").format(Number(value || 0));
}

function formatCurrency(value?: number | null) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));
}

function formatDate(value?: string | null) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("vi-VN");
}
</script>