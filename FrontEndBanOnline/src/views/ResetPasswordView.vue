<script setup lang="ts">
import axios from "axios"
import { ref } from "vue"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const token = route.query.token

const newPassword = ref("")
const confirmPassword = ref("")

const loading = ref(false)
const error = ref("")
const success = ref("")

const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

async function resetPassword() {

  error.value = ""
  success.value = ""

  if (
    !newPassword.value.trim() ||
    !confirmPassword.value.trim()
  ) {

    error.value =
      "Vui lòng nhập đầy đủ thông tin"

    return
  }

  if (
    newPassword.value !==
    confirmPassword.value
  ) {

    error.value =
      "Mật khẩu nhập lại không khớp"

    return
  }

  loading.value = true

  try {

    await axios.post(
      "http://localhost:8080/api/customer-auth/reset-password",
      {
        token,
        newPassword: newPassword.value
      }
    )

    success.value =
      "Đổi mật khẩu thành công"

    setTimeout(() => {
      router.push("/login")
    }, 1200)

  } catch (e: any) {

    error.value =
      e?.response?.data ||
      "Có lỗi xảy ra"

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

          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">
            GOALSTORE
          </p>

          <h2 class="mt-3 text-3xl font-semibold text-slate-950">
            Đặt lại mật khẩu
          </h2>

          <p class="mt-3 text-sm leading-6 text-slate-500">
            Nhập mật khẩu mới cho tài khoản của bạn
          </p>

          <div class="mt-8 space-y-4">

            <div class="relative">
                <input
                v-model="newPassword"
                :type="showNewPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 pr-12 outline-none transition focus:border-sky-500"
                />
                <button
                type="button"
                @click="showNewPassword = !showNewPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500"
                >
                {{ showNewPassword ? "Ẩn" : "Hiện" }}
                </button>
            </div>

            <div class="relative">
                <input
                v-model="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 pr-12 outline-none transition focus:border-sky-500"
                />
                <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500"
                >
                {{ showConfirmPassword ? "Ẩn" : "Hiện" }}
                </button>
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
              @click="resetPassword"
              class="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="loading"
            >
              {{
                loading
                  ? "Đang xử lý..."
                  : "Đổi mật khẩu"
              }}
            </button>

          </div>

        </div>

      </section>

    </div>

  </div>
</template>