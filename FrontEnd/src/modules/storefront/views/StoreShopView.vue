<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import ProductGrid from "@/modules/storefront/components/ProductGrid.vue"
import { getStoreProducts } from "@/modules/storefront/services/storefront.api"
import type { StoreProduct } from "@/modules/storefront/types"

const products = ref<StoreProduct[]>([])
const keyword = ref("")

const filteredProducts = computed(() => {
  const q = keyword.value.trim().toLowerCase()
  if (!q) return products.value
  return products.value.filter((item) => item.name.toLowerCase().includes(q) || item.category.toLowerCase().includes(q))
})

onMounted(async () => {
  products.value = await getStoreProducts()
})
</script>

<template>
  <section class="bg-[#f8f5f2] py-16">
    <div class="mx-auto max-w-7xl px-4">
      <p class="mb-3 text-sm font-semibold uppercase tracking-[0.3em] text-[#e53637]">Shop</p>
      <div class="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 class="text-5xl font-semibold text-black">Discover the collection</h1>
          <p class="mt-4 max-w-2xl text-lg text-neutral-600">Tách phần bán hàng online vào cùng project Vue để nhóm m làm chung, nhưng vẫn giữ được admin riêng.</p>
        </div>
        <input v-model="keyword" type="text" placeholder="Tìm áo, giày, phụ kiện..." class="w-full rounded-full border border-neutral-300 bg-white px-5 py-3 text-sm outline-none ring-0 md:w-80" />
      </div>
    </div>
  </section>

  <ProductGrid :products="filteredProducts" title="Shop Products" subtitle="Curated items" />
</template>
