import { createRouter, createWebHistory } from "vue-router"
import MainLayout from "@/shared/layouts/MainLayout.vue"

type Role = "ADMIN" | "SALES" | "INVENTORY"

const HOME_BY_ROLE: Record<Role, string> = {
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
      meta: { shared: true },
    },
    {
      path: "/",
      component: MainLayout,
      children: [
        { path: "", redirect: "/login" },

        // ===== Home theo role =====
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

        // ===== Product module =====
        {
          path: "inventory/products",
          component: () => import("@/modules/product/views/ProductHomeView.vue"),
          meta: { roles: ["ADMIN", "INVENTORY"], moduleOwner: "product" },
        },

        // ===== Order + Sales module =====
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

        // ===== Customer module =====
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

        // ===== User module =====
        {
          path: "admin/settings",
          component: () => import("@/modules/user/views/SettingsView.vue"),
          meta: { roles: ["ADMIN"], moduleOwner: "user" },
        },

        // ===== Audit module =====
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
        // ... trong children của path "/"
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
  const token = localStorage.getItem("token")
  const role = (localStorage.getItem("role") || "") as Role | ""

  if (!token && to.path !== "/login") return next("/login")

  if (token && to.path === "/login") {
    if (role && HOME_BY_ROLE[role]) return next(HOME_BY_ROLE[role])
    return next("/login")
  }

  const allowedRoles = (to.meta?.roles as Role[] | undefined)
  if (allowedRoles && (!role || !allowedRoles.includes(role))) {
    if (role && HOME_BY_ROLE[role]) return next(HOME_BY_ROLE[role])
    return next("/login")
  }

  next()
})

export default router
