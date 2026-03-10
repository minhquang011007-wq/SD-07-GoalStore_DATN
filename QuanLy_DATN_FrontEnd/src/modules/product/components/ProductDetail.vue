<script setup lang="ts">
import { FolderTree, LoaderCircle } from "lucide-vue-next"
import VariantManager from "@/modules/product/components/VariantManager.vue"
import ImageManager from "@/modules/product/components/ImageManager.vue"
import type { ProductDetailResponse, ProductImageResponse, ProductVariantResponse } from "@/modules/product/types"

const props = defineProps<{
  selectedProduct: ProductDetailResponse | null
  loadingDetail: boolean
  uploadingProductImages: boolean
  detailStats: { imageCount: number; variantCount: number; totalStock: number }
  formatCurrency: (value?: number | null) => string
  statusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: 'edit-product', id: number): void
  (e: 'hide-oos', id: number): void
  (e: 'delete-soft', id: number): void
  (e: 'create-variant'): void
  (e: 'edit-variant', variant: ProductVariantResponse): void
  (e: 'remove-variant', id: number): void
  (e: 'upload-images', event: Event): void
  (e: 'set-avatar', image: ProductImageResponse): void
  (e: 'remove-image', id: number): void
  (e: 'image-order-change', payload: { image: ProductImageResponse; event: Event }): void
}>()
</script>

<template>
  <div class="rounded-2xl border bg-white p-4 shadow-sm">
    <div class="flex items-center gap-2 border-b pb-3 text-lg font-semibold text-slate-900">
      <FolderTree :size="18" /> Chi tiết sản phẩm
    </div>

    <div v-if="props.loadingDetail" class="flex items-center justify-center gap-2 py-10 text-sm text-slate-500">
      <LoaderCircle class="animate-spin" :size="18" /> Đang tải chi tiết...
    </div>

    <div v-else-if="props.selectedProduct" class="space-y-5 pt-4">
      <div class="flex gap-3">
        <img v-if="props.selectedProduct.thumbnailUrl" :src="props.selectedProduct.thumbnailUrl" class="h-20 w-20 rounded-2xl border object-cover" />
        <div>
          <h3 class="text-lg font-semibold text-slate-900">{{ props.selectedProduct.name }}</h3>
          <p class="text-sm text-slate-500">{{ props.selectedProduct.baseSku }}</p>
          <p class="mt-1 text-sm text-slate-500">{{ props.selectedProduct.brand || '-' }} • {{ props.statusLabel(props.selectedProduct.displayStatus) }}</p>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-3 text-center text-sm">
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Ảnh</div><div class="mt-1 font-semibold">{{ props.detailStats.imageCount }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Variants</div><div class="mt-1 font-semibold">{{ props.detailStats.variantCount }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Tổng tồn</div><div class="mt-1 font-semibold">{{ props.detailStats.totalStock }}</div></div>
      </div>

      <div>
        <div class="mb-2 flex items-center justify-between">
          <h4 class="font-semibold text-slate-900">Category & Tags</h4>
        </div>
        <div class="flex flex-wrap gap-2">
          <span v-for="cat in props.selectedProduct.categories" :key="cat.id" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ cat.name }}</span>
          <span v-for="tag in props.selectedProduct.tags" :key="tag.id" class="rounded-full bg-amber-100 px-2 py-1 text-xs text-amber-800">#{{ tag.name }}</span>
        </div>
      </div>

      <VariantManager
        :variants="props.selectedProduct.variants"
        :format-currency="props.formatCurrency"
        :status-label="props.statusLabel"
        @create="emit('create-variant')"
        @edit="emit('edit-variant', $event)"
        @remove="emit('remove-variant', $event)"
      />

      <ImageManager
        :images="props.selectedProduct.images"
        :uploading="props.uploadingProductImages"
        @upload="emit('upload-images', $event)"
        @set-avatar="emit('set-avatar', $event)"
        @remove="emit('remove-image', $event)"
        @image-order-change="emit('image-order-change', $event)"
      />

      <div class="flex flex-wrap gap-2">
        <button @click="emit('edit-product', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa sản phẩm</button>
        <button @click="emit('hide-oos', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Hide if OOS</button>
        <button @click="emit('delete-soft', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa mềm</button>
      </div>
    </div>

    <div v-else class="py-10 text-center text-sm text-slate-500">Chọn một sản phẩm ở bên trái để xem chi tiết.</div>
  </div>
</template>
