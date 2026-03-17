<script setup lang="ts">
import { Pencil, Plus, Trash2, Upload } from "lucide-vue-next"
import type { CategoryResponse } from "@/modules/product/types"
import { resolveProductImageUrl } from "@/modules/product/utils/image"

defineProps<{
  loading?: boolean
  items: CategoryResponse[]
}>()

const emit = defineEmits<{
  (e: "create"): void
  (e: "edit", category: CategoryResponse): void
  (e: "delete", id: number): void
  (e: "upload-image", payload: { id: number; event: Event }): void
  (e: "refresh"): void
}>()
</script>

<template>
  <div>
    <div class="mt-4 flex flex-wrap items-center gap-3">
      <button @click="emit('create')" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">
        <span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm category</span>
      </button>
      <button @click="emit('refresh')" class="rounded-xl border px-4 py-2 text-sm font-medium">Làm mới danh mục</button>
    </div>

    <div v-if="loading" class="mt-4 rounded-2xl border p-10 text-center text-sm text-slate-500">Đang tải category...</div>

    <div v-else class="mt-4 grid gap-4 md:grid-cols-2">
      <div v-for="category in items" :key="category.id" class="rounded-2xl border p-4">
        <div class="flex gap-3">
          <img v-if="category.imageUrl" :src="resolveProductImageUrl(category.imageUrl)" class="h-16 w-16 rounded-xl border object-cover" />
          <div v-else class="flex h-16 w-16 items-center justify-center rounded-xl border bg-slate-50 text-[11px] text-slate-400">No image</div>
          <div class="flex-1">
            <div class="flex items-start justify-between gap-2">
              <div>
                <h3 class="font-semibold text-slate-900">{{ category.name }}</h3>
                <p class="mt-1 text-sm text-slate-500">{{ category.description || 'Chưa có mô tả' }}</p>
              </div>
              <span class="inline-flex whitespace-nowrap rounded-full bg-slate-100 px-3 py-1 text-xs">{{ category.productCount }} SP</span>
            </div>
            <div class="mt-3 flex flex-wrap gap-2">
              <button @click="emit('edit', category)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
                <span class="inline-flex items-center gap-2"><Pencil :size="14" /> Sửa</span>
              </button>
              <label class="cursor-pointer rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
                <span class="inline-flex items-center gap-2"><Upload :size="14" /> Ảnh</span>
                <input type="file" class="hidden" accept="image/*" @change="emit('upload-image', { id: category.id, event: $event })" />
              </label>
              <button @click="emit('delete', category.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">
                <span class="inline-flex items-center gap-2"><Trash2 :size="14" /> Xóa</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="items.length === 0" class="rounded-2xl border p-10 text-center text-sm text-slate-500">Chưa có category.</div>
    </div>
  </div>
</template>
