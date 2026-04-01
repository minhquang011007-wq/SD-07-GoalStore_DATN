<template>
  <div class="overflow-hidden rounded-2xl border bg-white shadow-sm">
    <div class="border-b px-5 py-4">
      <h3 class="text-lg font-bold text-slate-800">Khách hàng lâu chưa mua</h3>
      <p class="mt-1 text-sm text-slate-500">Danh sách khách chưa phát sinh mua hàng trong thời gian dài</p>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead class="bg-slate-50">
          <tr>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Khách hàng</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Loại</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Lần mua cuối</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Không mua</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Tổng đơn</th>
            <th class="px-4 py-3 text-right font-semibold text-slate-700">Tổng chi tiêu</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="customers.length === 0">
            <td colspan="6" class="px-4 py-10 text-center text-slate-500">Không có dữ liệu</td>
          </tr>

          <tr
            v-for="customer in customers"
            :key="customer.id"
            class="border-t transition hover:bg-slate-50"
          >
            <td class="px-4 py-3">
              <div class="font-semibold text-slate-800">{{ customer.ten }}</div>
              <div class="text-xs text-slate-500">{{ customer.email || customer.sdt || "-" }}</div>
            </td>
            <td class="px-4 py-3">
              <span
                class="inline-flex rounded-full px-2.5 py-1 text-xs font-semibold"
                :class="
                  customer.loai_khach === 'VIP'
                    ? 'bg-amber-100 text-amber-700'
                    : 'bg-slate-100 text-slate-700'
                "
              >
                {{ customer.loai_khach }}
              </span>
            </td>
            <td class="px-4 py-3 text-slate-700">{{ formatDate(customer.lan_mua_cuoi) }}</td>
            <td class="px-4 py-3 font-semibold text-rose-600">
              {{ customer.so_ngay_khong_mua || 0 }} ngày
            </td>
            <td class="px-4 py-3">{{ customer.tong_don_hang || 0 }}</td>
            <td class="px-4 py-3 text-right font-bold text-blue-600">
              {{ formatCurrency(customer.tong_chi_tieu || 0) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Customer } from "../types/customer";

defineProps<{
  customers: Customer[];
}>();

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