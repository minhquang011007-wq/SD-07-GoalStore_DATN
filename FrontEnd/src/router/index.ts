import { createRouter, createWebHistory } from "vue-router"
import MainLayout from "@/shared/layouts/MainLayout.vue"
import { getRole } from "@/shared/lib/auth"
import { hydrateSessionFromUrl } from "@/shared/lib/cross-app-auth"

type StaffRole = "ADMIN" | "SALES" | "INVENTORY"
type Role = StaffRole | "CUSTOMER"


const HOME_BY_ROLE: Record<StaffRole, string> = {
  ADMIN: "/admin",
  SALES: "/sales",
  INVENTORY: "/inventory",
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      component: () => import("@/modules/system/auth/views/LoginView.vue"),
      meta: { public: true },
    },
    {
      path: "/",
      component: MainLayout,
      children: [
        {
          path: "",
          redirect: () => {
            const role = getRole() as Role | ""
            if (!role || role === "CUSTOMER") return "/login"
            return HOME_BY_ROLE[role as StaffRole] || "/admin"
          },
        },
        {
          path: "admin",
          component: () => import("@/modules/system/dashboard/views/AdminHomeView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "system" },
        },
        {
          path: "sales",
          component: () => import("@/modules/system/dashboard/views/SalesHomeView.vue"),
          meta: { roles: ["SALES"], moduleOwner: "system" },
        },
        {
          path: "inventory",
          component: () => import("@/modules/system/dashboard/views/InventoryHomeView.vue"),
          meta: { roles: ["INVENTORY"], moduleOwner: "system" },
        },
        {
          path: "inventory/products",
          component: () => import("@/modules/product/views/ProductHomeView.vue"),
          meta: { roles: ["ADMIN", "INVENTORY"], moduleOwner: "product" },
        },
        {
          path: "admin/orders",
          component: () => import("@/modules/order/views/OrderBillingView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "order" },
        },
        {
          path: "sales/deals",
          component: () => import("@/modules/sales/views/DealsView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "sales" },
        },
        {
          path: "sales/tasks",
          component: () => import("@/modules/sales/views/TasksView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "sales" },
        },
        {
          path: "sales/customers",
          component: () => import("@/modules/customer/views/ContactsView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "sales/companies",
          component: () => import("@/modules/customer/views/CompaniesView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "admin/settings",
          component: () => import("@/modules/user/views/SettingsView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "user" },
        },
        {
          path: "admin/reports",
          component: () => import("@/modules/audit/views/ReportsView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "audit" },
        },
        {
          path: "admin/docs",
          component: () => import("@/modules/audit/views/DocsView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "audit" },
        },
        {
          path: "admin/permissions",
          component: () => import("@/modules/permission/views/PermissionHomeView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "permission" },
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  hydrateSessionFromUrl()

  const token = localStorage.getItem("token")
  const isPublic = Boolean(to.meta?.public)
  const role = getRole() as Role | ""

  // Chưa đăng nhập
  if (!token) {
    if (isPublic) return next()
    return next("/login")
  }

  // Đã đăng nhập mà vào login
  if (to.path === "/login") {
    if (role && role !== "CUSTOMER") {
      return next(HOME_BY_ROLE[role as StaffRole] || "/admin")
    }
    return next()
  }

  // FE quản lý chỉ cho staff vào
  const allowedRoles = to.meta?.roles as StaffRole[] | undefined
  if (allowedRoles) {
    if (!role || role === "CUSTOMER" || !allowedRoles.includes(role as StaffRole)) {
      return next("/login")
    }
  }

  next()
})

export default router
