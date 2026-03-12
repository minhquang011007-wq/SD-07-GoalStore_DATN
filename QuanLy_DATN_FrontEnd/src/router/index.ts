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
          redirect: "/sales/customer-loyalty/customers",
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "sales/companies",
          component: () => import("@/modules/customer/views/CompaniesView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },

        {
          path: "sales/customer-loyalty/customers",
          component: () => import("@/modules/customer/views/CustomerListView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "sales/customer-loyalty/customers/create",
          component: () => import("@/modules/customer/views/CustomerCreateView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "sales/customer-loyalty/customers/:id/edit",
          component: () => import("@/modules/customer/views/CustomerEditView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
          props: true,
        },
        {
          path: "sales/customer-loyalty/customers/:id",
          component: () => import("@/modules/customer/views/CustomerDetailView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
          props: true,
        },
        {
          path: "sales/customer-loyalty/customers/:id/orders",
          component: () => import("@/modules/customer/views/CustomerOrdersView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
          props: true,
        },
        {
          path: "sales/customer-loyalty/customers-top-spending",
          component: () => import("@/modules/customer/views/CustomerTopSpendingView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },
        {
          path: "sales/customer-loyalty/customers-inactive",
          component: () => import("@/modules/customer/views/CustomerInactiveView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "customer" },
        },

        {
          path: "sales/customer-loyalty/loyalty",
          component: () => import("@/modules/loyalty/views/LoyaltyListView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
        },
        {
          path: "sales/customer-loyalty/loyalty/:customerId/history",
          component: () => import("@/modules/loyalty/views/LoyaltyHistoryView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
          props: true,
        },
        {
          path: "sales/customer-loyalty/loyalty/vip-programs",
          component: () => import("@/modules/loyalty/views/VipProgramsView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
        },
        {
          path: "sales/customer-loyalty/loyalty/:customerId/vip-history",
          component: () => import("@/modules/loyalty/views/VipHistoryView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
          props: true,
        },
        {
          path: "sales/customer-loyalty/loyalty/reward-rules",
          component: () => import("@/modules/loyalty/views/RewardRulesView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
        },
        {
          path: "sales/customer-loyalty/loyalty/:customerId/birthday-logs",
          component: () => import("@/modules/loyalty/views/BirthdayLogsView.vue"),
          meta: { roles: ["ADMIN", "SALES"], moduleOwner: "loyalty" },
          props: true,
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

  const allowedRoles = to.meta?.roles as Role[] | undefined
  if (allowedRoles && (!role || !allowedRoles.includes(role))) {
    if (role && HOME_BY_ROLE[role]) return next(HOME_BY_ROLE[role])
    return next("/login")
  }

  next()
})

export default router