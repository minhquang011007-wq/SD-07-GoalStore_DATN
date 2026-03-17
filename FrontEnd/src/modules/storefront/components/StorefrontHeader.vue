<script setup lang="ts">
import { Heart, Search, ShoppingBag, User } from "lucide-vue-next"
import { computed } from "vue"
import { RouterLink, useRoute } from "vue-router"

const route = useRoute()
const cartCount = computed(() => 0)
const totalText = computed(() => "$0.00")

const menus = [
  { label: "Home", to: "/" },
  { label: "Shop", to: "/shop" },
  { label: "Pages", to: "/shop" },
  { label: "Blog", to: "/shop" },
  { label: "Contacts", to: "/shop" },
]
</script>

<template>
  <header class="border-b border-neutral-200 bg-white">
    <div class="bg-black text-white">
      <div class="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 text-xs md:text-sm">
        <p>Free shipping, 30-day return or refund guarantee.</p>
        <div class="hidden items-center gap-6 md:flex">
          <RouterLink to="/login" class="uppercase tracking-[0.2em] hover:text-neutral-300">Sign in</RouterLink>
          <a href="#" class="uppercase tracking-[0.2em] hover:text-neutral-300">FAQs</a>
          <button class="uppercase tracking-[0.2em] hover:text-neutral-300">USD</button>
        </div>
      </div>
    </div>

    <div class="mx-auto flex max-w-7xl items-center justify-between gap-6 px-4 py-6">
      <RouterLink to="/" class="flex items-center gap-3">
        <img src="/storefront/logo.png" alt="GoalStore" class="h-7 w-auto" />
      </RouterLink>

      <nav class="hidden items-center gap-10 text-lg md:flex">
        <RouterLink
          v-for="menu in menus"
          :key="menu.to + menu.label"
          :to="menu.to"
          class="relative pb-1 text-black transition hover:text-red-500"
          :class="route.path === menu.to ? 'after:absolute after:bottom-0 after:left-0 after:h-[2px] after:w-full after:bg-red-500' : ''"
        >
          {{ menu.label }}
        </RouterLink>
      </nav>

      <div class="flex items-center gap-4 md:gap-5">
        <button class="text-neutral-800 transition hover:text-red-500"><Search class="h-5 w-5" /></button>
        <button class="text-neutral-800 transition hover:text-red-500"><Heart class="h-5 w-5" /></button>
        <RouterLink to="/login" class="text-neutral-800 transition hover:text-red-500"><User class="h-5 w-5" /></RouterLink>
        <button class="flex items-center gap-2 text-neutral-900 transition hover:text-red-500">
          <ShoppingBag class="h-5 w-5" />
          <span class="text-sm">{{ totalText }}</span>
          <span class="rounded-full bg-black px-1.5 py-0.5 text-[10px] text-white">{{ cartCount }}</span>
        </button>
      </div>
    </div>
  </header>
</template>
