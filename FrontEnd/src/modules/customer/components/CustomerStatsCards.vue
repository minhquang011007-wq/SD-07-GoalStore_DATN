<template>
  <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-6">
    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Tổng khách hàng</p>
      <p class="mt-2 text-2xl font-bold text-slate-800">{{ stats.totalCustomers }}</p>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Khách VIP</p>
      <p class="mt-2 text-2xl font-bold text-amber-600">{{ stats.totalVipCustomers }}</p>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Khách thường</p>
      <p class="mt-2 text-2xl font-bold text-slate-700">{{ stats.totalRegularCustomers }}</p>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Tổng điểm</p>
      <p class="mt-2 text-2xl font-bold text-emerald-600">{{ formatNumber(stats.totalPoints) }}</p>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Tổng chi tiêu</p>
      <p class="mt-2 text-xl font-bold text-blue-600">{{ formatCurrency(stats.totalRevenueFromCustomers) }}</p>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <p class="text-sm text-slate-500">Active 30 ngày</p>
      <p class="mt-2 text-2xl font-bold text-violet-600">{{ stats.active30Days }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CustomerStats } from "../types/customerStats";

defineProps<{
  stats: CustomerStats;
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
</script>