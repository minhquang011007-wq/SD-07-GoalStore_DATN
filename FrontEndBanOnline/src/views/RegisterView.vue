<script setup lang="ts">
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { saveSession, type AuthSession } from "@/shared/lib/auth"
import { registerCustomer } from "@/shared/lib/auth.api"

const router = useRouter()

const loading = ref(false)
const error = ref("")
const success = ref("")
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const registerForm = reactive({
  ten: "",
  email: "",
  sdt: "",
  password: "",
  confirmPassword: "",
  ngaySinh: "",
})

function resetMessages() {
  error.value = ""
  success.value = ""
}

async function handleRegister() {
  resetMessages()

  if (!registerForm.ten.trim()) {
    error.value = "Vui lòng nhập họ tên"
    return
  }

  if (!registerForm.email.trim()) {
    error.value = "Vui lòng nhập email"
    return
  }

  if (!registerForm.sdt.trim()) {
    error.value = "Vui lòng nhập số điện thoại"
    return
  }

  if (!registerForm.password.trim()) {
    error.value = "Vui lòng nhập mật khẩu"
    return
  }

  if (registerForm.password.length < 6) {
    error.value = "Mật khẩu phải có ít nhất 6 ký tự"
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    error.value = "Mật khẩu xác nhận không khớp"
    return
  }

  loading.value = true

  try {
    await registerCustomer({
      ten: registerForm.ten.trim(),
      email: registerForm.email.trim(),
      sdt: registerForm.sdt.trim(),
      password: registerForm.password,
      ngaySinh: registerForm.ngaySinh || undefined,
    })

    success.value = "Đăng ký thành công, đang chuyển sang trang đăng nhập..."
    setTimeout(() => {
      router.push("/login")
    }, 700)
  } catch (e: any) {
    error.value = e?.response?.data?.message || "Đăng ký thất bại"
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-[linear-gradient(135deg,#f8fafc_0%,#eff6ff_45%,#ffffff_100%)] px-4 py-10">
    <div
      class="mx-auto grid max-w-6xl overflow-hidden rounded-[32px] bg-white shadow-2xl shadow-slate-200/70 lg:grid-cols-[1.05fr_0.95fr]"
    >
      <section class="hidden bg-slate-950 p-10 text-white lg:flex lg:flex-col lg:justify-between">
        <div>
          <p class="text-sm uppercase tracking-[0.35em] text-slate-300">GoalStore</p>
          <h1 class="mt-6 max-w-md text-5xl font-semibold leading-tight">
            Tạo tài khoản khách hàng.
          </h1>
          <p class="mt-6 max-w-lg text-base leading-8 text-slate-300">
            Đăng ký nhanh để mua sắm, theo dõi đơn hàng và trải nghiệm đầy đủ hệ thống bán hàng online của GoalStore.
          </p>
        </div>

        <div class="grid gap-4 rounded-3xl border border-white/10 bg-white/5 p-6 text-sm text-slate-200">
          <div>
            <p class="font-semibold text-white">Đăng ký nhanh</p>
            <p class="mt-1 text-slate-300">
              Chỉ cần họ tên, email, số điện thoại và mật khẩu để bắt đầu.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Dành cho khách hàng</p>
            <p class="mt-1 text-slate-300">
              Trang này dùng để tạo tài khoản CUSTOMER cho frontend bán hàng online.
            </p>
          </div>
          <div>
            <p class="font-semibold text-white">Tự đăng nhập sau khi tạo</p>
            <p class="mt-1 text-slate-300">
              Sau khi đăng ký thành công, hệ thống sẽ tự lưu phiên đăng nhập và chuyển bạn về trang chủ.
            </p>
          </div>
        </div>
      </section>

      <section class="p-6 sm:p-8 lg:p-10">
        <div class="mx-auto max-w-xl">
          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">Customer Register</p>
          <h2 class="mt-3 text-3xl font-semibold text-slate-950">Đăng ký GoalStore</h2>
          <p class="mt-3 text-sm leading-6 text-slate-500">
            Tạo tài khoản khách hàng để mua sắm và theo dõi đơn hàng trên GoalStore.
          </p>

          <form class="mt-8 space-y-4" @submit.prevent="handleRegister">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Họ và tên</label>
              <input
                v-model="registerForm.ten"
                type="text"
                placeholder="Nguyễn Văn A"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Email</label>
              <input
                v-model="registerForm.email"
                type="email"
                placeholder="you@example.com"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Số điện thoại</label>
              <input
                v-model="registerForm.sdt"
                type="text"
                placeholder="09xxxxxxxx"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Ngày sinh</label>
              <input
                v-model="registerForm.ngaySinh"
                type="date"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
              />
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Mật khẩu</label>
              <div class="relative">
                <input
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  class="w-full rounded-2xl border border-slate-200 px-4 py-3 pr-16 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
                />
                <button
                  type="button"
                  class="absolute right-4 top-1/2 -translate-y-1/2 text-sm font-medium text-slate-500 hover:text-slate-700"
                  @click="showPassword = !showPassword"
                >
                  {{ showPassword ? "Ẩn" : "Hiện" }}
                </button>
              </div>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">Xác nhận mật khẩu</label>
              <div class="relative">
                <input
                  v-model="registerForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  class="w-full rounded-2xl border border-slate-200 px-4 py-3 pr-16 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100"
                />
                <button
                  type="button"
                  class="absolute right-4 top-1/2 -translate-y-1/2 text-sm font-medium text-slate-500 hover:text-slate-700"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  {{ showConfirmPassword ? "Ẩn" : "Hiện" }}
                </button>
              </div>
            </div>

            <div
              v-if="error"
              class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600"
            >
              {{ error }}
            </div>

            <div
              v-if="success"
              class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-600"
            >
              {{ success }}
            </div>

            <button
              type="submit"
              class="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="loading"
            >
              {{ loading ? "Đang đăng ký..." : "Đăng ký" }}
            </button>

            <p class="text-center text-sm text-slate-500">
              Đã có tài khoản?
              <RouterLink to="/login" class="font-medium text-sky-600 hover:text-sky-700">
                Đăng nhập ngay
              </RouterLink>
            </p>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>