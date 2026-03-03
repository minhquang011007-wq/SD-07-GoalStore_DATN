<template>
    <div style="width:300px;margin:100px auto;">
      <h2>Login</h2>
      <input v-model="username" placeholder="Username" />
      <input v-model="password" type="password" placeholder="Password" />
      <button @click="handleLogin">Login</button>
      <p style="color:red">{{ error }}</p>
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
  
  async function handleLogin() {
    try {
      const res = await api.post("/api/auth/login", {
        username: username.value,
        password: password.value,
      });
  
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("role", res.data.role);
  
      if (res.data.role === "ADMIN") router.push("/dashboard");
      else if (res.data.role === "SALES") router.push("/dashboard");
      else router.push("/");
  
    } catch (err) {
      error.value = "Sai tài khoản hoặc mật khẩu";
    }
  }
  </script>