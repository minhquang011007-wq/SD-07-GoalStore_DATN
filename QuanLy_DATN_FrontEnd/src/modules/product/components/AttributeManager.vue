<script setup lang="ts">
import { Pencil, Plus, Trash2 } from "lucide-vue-next"
import type { ProductAttributeResponse } from "@/modules/product/types"

defineProps<{
  attributes: ProductAttributeResponse[]
}>()

const emit = defineEmits<{
  (e: "create"): void
  (e: "edit", attribute: ProductAttributeResponse): void
  (e: "remove", id: number): void
}>()
</script>

<template>
  <div>
    <div class="mb-2 flex items-center justify-between">
      <h4 class="font-semibold text-slate-900">Thuộc tính sản phẩm</h4>
      <button @click="emit('create')" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
        <span class="inline-flex items-center gap-2"><Plus :size="14" /> Thêm thuộc tính</span>
      </button>
    </div>

    <div class="space-y-2">
      <div v-for="item in attributes" :key="item.id" class="rounded-xl border p-3 text-sm">
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="font-medium">{{ item.name }}</div>
            <div class="text-slate-600">{{ item.value }}</div>
            <div class="mt-1 text-xs text-slate-400">Thứ tự: {{ item.sortOrder }}</div>
          </div>
          <div class="flex gap-2">
            <button @click="emit('edit', item)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="14" /></button>
            <button @click="emit('remove', item.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="14" /></button>
          </div>
        </div>
      </div>
      <div v-if="attributes.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có thuộc tính.</div>
    </div>
  </div>
</template>
