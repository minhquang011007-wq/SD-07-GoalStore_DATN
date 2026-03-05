// src/router/index.ts
import { createRouter, createWebHistory } from "vue-router"
import MainLayout from "@/layouts/MainLayout.vue"

type Role = "ADMIN" | "SALES" | "INVENTORY"

const HOME_BY_ROLE: Record<Role, string> = {
  ADMIN: "/admin",
  SALES: "/sales",
  INVENTORY: "/inventory",
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: () => import("@/views/Login.vue") },

    {
      path: "/",
      component: MainLayout,
      children: [
        { path: "", redirect: "/login" },

        { path: "admin", component: () => import("@/views/AdminHome.vue"), meta: { roles: ["ADMIN"] } },
        { path: "sales", component: () => import("@/views/SalesHome.vue"), meta: { roles: ["SALES"] } },
        { path: "inventory", component: () => import("@/views/InventoryHome.vue"), meta: { roles: ["INVENTORY"] } },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem("token")
  const role = (localStorage.getItem("role") || "") as Role | ""

  // chưa login
  if (!token && to.path !== "/login") return next("/login")

  // đã login mà vào /login -> đá về đúng home theo role
  if (token && to.path === "/login") {
    if (role && HOME_BY_ROLE[role]) return next(HOME_BY_ROLE[role])
    return next("/login")
  }

  // check role theo meta
  const allowedRoles = (to.meta?.roles as Role[] | undefined)
  if (allowedRoles && (!role || !allowedRoles.includes(role))) {
    // role lạ -> login, role đúng -> home theo role
    if (role && HOME_BY_ROLE[role]) return next(HOME_BY_ROLE[role])
    return next("/login")
  }

  next()
})

export default router