<script setup lang="ts">
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { clearSession, saveSession, type AuthSession } from "@/shared/lib/auth"
import { buildCrossAppUrl } from "@/shared/lib/cross-app-auth"
import { loginSystem } from "@/modules/system/services/auth.api"
import { loginCustomer } from "@/modules/system/services/customer-auth.api"

const router = useRouter()
const STOREFRONT_URL = (import.meta.env.VITE_STOREFRONT_URL as string | undefined) || "http://localhost:5173"

const loading = ref(false)
const error = ref("")
const success = ref("")

const loginForm = reactive({
  email: "",
  password: "",
})

function resetMessages() {
  error.value = ""
  success.value = ""
}

async function goAfterLogin(session: AuthSession) {
  if (session.role === "CUSTOMER") {
    window.location.href = buildCrossAppUrl(STOREFRONT_URL, session, "/")
    return
  }

  const role = session.role

  if (role === "ADMIN") return router.push("/admin")
  if (role === "SALES") return router.push("/sales")
  if (role === "INVENTORY") return router.push("/inventory")

  return router.push("/admin")
}

async function handleLogin() {
  resetMessages()

  if (!loginForm.email.trim() || !loginForm.password.trim()) {
    error.value = "Vui lòng nhập email và mật khẩu"
    return
  }

  loading.value = true
  try {
    try {
      const staffData = await loginSystem({
        email: loginForm.email.trim(),
        password: loginForm.password,
      })

      const session = {
        token: staffData.token,
        role: staffData.role,
        email: staffData.email,
        displayName: staffData.email,
      } as AuthSession

      saveSession(session)

      success.value = "Đăng nhập thành công"
      await goAfterLogin(session)
      return
    } catch {
      const customerData = await loginCustomer({
        email: loginForm.email.trim(),
        password: loginForm.password,
      })

      const session = {
        token: customerData.token,
        role: customerData.role,
        email: customerData.email,
        displayName: customerData.ten,
        customerId: customerData.customerId,
      } as AuthSession

      saveSession(session)

      success.value = "Đăng nhập khách hàng thành công"
      await goAfterLogin(session)
    }
  } catch (e: any) {
    clearSession()
    error.value = e?.response?.data?.message || "Sai email hoặc mật khẩu"
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-[linear-gradient(135deg,#f8fafc_0%,#eff6ff_45%,#ffffff_100%)] px-4 py-10">
    <div class="mx-auto grid max-w-6xl overflow-hidden rounded-[32px] bg-white shadow-2xl shadow-slate-200/70 lg:grid-cols-[1.05fr_0.95fr]">
      <section class="hidden bg-slate-950 p-10 text-white lg:flex lg:flex-col lg:justify-between">
        <div>
          <p class="text-sm uppercase tracking-[0.35em] text-slate-300">GoalStore</p>
          <h1 class="mt-6 max-w-md text-5xl font-semibold leading-tight">
            Đăng nhập hệ thống quản lý.
          </h1>
          <p class="mt-6 max-w-lg text-base leading-8 text-slate-300">
            Dùng chung một form đăng nhập. Hệ thống sẽ tự nhận diện tài khoản và chuyển đúng frontend.
          </p>
        </div>

        <div class="grid gap-4 rounded-3xl border border-white/10 bg-white/5 p-6 text-sm text-slate-200">
          <div>
            <p class="font-semibold text-white">Tài khoản nội bộ</p>
            <p class="mt-1 text-slate-300">
              ADMIN, SALES, INVENTORY sẽ vào đúng khu vực quản trị tương ứng.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Tài khoản khách hàng</p>
            <p class="mt-1 text-slate-300">
              CUSTOMER sẽ được chuyển sang frontend bán hàng online sau khi đăng nhập thành công.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Tách riêng 2 FE</p>
            <p class="mt-1 text-slate-300">
              Quản trị và bán online vẫn tách project, nhưng luồng đăng nhập đã nối với nhau.
            </p>
          </div>
        </div>
      </section>

      <section class="p-6 sm:p-8 lg:p-10">
        <div class="mx-auto max-w-xl">
          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">Auth Center</p>
          <h2 class="mt-3 text-3xl font-semibold text-slate-950">Đăng nhập GoalStore</h2>
          <p class="mt-3 text-sm leading-6 text-slate-500">
            Nhập email và mật khẩu. Hệ thống sẽ tự chuyển ADMIN / SALES / INVENTORY / CUSTOMER về đúng frontend.
          </p>

          <form class="mt-8 space-y-4" @submit.prevent="handleLogin">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Email</label>
              <input
                v-model="loginForm.email"
                type="email"
                placeholder="you@example.com"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Mật khẩu</label>
              <input
                v-model="loginForm.password"
                type="password"
                placeholder="••••••••"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div v-if="error" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              {{ error }}
            </div>

            <div v-if="success" class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-600">
              {{ success }}
            </div>

            <button
              type="submit"
              class="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="loading"
            >
              {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
            </button>

            <p class="text-center text-sm text-slate-500">
              Khách hàng có thể đăng nhập trực tiếp ở storefront nếu muốn.
            </p>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>
