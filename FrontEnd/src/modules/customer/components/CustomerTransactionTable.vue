<template>
  <div class="overflow-hidden rounded-2xl border bg-white shadow-sm">
    <div class="border-b px-5 py-4">
      <h3 class="text-lg font-bold text-slate-800">Lịch sử giao dịch</h3>
      <p class="mt-1 text-sm text-slate-500">Danh sách đơn hàng của khách hàng này</p>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead class="bg-slate-50">
          <tr>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Mã đơn</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Ngày</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Trạng thái</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Thanh toán</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Kênh</th>
            <th class="px-4 py-3 text-left font-semibold text-slate-700">Điểm cộng</th>
            <th class="px-4 py-3 text-right font-semibold text-slate-700">Tổng tiền</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="transactions.length === 0">
            <td colspan="7" class="px-4 py-10 text-center text-slate-500">
              Chưa có giao dịch
            </td>
          </tr>

          <template v-for="order in transactions" :key="order.id">
            <tr class="border-t transition hover:bg-slate-50">
              <td class="px-4 py-3 font-semibold text-slate-800">#{{ order.id }}</td>
              <td class="px-4 py-3 text-slate-700">{{ formatDate(order.order_date || order.created_at) }}</td>
              <td class="px-4 py-3">
                <span
                  class="inline-flex rounded-full px-2.5 py-1 text-xs font-semibold"
                  :class="getStatusClass(order.status)"
                >
                  {{ formatStatus(order.status) }}
                </span>
              </td>
              <td class="px-4 py-3 text-slate-700">{{ order.payment_method || "-" }}</td>
              <td class="px-4 py-3 text-slate-700">{{ order.channel || "-" }}</td>
              <td class="px-4 py-3 font-semibold text-emerald-600">+{{ order.points_earned || 0 }}</td>
              <td class="px-4 py-3 text-right font-bold text-blue-600">
                {{ formatCurrency(order.total || 0) }}
              </td>
            </tr>

            <tr v-if="order.items?.length" class="bg-slate-50/50">
              <td colspan="7" class="px-4 pb-4 pt-0">
                <div class="mt-2 rounded-xl border bg-white p-3">
                  <p class="mb-2 text-sm font-semibold text-slate-700">Sản phẩm trong đơn</p>
                  <div class="space-y-2">
                    <div
                      v-for="item in order.items"
                      :key="item.id"
                      class="flex flex-wrap items-center justify-between gap-2 rounded-lg bg-slate-50 px-3 py-2 text-sm"
                    >
                      <div class="text-slate-700">
                        Variant ID: <span class="font-semibold">{{ item.variant_id || 0 }}</span>
                      </div>
                      <div class="text-slate-700">SL: {{ item.quantity }}</div>
                      <div class="font-semibold text-blue-600">
                        {{ formatCurrency(item.unit_price) }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CustomerTransaction } from "../types/customerTransaction";

defineProps<{
  transactions: CustomerTransaction[];
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

function formatStatus(status?: string | null) {
  switch (status) {
    case "MOI":
      return "Mới";
    case "DANG_XU_LY":
      return "Đang xử lý";
    case "DANG_GIAO":
      return "Đang giao";
    case "HOAN_TAT":
      return "Hoàn tất";
    case "HUY":
      return "Hủy";
    case "TRA_HANG":
      return "Trả hàng";
    default:
      return status || "-";
  }
}

function getStatusClass(status?: string | null) {
  switch (status) {
    case "MOI":
      return "bg-slate-100 text-slate-700";
    case "DANG_XU_LY":
      return "bg-blue-100 text-blue-700";
    case "DANG_GIAO":
      return "bg-amber-100 text-amber-700";
    case "HOAN_TAT":
      return "bg-emerald-100 text-emerald-700";
    case "HUY":
      return "bg-rose-100 text-rose-700";
    case "TRA_HANG":
      return "bg-violet-100 text-violet-700";
    default:
      return "bg-slate-100 text-slate-700";
  }
}
</script>