<script setup lang="ts">
import { Eye, LoaderCircle, Pencil, Trash2 } from "lucide-vue-next"
import type { ProductSummaryResponse } from "@/modules/product/types"
import { resolveProductImageUrl } from "@/modules/product/utils/image"

const props = defineProps<{
  items: ProductSummaryResponse[]
  loading: boolean
  selectedProductIds: number[]
  page: number
  totalPages: number
  totalElements: number
  formatCurrency: (value?: number | null) => string
  statusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: 'toggle-all'): void
  (e: 'toggle-select', id: number): void
  (e: 'view', id: number): void
  (e: 'edit', id: number): void
  (e: 'hide-oos', id: number): void
  (e: 'delete', id: number): void
  (e: 'prev-page'): void
  (e: 'next-page'): void
}>()
</script>

<template>
  <div>
    <div class="mt-4 overflow-hidden rounded-2xl border">
      <div v-if="props.loading" class="flex items-center justify-center gap-2 p-10 text-sm text-slate-500">
        <LoaderCircle class="animate-spin" :size="18" /> Đang tải sản phẩm...
      </div>
      <table v-else class="min-w-full divide-y divide-slate-200 text-sm">
        <thead class="bg-slate-50 text-left text-slate-600">
          <tr>
            <th class="px-4 py-3"><input type="checkbox" :checked="props.selectedProductIds.length === props.items.length && props.items.length > 0" @change="emit('toggle-all')" /></th>
            <th class="px-4 py-3">Sản phẩm</th>
            <th class="px-4 py-3">Category</th>
            <th class="px-4 py-3">Giá</th>
            <th class="px-4 py-3">Tồn kho</th>
            <th class="px-4 py-3">Trạng thái</th>
            <th class="px-4 py-3 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 bg-white">
          <tr v-for="item in props.items" :key="item.id" class="hover:bg-slate-50">
            <td class="px-4 py-3 align-top"><input type="checkbox" :checked="props.selectedProductIds.includes(item.id)" @change="emit('toggle-select', item.id)" /></td>
            <td class="px-4 py-3 align-top">
              <div class="flex gap-3">
                <img v-if="item.thumbnailUrl" :src="resolveProductImageUrl(item.thumbnailUrl)" class="h-14 w-14 shrink-0 rounded-xl border bg-slate-50 object-cover" />
                <div>
                  <div class="font-semibold text-slate-900">{{ item.name }}</div>
                  <div class="text-xs text-slate-500">{{ item.baseSku }}</div>
                  <div class="mt-1 text-xs text-slate-500">{{ item.brand || '-' }} • {{ props.statusLabel(item.productType) }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3 align-top">
              <div class="flex flex-wrap gap-1">
                <span v-for="cat in item.categoryNames" :key="cat" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ cat }}</span>
              </div>
            </td>
            <td class="px-4 py-3 align-top">
              <div>{{ props.formatCurrency(item.minPrice) }}</div>
              <div v-if="item.maxPrice && item.maxPrice !== item.minPrice" class="text-xs text-slate-500">đến {{ props.formatCurrency(item.maxPrice) }}</div>
            </td>
            <td class="px-4 py-3 align-top">
              <div>{{ item.totalStock ?? 0 }}</div>
              <div class="text-xs text-slate-500">{{ props.statusLabel(item.stockStatus) }}</div>
            </td>
            <td class="px-4 py-3 align-top">
              <span class="inline-flex whitespace-nowrap rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-700">{{ props.statusLabel(item.displayStatus) }}</span>
            </td>
            <td class="px-4 py-3 align-top">
              <div class="flex justify-end gap-2 whitespace-nowrap">
                <button @click="emit('view', item.id)" class="rounded-lg border p-2 hover:bg-slate-50"><Eye :size="16" /></button>
                <button @click="emit('edit', item.id)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="16" /></button>
                <button @click="emit('hide-oos', item.id)" class="rounded-lg border px-3 text-xs hover:bg-slate-50">Hide OOS</button>
                <button @click="emit('delete', item.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="16" /></button>
              </div>
            </td>
          </tr>
          <tr v-if="props.items.length === 0">
            <td colspan="7" class="px-4 py-8 text-center text-sm text-slate-500">Chưa có dữ liệu sản phẩm.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-4 flex items-center justify-between text-sm text-slate-600">
      <div>Trang {{ props.page + 1 }} / {{ Math.max(props.totalPages, 1) }} — {{ props.totalElements }} sản phẩm</div>
      <div class="flex items-center gap-2">
        <button :disabled="props.page <= 0" @click="emit('prev-page')" class="rounded-lg border px-3 py-2 disabled:opacity-50">Trước</button>
        <button :disabled="props.page + 1 >= props.totalPages" @click="emit('next-page')" class="rounded-lg border px-3 py-2 disabled:opacity-50">Sau</button>
      </div>
    </div>
  </div>
</template>
