<template>
  <div style="max-width:360px;margin:80px auto;">
    <h2>Đăng nhập</h2>

    <input
      v-model="username"
      placeholder="Username"
      style="width:100%;padding:10px;margin:8px 0;"
      @keyup.enter="handleLogin"
    />

    <input
      v-model="password"
      type="password"
      placeholder="Password"
      style="width:100%;padding:10px;margin:8px 0;"
      @keyup.enter="handleLogin"
    />

    <button
      @click="handleLogin"
      :disabled="loading"
      style="width:100%;padding:10px;"
    >
      {{ loading ? "Đang đăng nhập..." : "Login" }}
    </button>

    <p v-if="error" style="color:red;margin-top:10px;">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/lib/api";

const router = useRouter();

const username = ref("");
const password = ref("");
const error = ref("");
const loading = ref(false);

async function handleLogin() {
  error.value = "";

  if (!username.value || !password.value) {
    error.value = "Vui lòng nhập username và password";
    return;
  }

  loading.value = true;
  try {
    const res = await api.post("/api/auth/login", {
      username: username.value,
      password: password.value,
    });

    // BE trả: { token, username, role }
    localStorage.setItem("token", res.data.token);
    localStorage.setItem("role", res.data.role);
    localStorage.setItem("username", res.data.username);

    // ✅ Redirect theo role
    if (res.data.role === "ADMIN") {
      router.push("/admin");
    } else if (res.data.role === "SALES") {
      router.push("/sales");
    } else {
      // fallback nếu role lạ
      router.push("/login");
    }
  } catch (e: any) {
    // nếu BE có trả message thì lấy, không thì dùng default
    error.value =
      e?.response?.data?.message || "Sai tài khoản hoặc mật khẩu";
  } finally {
    loading.value = false;
  }
}
</script>