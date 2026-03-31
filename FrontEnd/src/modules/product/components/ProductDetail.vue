<script setup lang="ts">
import { FolderTree, History, LoaderCircle } from "lucide-vue-next"
import VariantManager from "@/modules/product/components/VariantManager.vue"
import ImageManager from "@/modules/product/components/ImageManager.vue"
import AttributeManager from "@/modules/product/components/AttributeManager.vue"
import { resolveProductImageUrl } from "@/modules/product/utils/image"
import type {
  ProductAttributeResponse,
  ProductDetailResponse,
  ProductHistoryResponse,
  ProductImageResponse,
  ProductVariantResponse,
  VariantStockStatus,
} from "@/modules/product/types"

interface Option<T = string> {
  label: string
  value: T
}

const props = defineProps<{
  selectedProduct: ProductDetailResponse | null
  loadingDetail: boolean
  uploadingProductImages: boolean
  detailStats: { imageCount: number; variantCount: number; totalStock: number }
  historyItems: ProductHistoryResponse[]
  formatCurrency: (value?: number | null) => string
  formatDate: (value?: string | null) => string
  statusLabel: (value?: string | null) => string
  variantFilters: {
    keyword: string
    stockStatus: VariantStockStatus | ""
  }
  stockStatusOptions: Option<VariantStockStatus>[]
}>()

const emit = defineEmits<{
  (e: "edit-product", id: number): void
  (e: "quick-edit", id: number): void
  (e: "hide-oos", id: number): void
  (e: "delete-soft", id: number): void
  (e: "create-variant"): void
  (e: "edit-variant", variant: ProductVariantResponse): void
  (e: "remove-variant", id: number): void
  (e: "variant-filter-change"): void
  (e: "variant-filter-reset"): void
  (e: "upload-images", event: Event): void
  (e: "set-avatar", image: ProductImageResponse): void
  (e: "remove-image", id: number): void
  (e: "image-order-change", payload: { image: ProductImageResponse; event: Event }): void
  (e: "create-attribute"): void
  (e: "edit-attribute", attribute: ProductAttributeResponse): void
  (e: "remove-attribute", id: number): void
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
        <img v-if="props.selectedProduct.thumbnailUrl" :src="resolveProductImageUrl(props.selectedProduct.thumbnailUrl)" class="h-20 w-20 rounded-2xl border object-cover" />
        <div v-else class="flex h-20 w-20 items-center justify-center rounded-2xl border bg-slate-50 text-xs text-slate-400">Chưa có ảnh</div>
        <div>
          <h3 class="text-lg font-semibold text-slate-900">{{ props.selectedProduct.name }}</h3>
          <p class="text-sm text-slate-500">{{ props.selectedProduct.baseSku }}</p>
          <p class="mt-1 text-sm text-slate-500">{{ props.selectedProduct.brand || '-' }} • {{ props.statusLabel(props.selectedProduct.displayStatus) }}</p>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-3 text-center text-sm">
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Ảnh</div><div class="mt-1 font-semibold">{{ props.detailStats.imageCount }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Biến thể</div><div class="mt-1 font-semibold">{{ props.detailStats.variantCount }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Tổng tồn</div><div class="mt-1 font-semibold">{{ props.detailStats.totalStock }}</div></div>
      </div>

      <div>
        <h4 class="mb-2 font-semibold text-slate-900">Danh mục và thẻ</h4>
        <div class="flex flex-wrap gap-2">
          <span v-for="cat in props.selectedProduct.categories" :key="cat.id" class="inline-flex whitespace-nowrap rounded-full bg-slate-100 px-3 py-1 text-xs">{{ cat.name }}</span>
          <span v-for="tag in props.selectedProduct.tags" :key="tag.id" class="inline-flex whitespace-nowrap rounded-full bg-amber-100 px-3 py-1 text-xs text-amber-800">#{{ tag.name }}</span>
        </div>
      </div>

      <AttributeManager
        :attributes="props.selectedProduct.attributes || []"
        @create="emit('create-attribute')"
        @edit="emit('edit-attribute', $event)"
        @remove="emit('remove-attribute', $event)"
      />

      <VariantManager
        :variants="props.selectedProduct.variants || []"
        :format-currency="props.formatCurrency"
        :status-label="props.statusLabel"
        :filters="props.variantFilters"
        :stock-status-options="props.stockStatusOptions"
        @create="emit('create-variant')"
        @edit="emit('edit-variant', $event)"
        @remove="emit('remove-variant', $event)"
        @filter-change="emit('variant-filter-change')"
        @filter-reset="emit('variant-filter-reset')"
      />

      <ImageManager
        :images="props.selectedProduct.images || []"
        :uploading="props.uploadingProductImages"
        @upload="emit('upload-images', $event)"
        @set-avatar="emit('set-avatar', $event)"
        @remove="emit('remove-image', $event)"
        @image-order-change="emit('image-order-change', $event)"
      />

      <div>
        <div class="mb-2 flex items-center gap-2 font-semibold text-slate-900"><History :size="16" /> Lịch sử thay đổi</div>
        <div class="space-y-2">
          <div v-for="history in props.historyItems" :key="history.id" class="rounded-xl border p-3 text-sm">
            <div class="font-medium">{{ history.action }}</div>
            <div class="text-slate-500">{{ history.note || 'Không có ghi chú' }}</div>
            <div class="mt-1 text-xs text-slate-400">{{ props.formatDate(history.changedAt) }} • người dùng {{ history.changedBy || '-' }}</div>
          </div>
          <div v-if="props.historyItems.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có lịch sử thay đổi.</div>
        </div>
      </div>

      <div class="flex flex-wrap gap-2">
        <button @click="emit('edit-product', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa sản phẩm</button>
        <button @click="emit('quick-edit', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Cập nhật nhanh</button>
        <button @click="emit('hide-oos', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Ẩn nếu hết hàng</button>
        <button @click="emit('delete-soft', props.selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa mềm</button>
      </div>
    </div>

    <div v-else class="py-10 text-center text-sm text-slate-500">Chọn một sản phẩm ở bên trái để xem chi tiết.</div>
  </div>
</template>
