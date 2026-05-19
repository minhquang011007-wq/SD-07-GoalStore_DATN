<script setup lang="ts">
import axios from "axios"
import { ref } from "vue"

const email = ref("")
const loading = ref(false)
const error = ref("")
const success = ref("")

async function sendResetLink() {

  error.value = ""
  success.value = ""

  if (!email.value.trim()) {
    error.value = "Vui lòng nhập email"
    return
  }

  loading.value = true

  try {

    await axios.post(
      "http://localhost:8080/api/customer-auth/forgot-password",
      {
        email: email.value
      }
    )

    success.value =
      "Đã gửi email hướng dẫn đặt lại mật khẩu"

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

      <section class="p-6 sm:p-8 lg:p-10">

        <div class="mx-auto max-w-xl">

          <p class="text-sm font-semibold uppercase tracking-[0.25em] text-sky-600">
            GOALSTORE
          </p>

          <h2 class="mt-3 text-3xl font-semibold text-slate-950">
            Quên mật khẩu
          </h2>

          <p class="mt-3 text-sm leading-6 text-slate-500">
            Nhập email đã đăng ký để nhận hướng dẫn đặt lại mật khẩu
          </p>

          <div class="mt-8 space-y-4">

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">
                Email
              </label>

              <input
                v-model="email"
                type="email"
                placeholder="you@example.com"
                class="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-sky-500"
              />
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
              @click="sendResetLink"
              class="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="loading"
            >
              {{
                loading
                  ? "Đang gửi..."
                  : "Gửi hướng dẫn"
              }}
            </button>

            <p class="text-center text-sm text-slate-500">
              Đã nhớ mật khẩu?
              <RouterLink
                to="/login"
                class="font-medium text-sky-600 hover:text-sky-700"
              >
                Đăng nhập
              </RouterLink>
            </p>

          </div>

        </div>

      </section>

      <section class="relative hidden min-h-[760px] overflow-hidden bg-slate-950 lg:block">
        <img
          src="/legacy/img/anhlogin.jpg"
          alt="GoalStore banner"
          class="absolute inset-0 h-full w-full object-cover"
        />
      </section>

    </div>

  </div>
</template>