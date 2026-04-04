<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue"
import { RouterView, useRoute, useRouter } from "vue-router"
import Navbar from "@/shared/components/Navbar.vue"
import Footer from "@/shared/components/Footer.vue"
import { Shield } from "lucide-vue-next"
import {
  LayoutDashboard,
  Users,
  Building2,
  TrendingUp,
  CheckSquare,
  BarChart3,
  Settings,
  BookOpen,
  Boxes,
  ChevronLeft,
  ChevronRight,
  CreditCard,
  TicketPercent,
  LogOut,
} from "lucide-vue-next"

type RoleType = "ADMIN" | "SALES" | "INVENTORY"
type NavItem = {
  name: string
  path: string
  icon: any
  roles?: RoleType[]
  note?: string
}

const route = useRoute()
const router = useRouter()
const sidebarOpen = ref(true)
const isMobile = ref(false)
const role = ref<string>(localStorage.getItem("role") || "")

const navigation: NavItem[] = [
  { name: "Admin Home", path: "/admin", icon: LayoutDashboard, roles: ["ADMIN"], note: "Chung" },
  { name: "Sales Home", path: "/sales", icon: LayoutDashboard, roles: ["SALES"], note: "Chung" },
  { name: "Inventory Home", path: "/inventory", icon: LayoutDashboard, roles: ["INVENTORY"], note: "Chung" },

  { name: "Products", path: "/inventory/products", icon: Boxes, roles: ["ADMIN", "INVENTORY"], note: "Trang quản lý kho hàng" },
  { name: "Customers", path: "/sales/customers", icon: Users, roles: ["ADMIN", "SALES"], note: "Trang quản lý khách hàng" },
  { name: "Companies", path: "/sales/companies", icon: Building2, roles: ["ADMIN", "SALES"], note: "Module Customer" },
  { name: "Deals", path: "/sales/deals", icon: TrendingUp, roles: ["ADMIN", "SALES"], note: "Module Sales" },
  { name: "Tasks", path: "/sales/tasks", icon: CheckSquare, roles: ["ADMIN", "SALES"], note: "Module Sales" },
  { name: "Orders", path: "/admin/orders", icon: CreditCard, roles: ["ADMIN"], note: "Trang quản lý đơn hàng" },
  { name: "Vouchers", path: "/admin/vouchers", icon: TicketPercent, roles: ["ADMIN"], note: "Trang quản lý voucher" },
  { name: "Reports", path: "/admin/reports", icon: BarChart3, roles: ["ADMIN"], note: "Trang quản lý báo cáo" },
  { name: "Docs", path: "/admin/docs", icon: BookOpen, roles: ["ADMIN"], note: "Trang tra cứu tài liệu" },
  { name: "Settings", path: "/admin/settings", icon: Settings, roles: ["ADMIN"], note: "Trang quản lý tài khoản" },
  { name: "Permissions", path: "/admin/permissions", icon: Shield, roles: ["ADMIN"], note: "Trang quản lý chức năng" },
]

const filteredNavigation = computed(() => {
  if (!role.value) return []
  return navigation.filter((item) => !item.roles || item.roles.includes(role.value as RoleType))
})

const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024
  sidebarOpen.value = !isMobile.value
}

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebarOnMobile = () => {
  if (isMobile.value) sidebarOpen.value = false
}

const logout = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("role")
  localStorage.removeItem("username")
  role.value = ""
  router.replace("/login")
}

const handleStorage = (e: StorageEvent) => {
  if (e.key === "role" || e.key === "token") {
    role.value = localStorage.getItem("role") || ""
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener("resize", checkMobile)
  window.addEventListener("storage", handleStorage)
})

onUnmounted(() => {
  window.removeEventListener("resize", checkMobile)
  window.removeEventListener("storage", handleStorage)
})
</script>

<template>
  <div class="flex h-screen bg-background">
    <div
      v-if="sidebarOpen && isMobile"
      @click="closeSidebarOnMobile"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
    />

    <aside
      :class="[
        'bg-card border-r transition-all duration-300 flex flex-col fixed lg:relative h-full z-50 overflow-hidden',
        isMobile ? (sidebarOpen ? 'w-72' : 'w-0') : (sidebarOpen ? 'w-72' : 'w-16'),
        isMobile && !sidebarOpen ? '-translate-x-full' : 'translate-x-0'
      ]"
    >
      <div class="p-4 border-b flex items-center justify-between">
        <div class="flex items-center gap-2 overflow-hidden">
          <div>
            <h3 v-if="sidebarOpen" class="text-sm font-semibold">GoalStore </h3>
          </div>
        </div>

        <button
          @click="toggleSidebar"
          class="p-2 hover:bg-accent rounded-md hidden lg:block"
          :class="{ 'mx-auto': !sidebarOpen }"
        >
          <ChevronRight v-if="!sidebarOpen" :size="20" />
          <ChevronLeft v-else :size="20" />
        </button>
      </div>

      <nav class="flex-1 p-4 space-y-1 overflow-y-auto">
        <router-link
          v-for="item in filteredNavigation"
          :key="item.path"
          :to="item.path"
          @click="closeSidebarOnMobile"
          :class="[
            'flex items-start gap-2 px-3 py-2 rounded-lg transition-colors text-sm',
            route.path === item.path
              ? 'bg-primary text-primary-foreground'
              : 'hover:bg-accent hover:text-accent-foreground'
          ]"
        >
          <component :is="item.icon" :size="20" class="mt-0.5 shrink-0" />
          <div v-if="sidebarOpen" class="min-w-0">
            <div>{{ item.name }}</div>
            <div class="text-[11px] opacity-80">{{ item.note }}</div>
          </div>
        </router-link>
      </nav>

      <div class="p-4 border-t">
        <button
          v-if="role"
          @click="logout"
          class="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm hover:bg-accent"
        >
          <LogOut :size="18" />
          <span v-if="sidebarOpen">Logout</span>
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col overflow-hidden">
      <Navbar :on-toggle-sidebar="toggleSidebar" />
      <main class="flex-1 overflow-auto">
        <div class="p-4 md:p-8">
          <RouterView />
        </div>
      </main>
      <Footer />
    </div>
  </div>
</template>
