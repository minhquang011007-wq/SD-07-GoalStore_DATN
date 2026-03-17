<script setup lang="ts">
import { RouterView } from "vue-router"
import StorefrontHeader from "@/modules/storefront/components/StorefrontHeader.vue"
</script>

<template>
  <div class="min-h-screen bg-white text-black">
    <StorefrontHeader />
    <RouterView />
    <footer class="border-t border-neutral-200 bg-white">
      <div class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-10 text-sm text-neutral-500 md:flex-row md:items-center md:justify-between">
        <p>© 2026 GoalStore. Giao diện storefront tích hợp trong QuanLy_DATN_FrontEnd.</p>
        <div class="flex gap-6 uppercase tracking-[0.2em] text-neutral-400">
          <span>Home</span>
          <span>Shop</span>
          <span>Contact</span>
        </div>
      </div>
    </footer>
  </div>
</template>
