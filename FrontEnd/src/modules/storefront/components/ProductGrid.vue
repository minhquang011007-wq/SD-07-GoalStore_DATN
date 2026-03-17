<script setup lang="ts">
import type { StoreProduct } from "@/modules/storefront/types"
import { computed } from "vue"

const props = defineProps<{
  products: StoreProduct[]
  title?: string
  subtitle?: string
}>()

function formatPrice(value: number) {
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value)
}

const visibleProducts = computed(() => props.products.slice(0, 8))
</script>

<template>
  <section class="bg-white py-20">
    <div class="mx-auto max-w-7xl px-4">
      <div class="mb-10 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <p class="text-sm font-semibold uppercase tracking-[0.3em] text-[#e53637]">{{ subtitle || 'Best seller' }}</p>
          <h2 class="mt-3 text-4xl font-semibold text-black">{{ title || 'Featured Products' }}</h2>
        </div>
        <router-link to="/shop" class="text-sm font-semibold uppercase tracking-[0.18em] text-black underline-offset-4 hover:underline">View all</router-link>
      </div>

      <div class="grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
        <article v-for="product in visibleProducts" :key="product.id" class="group">
          <div class="relative overflow-hidden rounded-[26px] bg-[#f8f5f2]">
            <img :src="product.image" :alt="product.name" class="h-80 w-full object-cover transition duration-500 group-hover:scale-105" />
            <span v-if="product.isNew" class="absolute left-4 top-4 rounded-full bg-black px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] text-white">New</span>
          </div>
          <div class="pt-5">
            <p class="text-sm uppercase tracking-[0.22em] text-neutral-400">{{ product.category }}</p>
            <h3 class="mt-2 text-xl font-semibold text-black">{{ product.name }}</h3>
            <p class="mt-1 text-sm text-neutral-500">{{ product.brand }}</p>
            <div class="mt-4 flex items-center gap-3">
              <span class="text-lg font-semibold text-black">{{ formatPrice(product.salePrice || product.price) }}</span>
              <span v-if="product.salePrice" class="text-sm text-neutral-400 line-through">{{ formatPrice(product.price) }}</span>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>
