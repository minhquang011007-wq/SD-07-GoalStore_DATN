<script setup lang="ts">
import type { TagResponse } from "@/modules/product/types"

defineProps<{ items: TagResponse[] }>()
const emit = defineEmits<{ (e: 'create'): void; (e: 'edit', item: TagResponse): void; (e: 'delete', id: number): void }>()
</script>

<template>
  <div class="space-y-4 rounded-2xl border bg-white p-4 shadow-sm">
    <button @click="emit('create')" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">Thêm tag</button>
    <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      <div v-for="tag in items" :key="tag.id" class="rounded-2xl border p-4">
        <div class="font-semibold text-slate-900">{{ tag.name }}</div>
        <div class="mt-1 text-sm text-slate-500">{{ tag.description || 'Chưa có mô tả' }}</div>
        <div class="mt-3 flex gap-2">
          <button @click="emit('edit', tag)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa</button>
          <button @click="emit('delete', tag.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa</button>
        </div>
      </div>
      <div v-if="items.length === 0" class="rounded-2xl border border-dashed p-10 text-center text-sm text-slate-500">Chưa có tag.</div>
    </div>
  </div>
</template>
