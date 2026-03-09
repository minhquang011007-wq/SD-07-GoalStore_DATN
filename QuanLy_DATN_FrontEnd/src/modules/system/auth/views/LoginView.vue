<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-100 px-4">
    <div class="w-full max-w-md rounded-2xl border bg-white p-6 shadow-sm">
      <h2 class="text-2xl font-semibold mb-2">Đăng nhập GoalStore</h2>
      <p class="text-sm text-slate-500 mb-4">Module chung, chỉ 1 người nên giữ.</p>

      <input v-model="username" placeholder="Username" class="w-full rounded-lg border px-3 py-2 mb-3" @keyup.enter="handleLogin" />
      <input v-model="password" type="password" placeholder="Password" class="w-full rounded-lg border px-3 py-2 mb-4" @keyup.enter="handleLogin" />

      <button @click="handleLogin" :disabled="loading" class="w-full rounded-lg bg-slate-900 text-white py-2">
        {{ loading ? "Đang đăng nhập..." : "Login" }}
      </button>

      <p v-if="error" class="text-red-600 mt-3 text-sm">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/shared/lib/api"

const router = useRouter()
const username = ref("")
const password = ref("")
const error = ref("")
const loading = ref(false)

async function handleLogin() {
  error.value = ""
  if (!username.value || !password.value) {
    error.value = "Vui lòng nhập username và password"
    return
  }

  loading.value = true
  try {
    const res = await api.post("/api/auth/login", {
      username: username.value,
      password: password.value,
    })

    localStorage.setItem("token", res.data.token)
    localStorage.setItem("role", res.data.role)
    localStorage.setItem("username", res.data.username)

    if (res.data.role === "ADMIN") router.push("/admin")
    else if (res.data.role === "SALES") router.push("/sales")
    else if (res.data.role === "INVENTORY") router.push("/inventory")
    else router.push("/login")
  } catch (e: any) {
    error.value = e?.response?.data?.message || "Sai tài khoản hoặc mật khẩu"
  } finally {
    loading.value = false
  }
}
</script>
