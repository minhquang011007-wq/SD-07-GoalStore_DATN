<script setup lang="ts">
import { Eye, Pencil, Trash2 } from "lucide-vue-next"
import type { ProductSummaryResponse } from "@/modules/product/types"

defineProps<{
  loading: boolean
  items: ProductSummaryResponse[]
  page: number
  totalPages: number
  totalElements: number
  normalizeAsset: (url?: string | null) => string
  formatCurrency: (value?: number | null) => string
  displayStatusLabel: (value?: string | null) => string
  stockStatusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: "detail", id: number): void
  (e: "edit", item: ProductSummaryResponse): void
  (e: "delete", id: number): void
  (e: "hide-oos", id: number): void
  (e: "page", page: number): void
}>()
</script>

<template>
  <div class="rounded-2xl border bg-white shadow-sm">
    <div v-if="loading" class="p-10 text-center text-sm text-slate-500">Đang tải sản phẩm...</div>
    <div v-else class="overflow-x-auto">
      <table class="min-w-full divide-y divide-slate-200 text-sm">
        <thead class="bg-slate-50 text-left text-slate-600">
          <tr>
            <th class="px-4 py-3">Sản phẩm</th>
            <th class="px-4 py-3">Category</th>
            <th class="px-4 py-3">Giá</th>
            <th class="px-4 py-3">Tồn kho</th>
            <th class="px-4 py-3">Hiển thị</th>
            <th class="px-4 py-3 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 bg-white">
          <tr v-for="item in items" :key="item.id" class="hover:bg-slate-50">
            <td class="px-4 py-3 align-top">
              <div class="flex gap-3">
                <img v-if="item.thumbnailUrl" :src="normalizeAsset(item.thumbnailUrl)" class="h-12 w-12 rounded-xl border object-cover" />
                <div>
                  <div class="font-semibold text-slate-900">{{ item.name }}</div>
                  <div class="text-xs text-slate-500">{{ item.baseSku }}</div>
                  <div class="mt-1 text-xs text-slate-500">{{ item.brand || '-' }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3 align-top">
              <div class="flex flex-wrap gap-1">
                <span v-for="cat in item.categoryNames" :key="cat" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ cat }}</span>
              </div>
            </td>
            <td class="px-4 py-3 align-top">{{ formatCurrency(item.minPrice) }}</td>
            <td class="px-4 py-3 align-top">
              <div>{{ item.totalStock ?? 0 }}</div>
              <div class="text-xs text-slate-500">{{ stockStatusLabel(item.stockStatus) }}</div>
            </td>
            <td class="px-4 py-3 align-top">
              <span class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ displayStatusLabel(item.displayStatus) }}</span>
            </td>
            <td class="px-4 py-3 align-top">
              <div class="flex justify-end gap-2">
                <button @click="emit('detail', item.id)" class="rounded-lg border p-2 hover:bg-slate-50"><Eye :size="16" /></button>
                <button @click="emit('edit', item)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="16" /></button>
                <button @click="emit('hide-oos', item.id)" class="rounded-lg border px-3 text-xs hover:bg-slate-50">Hide OOS</button>
                <button @click="emit('delete', item.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="16" /></button>
              </div>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="6" class="px-4 py-8 text-center text-sm text-slate-500">Chưa có dữ liệu sản phẩm.</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="flex items-center justify-between px-4 py-4 text-sm text-slate-600">
      <div>Trang {{ page + 1 }} / {{ Math.max(totalPages, 1) }} — {{ totalElements }} sản phẩm</div>
      <div class="flex items-center gap-2">
        <button :disabled="page <= 0" @click="emit('page', page - 1)" class="rounded-lg border px-3 py-2 disabled:opacity-50">Trước</button>
        <button :disabled="page + 1 >= totalPages" @click="emit('page', page + 1)" class="rounded-lg border px-3 py-2 disabled:opacity-50">Sau</button>
      </div>
    </div>
  </div>
</template>
