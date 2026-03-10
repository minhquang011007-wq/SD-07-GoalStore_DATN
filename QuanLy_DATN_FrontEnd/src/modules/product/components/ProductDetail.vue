<script setup lang="ts">
import { AlertCircle } from "lucide-vue-next"
import ImageManager from "@/modules/product/components/ImageManager.vue"
import VariantManager from "@/modules/product/components/VariantManager.vue"
import type { ProductDetailResponse, ProductVariantRequest, ProductVariantResponse, VariantStockStatus } from "@/modules/product/types"

defineProps<{
  loading: boolean
  selectedProduct: ProductDetailResponse | null
  uploadingProductImages: boolean
  variantForm: ProductVariantRequest
  editingVariantId: number | null
  normalizeAsset: (url?: string | null) => string
  formatCurrency: (value?: number | null) => string
  displayStatusLabel: (value?: string | null) => string
  stockStatusLabel: (value?: string | null) => string
  stockStatusOptions: Array<{ label: string; value: VariantStockStatus }>
}>()

const emit = defineEmits<{
  (e: "edit-product", id: number): void
  (e: "hide-oos", id: number): void
  (e: "delete-product", id: number): void
  (e: "upload-images", event: Event): void
  (e: "set-avatar", id: number): void
  (e: "delete-image", id: number): void
  (e: "edit-variant", variant: ProductVariantResponse): void
  (e: "delete-variant", id: number): void
  (e: "submit-variant"): void
  (e: "reset-variant"): void
}>()
</script>

<template>
  <div class="rounded-2xl border bg-white p-5 shadow-sm">
    <div v-if="loading" class="p-10 text-center text-sm text-slate-500">Đang tải chi tiết...</div>
    <div v-else-if="selectedProduct" class="space-y-5">
      <div class="flex gap-3">
        <img v-if="selectedProduct.thumbnailUrl" :src="normalizeAsset(selectedProduct.thumbnailUrl)" class="h-20 w-20 rounded-2xl border object-cover" />
        <div>
          <h3 class="text-lg font-semibold text-slate-900">{{ selectedProduct.name }}</h3>
          <div class="text-sm text-slate-500">{{ selectedProduct.baseSku }}</div>
          <div class="mt-1 text-sm text-slate-500">{{ selectedProduct.brand || 'Chưa có brand' }} · {{ selectedProduct.season || 'Chưa có season' }}</div>
          <div class="mt-1 text-sm text-slate-500">{{ selectedProduct.material || 'Chưa có chất liệu' }}</div>
          <div class="mt-2 flex flex-wrap gap-2 text-xs">
            <span class="rounded-full bg-blue-50 px-2 py-1 text-blue-700">{{ displayStatusLabel(selectedProduct.displayStatus) }}</span>
            <span class="rounded-full bg-slate-100 px-2 py-1 text-slate-700">{{ selectedProduct.productType }}</span>
            <span class="rounded-full bg-slate-100 px-2 py-1 text-slate-700">{{ selectedProduct.targetGender }}</span>
          </div>
        </div>
      </div>
      <div>
        <div class="text-sm font-medium text-slate-700">Mô tả</div>
        <p class="mt-1 text-sm leading-6 text-slate-600">{{ selectedProduct.description || 'Chưa có mô tả' }}</p>
      </div>
      <div>
        <div class="text-sm font-medium text-slate-700">Category / Tag</div>
        <div class="mt-2 flex flex-wrap gap-2">
          <span v-for="category in selectedProduct.categories" :key="category.id" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ category.name }}</span>
          <span v-for="tag in selectedProduct.tags" :key="tag.id" class="rounded-full bg-amber-50 px-2 py-1 text-xs text-amber-700">#{{ tag.name }}</span>
        </div>
      </div>
      <ImageManager :items="selectedProduct.images" :uploading="uploadingProductImages" :normalize-asset="normalizeAsset" @upload="emit('upload-images', $event)" @avatar="emit('set-avatar', $event)" @delete="emit('delete-image', $event)" />
      <VariantManager :items="selectedProduct.variants" :form="variantForm" :editing-variant-id="editingVariantId" :stock-status-options="stockStatusOptions" :format-currency="formatCurrency" :stock-status-label="stockStatusLabel" @edit="emit('edit-variant', $event)" @delete="emit('delete-variant', $event)" @submit="emit('submit-variant')" @reset="emit('reset-variant')" />
      <div class="flex flex-wrap gap-2">
        <button class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50" @click="emit('edit-product', selectedProduct.id)">Sửa sản phẩm</button>
        <button class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50" @click="emit('hide-oos', selectedProduct.id)">Hide if OOS</button>
        <button class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50" @click="emit('delete-product', selectedProduct.id)">Xóa mềm</button>
      </div>
    </div>
    <div v-else class="flex min-h-[420px] flex-col items-center justify-center gap-3 p-6 text-center text-slate-500"><AlertCircle class="h-8 w-8" /><p>Chưa chọn sản phẩm nào để xem chi tiết</p></div>
  </div>
</template>
