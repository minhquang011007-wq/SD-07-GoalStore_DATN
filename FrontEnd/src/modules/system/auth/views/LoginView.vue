<script setup lang="ts">
import { computed, reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { clearSession, saveSession } from "@/shared/lib/auth"
import {loginCustomer, loginSystem, registerCustomer } from "@/modules/system/services/auth.api"

type AuthTab = "login" | "register"

const router = useRouter()

const activeTab = ref<AuthTab>("login")
const loading = ref(false)
const googleLoading = ref(false)
const error = ref("")
const success = ref("")

const loginForm = reactive({
  email: "",
  password: "",
})

const customerRegisterForm = reactive({
  ten: "",
  email: "",
  sdt: "",
  ngaySinh: "",
  password: "",
  confirmPassword: "",
})

const tabTitle = computed(() => {
  return activeTab.value === "register"
    ? "Tạo tài khoản khách hàng"
    : "Đăng nhập hệ thống"
})

function resetMessages() {
  error.value = ""
  success.value = ""
}

async function goAfterLogin(role: string) {
  if (role === "ADMIN") return router.push("/admin")
  if (role === "SALES") return router.push("/sales")
  if (role === "INVENTORY") return router.push("/inventory")
  return router.push("/")
}

async function handleLogin() {
  resetMessages()

  if (!loginForm.email.trim() || !loginForm.password.trim()) {
    error.value = "Vui lòng nhập email và mật khẩu"
    return
  }

  loading.value = true
  try {
    // 🔥 thử login hệ thống trước (admin/sales/inventory)
    try {
      const data = await loginSystem({
        email: loginForm.email.trim(),
        password: loginForm.password,
      })

      saveSession({
        token: data.token,
        role: data.role,
        email: data.email,
        displayName: data.email,
      })

      success.value = "Đăng nhập thành công"
      await goAfterLogin(data.role)
      return
    } catch (err: any) {
      // ❌ nếu KHÔNG phải 401 thì lỗi thật → throw luôn
      if (err?.response?.status !== 401) {
        throw err
      }
    }

    // 🔥 fallback sang customer login
    const customer = await loginCustomer({
      email: loginForm.email.trim(),
      password: loginForm.password,
    })

    saveSession({
      token: customer.token,
      role: customer.role,
      email: customer.email,
      customerId: customer.customerId,
      displayName: customer.ten,
    })

    success.value = "Đăng nhập thành công"
    await goAfterLogin(customer.role)

  } catch (e: any) {
    clearSession()
    error.value = e?.response?.data?.message || "Sai email hoặc mật khẩu"
  } finally {
    loading.value = false
  }
}

async function handleCustomerRegister() {
  resetMessages()

  if (!customerRegisterForm.ten.trim() || !customerRegisterForm.email.trim() || !customerRegisterForm.password.trim()) {
    error.value = "Họ tên, email và mật khẩu là bắt buộc"
    return
  }

  if (customerRegisterForm.password.length < 6) {
    error.value = "Mật khẩu phải từ 6 ký tự"
    return
  }

  if (customerRegisterForm.password !== customerRegisterForm.confirmPassword) {
    error.value = "Mật khẩu nhập lại không khớp"
    return
  }

  loading.value = true
  try {
    await registerCustomer({
      ten: customerRegisterForm.ten.trim(),
      email: customerRegisterForm.email.trim(),
      sdt: customerRegisterForm.sdt.trim() || undefined,
      ngaySinh: customerRegisterForm.ngaySinh || undefined,
      password: customerRegisterForm.password,
    })

    clearSession()
    success.value = "Đăng ký thành công, vui lòng đăng nhập"

    loginForm.email = customerRegisterForm.email.trim()
    loginForm.password = ""

    customerRegisterForm.ten = ""
    customerRegisterForm.email = ""
    customerRegisterForm.sdt = ""
    customerRegisterForm.ngaySinh = ""
    customerRegisterForm.password = ""
    customerRegisterForm.confirmPassword = ""

    activeTab.value = "login"
  } catch (e: any) {
    clearSession()
    error.value = e?.response?.data?.message || "Đăng ký thất bại"
  } finally {
    loading.value = false
  }
}

function handleGoogleLogin() {
  resetMessages()
  googleLoading.value = true

  window.setTimeout(() => {
    googleLoading.value = false
    error.value = "Backend hiện chưa có API đăng nhập Google. Khi backend có OAuth thì nối tiếp được ngay."
  }, 500)
}
</script>

<template>
  <div class="min-h-screen bg-[linear-gradient(135deg,#f8fafc_0%,#eff6ff_45%,#ffffff_100%)] px-4 py-10">
    <div class="mx-auto grid max-w-6xl overflow-hidden rounded-[32px] bg-white shadow-2xl shadow-slate-200/70 lg:grid-cols-[1.05fr_0.95fr]">
      <section class="hidden bg-slate-950 p-10 text-white lg:flex lg:flex-col lg:justify-between">
        <div>
          <p class="text-sm uppercase tracking-[0.35em] text-slate-300">GoalStore</p>
          <h1 class="mt-6 max-w-md text-5xl font-semibold leading-tight">
            Đăng nhập và đăng ký trong cùng một màn hình.
          </h1>
          <p class="mt-6 max-w-lg text-base leading-8 text-slate-300">
            Đăng nhập bằng email cho toàn bộ hệ thống. Sau khi đăng nhập, hệ thống sẽ tự điều hướng theo vai trò tài khoản.
          </p>
        </div>

        <div class="grid gap-4 rounded-3xl border border-white/10 bg-white/5 p-6 text-sm text-slate-200">
          <div>
            <p class="font-semibold text-white">Đăng nhập bằng email</p>
            <p class="mt-1 text-slate-300">
              Admin, sales, inventory và khách hàng đều dùng email + mật khẩu để đăng nhập.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Điều hướng theo role</p>
            <p class="mt-1 text-slate-300">
              ADMIN vào trang quản trị, SALES vào bán hàng, INVENTORY vào kho, CUSTOMER vào trang mua hàng online.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Google</p>
            <p class="mt-1 text-slate-300">
              Đã có nút giao diện, nhưng backend hiện chưa có endpoint OAuth nên chưa đăng nhập thật được.
            </p>
          </div>
        </div>
      </section>

      <section class="p-6 sm:p-8 lg:p-10">
        <div class="mx-auto max-w-xl">
          <div class="mb-6 flex flex-wrap gap-2 rounded-2xl bg-slate-100 p-2">
            <button
              class="flex-1 rounded-2xl px-4 py-3 text-sm font-semibold transition"
              :class="activeTab === 'login' ? 'bg-white text-slate-950 shadow-sm' : 'text-slate-500'"
              @click="activeTab = 'login'; resetMessages()"
            >
              Đăng nhập
            </button>

            <button
              class="flex-1 rounded-2xl px-4 py-3 text-sm font-semibold transition"
              :class="activeTab === 'register' ? 'bg-white text-slate-950 shadow-sm' : 'text-slate-500'"
              @click="activeTab = 'register'; resetMessages()"
            >
              Đăng ký
            </button>
          </div>

          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">Auth Center</p>
          <h2 class="mt-3 text-3xl font-semibold text-slate-950">{{ tabTitle }}</h2>
          <p class="mt-3 text-sm leading-6 text-slate-500">
            {{
              activeTab === 'register'
                ? 'Khách hàng mới có thể tạo tài khoản ngay tại đây.'
                : 'Sử dụng email và mật khẩu để đăng nhập vào hệ thống.'
            }}
          </p>

          <button
            class="mt-6 flex w-full items-center justify-center gap-3 rounded-2xl border border-slate-200 px-4 py-3 font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
            :disabled="loading || googleLoading"
            @click="handleGoogleLogin"
          >
            <span class="inline-flex h-6 w-6 items-center justify-center rounded-full bg-red-50 text-sm font-bold text-red-500">G</span>
            {{ googleLoading ? "Đang kiểm tra Google..." : "Đăng nhập bằng Google" }}
          </button>

          <div class="my-6 flex items-center gap-3 text-xs uppercase tracking-[0.3em] text-slate-400">
            <span class="h-px flex-1 bg-slate-200"></span>
            <span>hoặc dùng tài khoản hệ thống</span>
            <span class="h-px flex-1 bg-slate-200"></span>
          </div>

          <form v-if="activeTab === 'login'" class="space-y-4" @submit.prevent="handleLogin">
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

            <button
              type="submit"
              :disabled="loading"
              class="w-full rounded-2xl bg-slate-950 px-4 py-3 font-semibold text-white transition hover:bg-slate-800 disabled:opacity-70"
            >
              {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
            </button>
          </form>

          <form v-else class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleCustomerRegister">
            <div class="sm:col-span-2">
              <label class="mb-2 block text-sm font-medium text-slate-700">Họ tên</label>
              <input
                v-model="customerRegisterForm.ten"
                type="text"
                placeholder="Nguyễn Văn A"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div class="sm:col-span-2">
              <label class="mb-2 block text-sm font-medium text-slate-700">Email</label>
              <input
                v-model="customerRegisterForm.email"
                type="email"
                placeholder="you@example.com"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Số điện thoại</label>
              <input
                v-model="customerRegisterForm.sdt"
                type="text"
                placeholder="0987xxxxxx"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Ngày sinh</label>
              <input
                v-model="customerRegisterForm.ngaySinh"
                type="date"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Mật khẩu</label>
              <input
                v-model="customerRegisterForm.password"
                type="password"
                placeholder="Tối thiểu 6 ký tự"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Nhập lại mật khẩu</label>
              <input
                v-model="customerRegisterForm.confirmPassword"
                type="password"
                placeholder="Nhập lại mật khẩu"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
            </div>

            <div class="sm:col-span-2">
              <button
                type="submit"
                :disabled="loading"
                class="w-full rounded-2xl bg-slate-950 px-4 py-3 font-semibold text-white transition hover:bg-slate-800 disabled:opacity-70"
              >
                {{ loading ? "Đang tạo tài khoản..." : "Đăng ký tài khoản" }}
              </button>
            </div>
          </form>

          <p v-if="error" class="mt-5 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
            {{ error }}
          </p>

          <p v-if="success" class="mt-5 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
            {{ success }}
          </p>
        </div>
      </section>
    </div>
  </div>
</template>