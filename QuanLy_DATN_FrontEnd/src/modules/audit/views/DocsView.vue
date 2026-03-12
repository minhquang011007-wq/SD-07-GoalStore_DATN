<template>
  <div class="space-y-6">
    <div class="rounded-2xl border bg-white p-6 shadow-sm">
      <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">Docs</h1>
          <p class="mt-1 text-sm text-slate-500">
            Tra cứu nhanh tài liệu module, route và phân quyền sử dụng.
          </p>
        </div>

        <div class="flex flex-col gap-3 sm:flex-row">
          <input
            v-model="search"
            type="text"
            placeholder="Tìm module, route..."
            class="rounded-xl border px-4 py-2 text-sm outline-none focus:border-slate-400"
          />

          <select
            v-model="selectedModule"
            class="rounded-xl border px-4 py-2 text-sm outline-none focus:border-slate-400"
          >
            <option value="">Tất cả module</option>
            <option value="system">System</option>
            <option value="audit">Audit</option>
            <option value="user">User</option>
            <option value="product">Product</option>
            <option value="sales">Sales</option>
            <option value="customer">Customer</option>
            <option value="order">Order</option>
          </select>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-2">
      <div
        v-for="item in filteredDocs"
        :key="item.route"
        class="rounded-2xl border bg-white p-6 shadow-sm"
      >
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="text-lg font-semibold text-slate-900">{{ item.title }}</h2>
            <p class="mt-2 text-sm text-slate-500">{{ item.description }}</p>
          </div>

          <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
            {{ item.module }}
          </span>
        </div>

        <div class="mt-4 space-y-2 text-sm">
          <p>
            <span class="font-medium text-slate-700">Route:</span>
            <span class="text-slate-500">{{ item.route }}</span>
          </p>
          <p>
            <span class="font-medium text-slate-700">Role:</span>
            <span class="text-slate-500">{{ item.role }}</span>
          </p>
        </div>

        <div class="mt-4 flex gap-3">
          <button
            class="rounded-xl border px-4 py-2 text-sm text-slate-700 hover:bg-slate-50"
            @click="copyRoute(item.route)"
          >
            Copy route
          </button>

          <button
            class="rounded-xl bg-slate-900 px-4 py-2 text-sm text-white hover:bg-slate-800"
            @click="goTo(item.route)"
          >
            Mở trang
          </button>
        </div>
      </div>
    </div>

    <div v-if="filteredDocs.length === 0" class="rounded-2xl border bg-white p-10 text-center text-sm text-slate-400 shadow-sm">
      Không tìm thấy tài liệu phù hợp.
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()
const search = ref("")
const selectedModule = ref("")

const docs = [
  {
    title: "Reports",
    module: "audit",
    route: "/admin/reports",
    role: "ADMIN",
    description: "Xem dashboard audit, login attempts và security alerts.",
  },
  {
    title: "Docs",
    module: "audit",
    route: "/admin/docs",
    role: "ADMIN",
    description: "Tài liệu mô tả route, module và phân quyền hệ thống.",
  },
  {
    title: "Settings",
    module: "user",
    route: "/admin/settings",
    role: "ADMIN",
    description: "Cập nhật hồ sơ tài khoản và thay đổi cài đặt cá nhân.",
  },
  {
    title: "Admin Dashboard",
    module: "system",
    route: "/admin",
    role: "ADMIN",
    description: "Trang tổng quan cho quản trị viên.",
  },
  {
    title: "Sales Dashboard",
    module: "system",
    route: "/sales",
    role: "SALES",
    description: "Trang tổng quan cho bộ phận bán hàng.",
  },
  {
    title: "Inventory Dashboard",
    module: "system",
    route: "/inventory",
    role: "INVENTORY",
    description: "Trang tổng quan cho kho.",
  },
  {
    title: "Product",
    module: "product",
    route: "/inventory/products",
    role: "ADMIN, INVENTORY",
    description: "Quản lý sản phẩm và hình ảnh.",
  },
  {
    title: "Order Billing",
    module: "order",
    route: "/admin/orders",
    role: "ADMIN",
    description: "Theo dõi đơn hàng và thanh toán.",
  },
  {
    title: "Deals",
    module: "sales",
    route: "/sales/deals",
    role: "ADMIN, SALES",
    description: "Quản lý cơ hội bán hàng.",
  },
  {
    title: "Tasks",
    module: "sales",
    route: "/sales/tasks",
    role: "ADMIN, SALES",
    description: "Quản lý công việc của sales.",
  },
  {
    title: "Contacts",
    module: "customer",
    route: "/sales/customers",
    role: "ADMIN, SALES",
    description: "Quản lý liên hệ khách hàng.",
  },
  {
    title: "Companies",
    module: "customer",
    route: "/sales/companies",
    role: "ADMIN, SALES",
    description: "Quản lý công ty khách hàng.",
  },
]

const filteredDocs = computed(() => {
  return docs.filter((item) => {
    const okModule = !selectedModule.value || item.module === selectedModule.value
    const q = search.value.trim().toLowerCase()

    const okSearch =
      !q ||
      item.title.toLowerCase().includes(q) ||
      item.route.toLowerCase().includes(q) ||
      item.description.toLowerCase().includes(q) ||
      item.role.toLowerCase().includes(q)

    return okModule && okSearch
  })
})

async function copyRoute(route: string) {
  try {
    await navigator.clipboard.writeText(route)
    alert(`Đã copy: ${route}`)
  } catch (error) {
    console.error("Copy error:", error)
  }
}

function goTo(route: string) {
  router.push(route)
}
</script>