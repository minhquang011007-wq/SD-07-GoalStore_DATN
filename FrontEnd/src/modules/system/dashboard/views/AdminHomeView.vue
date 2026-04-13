<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  ArcElement,
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  Title,
  Tooltip,
} from 'chart.js'
import { Bar, Doughnut, Line } from 'vue-chartjs'
import { getAdminDashboard } from '../services/dashboard.api'
import type { AdminDashboardResponse } from '../types/dashboard'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
)

const currentYear = new Date().getFullYear()
const selectedYear = ref(currentYear)
const loading = ref(false)
const error = ref('')
const dashboard = ref<AdminDashboardResponse | null>(null)

const years = computed(() => {
  const result: number[] = []
  for (let year = currentYear; year >= currentYear - 4; year -= 1) {
    result.push(year)
  }
  return result
})

function formatCurrency(value: number) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(value || 0)
}

function formatNumber(value: number) {
  return new Intl.NumberFormat('vi-VN').format(value || 0)
}

async function loadDashboard() {
  loading.value = true
  error.value = ''

  try {
    dashboard.value = await getAdminDashboard(selectedYear.value)
  } catch (err: any) {
    console.error(err)
    error.value = err?.response?.data?.message || 'Không tải được dữ liệu thống kê'
  } finally {
    loading.value = false
  }
}

const revenueLineData = computed(() => ({
  labels: dashboard.value?.revenueByMonth?.map((item) => item.label) || [],
  datasets: [
    {
      label: 'Doanh thu',
      data: dashboard.value?.revenueByMonth?.map((item) => item.value) || [],
      borderColor: '#2563eb',
      backgroundColor: 'rgba(37, 99, 235, 0.15)',
      tension: 0.35,
      fill: true,
    },
  ],
}))

const ordersBarData = computed(() => ({
  labels: dashboard.value?.ordersByMonth?.map((item) => item.label) || [],
  datasets: [
    {
      label: 'Số đơn hàng',
      data: dashboard.value?.ordersByMonth?.map((item) => item.value) || [],
      backgroundColor: '#10b981',
      borderRadius: 8,
    },
  ],
}))

const statusDoughnutData = computed(() => ({
  labels: dashboard.value?.orderStatusBreakdown?.map((item) => item.label) || [],
  datasets: [
    {
      data: dashboard.value?.orderStatusBreakdown?.map((item) => item.count) || [],
      backgroundColor: ['#2563eb', '#16a34a', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'],
      borderWidth: 1,
    },
  ],
}))

const lineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top' as const,
    },
  },
}

const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top' as const,
    },
  },
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom' as const,
    },
  },
}

onMounted(loadDashboard)
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm lg:flex-row lg:items-center lg:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-slate-800">Dashboard thống kê</h1>
        <p class="mt-1 text-sm text-slate-500">
          Theo dõi doanh thu, đơn hàng, khách hàng và voucher
        </p>
      </div>

      <div class="flex items-center gap-3">
        <select
          v-model="selectedYear"
          class="rounded-xl border border-slate-300 px-4 py-2 text-sm outline-none"
        >
          <option
            v-for="year in years"
            :key="year"
            :value="year"
          >
            {{ year }}
          </option>
        </select>

        <button
          class="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
          @click="loadDashboard"
        >
          Lọc
        </button>
      </div>
    </div>

    <div
      v-if="error"
      class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600"
    >
      {{ error }}
    </div>

    <div
      v-if="loading"
      class="rounded-2xl border border-slate-200 bg-white p-6 text-center text-slate-500 shadow-sm"
    >
      Đang tải dữ liệu thống kê...
    </div>

    <template v-else-if="dashboard">
      <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-sm text-slate-500">Tổng doanh thu</p>
          <h3 class="mt-2 text-2xl font-bold text-slate-800">
            {{ formatCurrency(dashboard.summary.totalRevenue) }}
          </h3>
          <p class="mt-2 text-xs text-slate-400">
            Giá trị trung bình / đơn: {{ formatCurrency(dashboard.summary.averageOrderValue) }}
          </p>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-sm text-slate-500">Tổng đơn hàng</p>
          <h3 class="mt-2 text-2xl font-bold text-slate-800">
            {{ formatNumber(dashboard.summary.totalOrders) }}
          </h3>
          <p class="mt-2 text-xs text-slate-400">
            Hoàn tất: {{ formatNumber(dashboard.summary.completedOrders) }}
          </p>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-sm text-slate-500">Khách hàng</p>
          <h3 class="mt-2 text-2xl font-bold text-slate-800">
            {{ formatNumber(dashboard.customers.totalCustomers) }}
          </h3>
          <p class="mt-2 text-xs text-slate-400">
            VIP: {{ formatNumber(dashboard.customers.vipCustomers) }}
          </p>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p class="text-sm text-slate-500">Voucher đã dùng</p>
          <h3 class="mt-2 text-2xl font-bold text-slate-800">
            {{ formatNumber(dashboard.vouchers.usedVouchers) }}
          </h3>
          <p class="mt-2 text-xs text-slate-400">
            Đang hoạt động: {{ formatNumber(dashboard.vouchers.activeVouchers) }}
          </p>
        </div>
      </div>

      <div class="grid gap-6 xl:grid-cols-[1.6fr_1fr]">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Doanh thu theo tháng</h2>
            <p class="text-sm text-slate-500">Biểu đồ xu hướng doanh thu trong năm</p>
          </div>
          <div class="h-[360px]">
            <Line
              :data="revenueLineData"
              :options="lineOptions"
            />
          </div>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Trạng thái đơn hàng</h2>
            <p class="text-sm text-slate-500">Cơ cấu đơn hàng theo trạng thái</p>
          </div>
          <div class="h-[360px]">
            <Doughnut
              :data="statusDoughnutData"
              :options="doughnutOptions"
            />
          </div>
        </div>
      </div>

      <div class="grid gap-6 xl:grid-cols-[1.2fr_1fr]">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Số đơn hàng theo tháng</h2>
            <p class="text-sm text-slate-500">Theo dõi lượng đơn tạo mới theo từng tháng</p>
          </div>
          <div class="h-[340px]">
            <Bar
              :data="ordersBarData"
              :options="barOptions"
            />
          </div>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Tổng quan khách hàng</h2>
            <p class="text-sm text-slate-500">Các chỉ số khách hàng quan trọng</p>
          </div>

          <div class="space-y-4">
            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Tổng khách hàng</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.customers.totalCustomers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Khách đang hoạt động</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.customers.activeCustomers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Khách VIP</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.customers.vipCustomers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Khách mới trong năm</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.customers.newCustomersThisYear) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="grid gap-6 xl:grid-cols-[1fr_1.1fr]">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Tổng quan voucher</h2>
            <p class="text-sm text-slate-500">Tình trạng sử dụng voucher trong hệ thống</p>
          </div>

          <div class="space-y-4">
            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Tổng voucher</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.vouchers.totalVouchers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Voucher đang hoạt động</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.vouchers.activeVouchers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Voucher đã nhận</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.vouchers.claimedVouchers) }}
              </span>
            </div>

            <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
              <span class="text-sm text-slate-600">Voucher đã sử dụng</span>
              <span class="font-semibold text-slate-800">
                {{ formatNumber(dashboard.vouchers.usedVouchers) }}
              </span>
            </div>
          </div>
        </div>

        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-800">Top khách hàng chi tiêu</h2>
            <p class="text-sm text-slate-500">Danh sách khách hàng nổi bật theo doanh thu</p>
          </div>

          <div
            v-if="!dashboard.topCustomers.length"
            class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-500"
          >
            Chưa có dữ liệu khách hàng
          </div>

          <div
            v-else
            class="space-y-3"
          >
            <div
              v-for="customer in dashboard.topCustomers"
              :key="customer.customerId"
              class="rounded-xl border border-slate-200 px-4 py-3"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <h3 class="font-semibold text-slate-800">{{ customer.customerName }}</h3>
                  <p class="mt-1 text-sm text-slate-500">
                    Loại khách: {{ customer.customerType }}
                  </p>
                </div>

                <div class="text-right">
                  <p class="font-semibold text-slate-800">
                    {{ formatCurrency(customer.totalSpent) }}
                  </p>
                  <p class="mt-1 text-xs text-slate-500">
                    {{ formatNumber(customer.totalOrders) }} đơn •
                    {{ formatNumber(customer.loyaltyPoints) }} điểm
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>