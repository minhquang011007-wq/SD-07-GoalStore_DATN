<template>
  <div class="overflow-hidden rounded-2xl border bg-white shadow-sm">
    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead class="bg-slate-50">
          <tr>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Khách hàng</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Loại</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Điểm</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Tổng đơn</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Lần mua cuối</th>
            <th class="px-4 py-3 text-right font-semibold text-slate-700">Tổng chi tiêu</th>
            <th class="px-4 py-3 text-center font-semibold text-slate-700">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="customers.length === 0">
            <td colspan="7" class="px-4 py-10 text-center text-slate-500">
              Không có khách hàng
            </td>
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

            <td class="px-4 py-3 font-semibold text-emerald-600">
              {{ formatNumber(customer.diem_tich_luy || 0) }}
            </td>

            <td class="px-4 py-3">{{ customer.tong_don_hang || 0 }}</td>

            <td class="px-4 py-3 text-slate-700">{{ formatDate(customer.lan_mua_cuoi) }}</td>

            <td class="px-4 py-3 text-right font-bold text-blue-600">
              {{ formatCurrency(customer.tong_chi_tieu || 0) }}
            </td>

            <td class="px-4 py-3">
              <div class="flex flex-wrap justify-center gap-2">
                <button
                  type="button"
                  class="rounded-lg bg-slate-800 px-3 py-1.5 text-xs font-semibold text-white hover:bg-slate-900"
                  @click="$emit('view', customer)"
                >
                  Xem
                </button>

                <button
                  type="button"
                  class="rounded-lg bg-blue-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-blue-700"
                  @click="$emit('edit', customer)"
                >
                  Sửa
                </button>

                <button
                  v-if="customer.loai_khach !== 'VIP'"
                  type="button"
                  class="rounded-lg bg-amber-500 px-3 py-1.5 text-xs font-semibold text-white hover:bg-amber-600"
                  @click="$emit('assign-vip', customer)"
                >
                  Gán VIP
                </button>

                <button
                  type="button"
                  class="rounded-lg bg-rose-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-rose-700"
                  @click="$emit('delete', customer.id)"
                >
                  Xóa
                </button>
              </div>
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

defineEmits<{
  (e: "view", customer: Customer): void;
  (e: "edit", customer: Customer): void;
  (e: "assign-vip", customer: Customer): void;
  (e: "delete", id?: number): void;
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