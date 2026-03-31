<script setup lang="ts">
import { Pencil, Search, Trash2 } from "lucide-vue-next"
import type { ProductVariantResponse, VariantStockStatus } from "@/modules/product/types"

interface Option<T = string> {
  label: string
  value: T
}

const props = defineProps<{
  variants: ProductVariantResponse[]
  formatCurrency: (value?: number | null) => string
  statusLabel: (value?: string | null) => string
  filters: {
    keyword: string
    stockStatus: VariantStockStatus | ""
  }
  stockStatusOptions: Option<VariantStockStatus>[]
}>()

const emit = defineEmits<{
  (e: 'create'): void
  (e: 'edit', variant: ProductVariantResponse): void
  (e: 'remove', id: number): void
  (e: 'filter-change'): void
  (e: 'filter-reset'): void
}>()
</script>

<template>
  <div>
    <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
      <h4 class="font-semibold text-slate-900">Biến thể</h4>
      <button @click="emit('create')" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Thêm biến thể</button>
    </div>

    <div class="mb-3 grid gap-3 md:grid-cols-[1fr_220px_auto_auto]">
      <label class="relative">
        <Search class="absolute left-3 top-3 text-slate-400" :size="16" />
        <input v-model="props.filters.keyword" @input="emit('filter-change')" class="w-full rounded-xl border px-9 py-2.5 text-sm" placeholder="Tìm SKU / kích thước / màu..." />
      </label>
      <select v-model="props.filters.stockStatus" @change="emit('filter-change')" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Tất cả trạng thái tồn</option>
        <option v-for="item in props.stockStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <button @click="emit('filter-change')" class="rounded-xl border px-4 py-2 text-sm">Lọc</button>
      <button @click="emit('filter-reset')" class="rounded-xl border px-4 py-2 text-sm">Xóa lọc</button>
    </div>

    <div class="space-y-2">
      <div v-for="variant in props.variants" :key="variant.id" class="rounded-xl border p-3 text-sm">
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="font-medium">{{ variant.sku }}</div>
            <div class="text-slate-500">{{ variant.size }} • {{ variant.color }}</div>
            <div class="mt-1 text-slate-500">{{ props.formatCurrency(variant.salePrice || variant.price) }} • Tồn: {{ variant.stockQuantity }} • {{ props.statusLabel(variant.stockStatus) }}</div>
          </div>
          <div class="flex gap-2">
            <button @click="emit('edit', variant)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="14" /></button>
            <button @click="emit('remove', variant.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="14" /></button>
          </div>
        </div>
      </div>
      <div v-if="props.variants.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có biến thể.</div>
    </div>
  </div>
</template>
