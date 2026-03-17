<script setup lang="ts">
import { onMounted, ref } from "vue"
import BannerGrid from "@/modules/storefront/components/BannerGrid.vue"
import HeroSection from "@/modules/storefront/components/HeroSection.vue"
import ProductGrid from "@/modules/storefront/components/ProductGrid.vue"
import { getStoreProducts } from "@/modules/storefront/services/storefront.api"
import type { StoreProduct } from "@/modules/storefront/types"

const products = ref<StoreProduct[]>([])
const loading = ref(true)

onMounted(async () => {
  products.value = await getStoreProducts()
  loading.value = false
})
</script>

<template>
  <HeroSection />
  <BannerGrid />
  <div v-if="loading" class="mx-auto max-w-7xl px-4 py-16 text-neutral-500">Đang tải sản phẩm...</div>
  <ProductGrid v-else :products="products" title="New Arrivals" subtitle="Shop the look" />
</template>
