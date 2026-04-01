<script setup lang="ts">
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { clearSession, saveSession, type AuthSession } from "@/shared/lib/auth"
import { buildCrossAppUrl } from "@/shared/lib/cross-app-auth"
import { loginStaff, loginCustomer } from "@/shared/lib/auth.api"

const router = useRouter()
const ADMIN_URL = (import.meta.env.VITE_ADMIN_URL as string | undefined) || "http://localhost:5000"

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
    await router.push("/")
    return
  }

  window.location.href = buildCrossAppUrl(ADMIN_URL, session, "/")
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
      const staffData = await loginStaff({
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
      <section class="relative hidden min-h-[760px] overflow-hidden bg-slate-950 lg:block">
        <img
          src="/legacy/img/anhlogin.jpg"
          alt="GoalStore banner"
          class="absolute inset-0 h-full w-full object-cover"
        />
      </section>

      <section class="p-6 sm:p-8 lg:p-10">
        <div class="mx-auto max-w-xl">
          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">GOALSTORE</p>
          <h2 class="mt-3 text-3xl font-semibold text-slate-950">Đăng nhập</h2>
          <p class="mt-3 text-sm leading-6 text-slate-500">
            Đăng nhập để có trải nghiệm tốt hơn
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
              Chưa có tài khoản?
              <RouterLink to="/register" class="font-medium text-sky-600 hover:text-sky-700">
                Đăng ký ngay
              </RouterLink>
            </p>

            <p class="text-center text-sm text-slate-500">
              Khách hàng có thể đăng nhập trực tiếp ở storefront nếu muốn.
            </p>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>
