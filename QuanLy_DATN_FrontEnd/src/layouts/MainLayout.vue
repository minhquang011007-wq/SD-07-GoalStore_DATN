<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import Navbar from "@/components/Navbar.vue";
import Footer from "@/components/Footer.vue";
import {
  LayoutDashboard,
  Users,
  Building2,
  TrendingUp,
  CheckSquare,
  BarChart3,
  Settings,
  BookOpen,
  ChevronLeft,
  ChevronRight,
  CreditCard,
  LogOut
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

const sidebarOpen = ref(true);
const isMobile = ref(false);

// ====== ROLE ======
const role = ref<string>(localStorage.getItem("role") || "");

// lắng nghe thay đổi localStorage (nếu m login/logout ở tab khác)
const handleStorage = (e: StorageEvent) => {
  if (e.key === "role") role.value = localStorage.getItem("role") || "";
};

// ====== MENU (gắn roles) ======
type NavItem = {
  name: string;
  path: string;
  icon: any;
  roles?: Array<"ADMIN" | "SALES">;
};

const navigation: NavItem[] = [
  { name: "Dashboard", path: "/dashboard", icon: LayoutDashboard, roles: ["ADMIN", "SALES"] },
  { name: "Contacts", path: "/contacts", icon: Users, roles: ["ADMIN", "SALES"] },

  // Admin-only ví dụ
  { name: "Companies", path: "/companies", icon: Building2, roles: ["ADMIN"] },
  { name: "Reports", path: "/reports", icon: BarChart3, roles: ["ADMIN"] },
  { name: "Billing", path: "/billing", icon: CreditCard, roles: ["ADMIN"] },
  { name: "Settings", path: "/settings", icon: Settings, roles: ["ADMIN"] },

  // Sales + Admin
  { name: "Deals", path: "/deals", icon: TrendingUp, roles: ["ADMIN", "SALES"] },
  { name: "Tasks", path: "/tasks", icon: CheckSquare, roles: ["ADMIN", "SALES"] },

  // ai cũng xem được
  { name: "Docs", path: "/docs", icon: BookOpen, roles: ["ADMIN", "SALES"] }
];

// lọc menu theo role hiện tại
const filteredNavigation = computed(() => {
  // nếu chưa có role (chưa login) thì trả rỗng
  if (!role.value) return [];
  return navigation.filter((item) => !item.roles || item.roles.includes(role.value as any));
});

// ====== UI ======
const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024;
  sidebarOpen.value = !isMobile.value;
};

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
};

const closeSidebarOnMobile = () => {
  if (isMobile.value) sidebarOpen.value = false;
};

// ====== Logout (tuỳ chọn) ======
const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  role.value = "";
  router.push("/login");
};

onMounted(() => {
  checkMobile();
  window.addEventListener("resize", checkMobile);
  window.addEventListener("storage", handleStorage);
});

onUnmounted(() => {
  window.removeEventListener("resize", checkMobile);
  window.removeEventListener("storage", handleStorage);
});
</script>

<template>
  <div class="flex h-screen bg-background">
    <!-- Overlay mobile -->
    <div
      v-if="sidebarOpen && isMobile"
      @click="closeSidebarOnMobile"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
    />

    <!-- Sidebar -->
    <aside
      :class="[
        'bg-card border-r transition-all duration-300 flex flex-col fixed lg:relative h-full z-50 overflow-hidden',
        isMobile ? (sidebarOpen ? 'w-64' : 'w-0') : (sidebarOpen ? 'w-64' : 'w-16'),
        isMobile && !sidebarOpen ? '-translate-x-full' : 'translate-x-0'
      ]"
    >
      <!-- Header -->
      <div class="p-4 border-b flex items-center justify-between">
        <div class="flex items-center gap-2">
          <h3 v-if="sidebarOpen" class="text-sm font-semibold">Material Shadcn Vue</h3>
          <span v-if="sidebarOpen && role" class="text-xs text-muted-foreground">({{ role }})</span>
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

      <!-- Nav -->
      <nav class="flex-1 p-4 space-y-1 overflow-y-auto">
        <router-link
          v-for="item in filteredNavigation"
          :key="item.path"
          :to="item.path"
          @click="closeSidebarOnMobile"
          :class="[
            'flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm',
            route.path === item.path
              ? 'bg-primary text-primary-foreground'
              : 'hover:bg-accent hover:text-accent-foreground'
          ]"
        >
          <component :is="item.icon" :size="20" />
          <span v-if="sidebarOpen">{{ item.name }}</span>
        </router-link>

        <!-- Nếu chưa có role/token -->
        <div v-if="filteredNavigation.length === 0" class="text-sm text-muted-foreground">
          <span v-if="sidebarOpen">Chưa đăng nhập</span>
        </div>
      </nav>

      <!-- Footer sidebar: logout -->
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

    <!-- Main content -->
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