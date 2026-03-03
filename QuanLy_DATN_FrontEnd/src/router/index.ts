import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layouts/MainLayout.vue";

const router = createRouter({
  history: createWebHistory("/material-dashboard-shadcn-vue/"),
  routes: [
    // ✅ Login đứng riêng, không dính MainLayout
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/Login.vue"),
    },

    // ✅ App layout (dashboard, contacts, ...)
    {
      path: "/",
      component: MainLayout,
      children: [
        {
          path: "",
          redirect: "/dashboard",
        },
        {
            path: "admin",
            name: "AdminHome",
            component: () => import("@/views/AdminHome.vue"),
            meta: { requiresAuth: true, roles: ["ADMIN"] },
        },
        {
            path: "sales",
            name: "SalesHome",
            component: () => import("@/views/SalesHome.vue"),
            meta: { requiresAuth: true, roles: ["SALES"] },
        },
        {
            path: "dashboard",
            name: "Dashboard",
            component: () => import("@/views/Dashboard.vue"),
            meta: { requiresAuth: true, role: "ADMIN" }
        },
        {
            path: "contacts",
            name: "Contacts",
            component: () => import("@/views/Contacts.vue"),
            // meta: { requiresAuth: true, role: "SALES" } cái này là cấm quyền kiểu như trang contacts nay chi danh cho sales admin khong vao dc
        },
        {
          path: "companies",
          name: "Companies",
          component: () => import("@/views/Companies.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "deals",
          name: "Deals",
          component: () => import("@/views/Deals.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "tasks",
          name: "Tasks",
          component: () => import("@/views/Tasks.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "reports",
          name: "Reports",
          component: () => import("@/views/Reports.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "billing",
          name: "Billing",
          component: () => import("@/views/Billing.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "settings",
          name: "Settings",
          component: () => import("@/views/Settings.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "docs",
          name: "Docs",
          component: () => import("@/views/Docs.vue"),
          meta: { requiresAuth: true },
        },
      ],
    },
  ],
});

// ✅ Guard: chưa có token thì đá về /login
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
  
    if (to.meta.requiresAuth && !token) return next("/login");
  
    const roles = (to.meta as any).roles as string[] | undefined;
    if (roles && role && !roles.includes(role)) {
      // sai role thì đá về đúng trang của nó
      return next(role === "ADMIN" ? "/admin" : "/sales");
    }
  
    // đã login mà vào /login -> đá về đúng role
    if (to.path === "/login" && token) {
      return next(role === "ADMIN" ? "/admin" : "/sales");
    }
  
    next();
  });

export default router;