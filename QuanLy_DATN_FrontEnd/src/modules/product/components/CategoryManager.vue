<script setup lang="ts">
import type { CategoryResponse } from "@/modules/product/types"

defineProps<{
  loading: boolean
  items: CategoryResponse[]
  normalizeAsset: (url?: string | null) => string
}>()

const emit = defineEmits<{
  (e: "create"): void
  (e: "edit", item: CategoryResponse): void
  (e: "delete", id: number): void
  (e: "upload", payload: { event: Event; id: number }): void
}>()
</script>

<template>
  <div class="space-y-4 rounded-2xl border bg-white p-4 shadow-sm">
    <div class="flex items-center gap-3">
      <button @click="emit('create')" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">Thêm category</button>
    </div>
    <div v-if="loading" class="rounded-2xl border p-10 text-center text-sm text-slate-500">Đang tải category...</div>
    <div v-else class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      <div v-for="category in items" :key="category.id" class="rounded-2xl border p-4">
        <div class="mb-4 flex items-start gap-3">
          <img :src="normalizeAsset(category.imageUrl) || 'https://placehold.co/88x88?text=Category'" class="h-20 w-20 rounded-2xl border object-cover" alt="category" />
          <div class="min-w-0 flex-1">
            <div class="font-semibold text-slate-900">{{ category.name }}</div>
            <div class="mt-1 text-sm text-slate-500">{{ category.description || 'Chưa có mô tả' }}</div>
            <div class="mt-2 text-xs text-slate-500">{{ category.productCount }} sản phẩm</div>
          </div>
        </div>
        <div class="flex flex-wrap gap-2">
          <button class="rounded-lg border px-3 py-2 text-xs" @click="emit('edit', category)">Sửa</button>
          <label class="cursor-pointer rounded-lg border px-3 py-2 text-xs">Ảnh<input type="file" class="hidden" accept="image/*" @change="emit('upload', { event: $event, id: category.id })" /></label>
          <button class="rounded-lg border border-red-200 px-3 py-2 text-xs text-red-600" @click="emit('delete', category.id)">Xóa</button>
        </div>
      </div>
      <div v-if="items.length === 0" class="rounded-2xl border border-dashed p-10 text-center text-sm text-slate-500">Chưa có category.</div>
    </div>
  </div>
</template>
