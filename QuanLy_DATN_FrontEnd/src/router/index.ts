import { createRouter, createWebHistory } from "vue-router"
import MainLayout from "@/layouts/MainLayout.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      component: () => import("@/views/Login.vue"),
    },
    {
      path: "/",
      component: MainLayout,
      children: [
        {
          path: "",
          redirect: "/login",
        },
        {
          path: "admin",
          component: () => import("@/views/AdminHome.vue"),
        },
        {
          path: "sales",
          component: () => import("@/views/SalesHome.vue"),
        },
      ],
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token")
  const role = localStorage.getItem("role")

  // 🚫 chưa login thì luôn về login
  if (!token && to.path !== "/login") {
    return next("/login")
  }

  // ✅ đã login mà vào /login thì redirect đúng role
  if (token && to.path === "/login") {
    if (role === "ADMIN") return next("/admin")
    if (role === "SALES") return next("/sales")
  }

  // 🔐 chặn sai role
  if (to.path.startsWith("/admin") && role !== "ADMIN") {
    return next("/sales")
  }

  if (to.path.startsWith("/sales") && role !== "SALES") {
    return next("/admin")
  }

  next()
})

export default router