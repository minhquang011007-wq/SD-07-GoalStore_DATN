<script setup lang="ts">
import { X } from "lucide-vue-next"
import type { TagRequest } from "@/modules/product/types"

defineProps<{ open: boolean; saving: boolean; editingId: number | null; form: TagRequest }>()
const emit = defineEmits<{ (e: 'close'): void; (e: 'submit'): void }>()
</script>

<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
    <div class="w-full max-w-xl rounded-3xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b px-6 py-4">
        <h3 class="text-lg font-semibold text-slate-900">{{ editingId ? 'Sửa tag' : 'Tạo tag mới' }}</h3>
        <button class="rounded-full border p-2" @click="emit('close')"><X class="h-4 w-4" /></button>
      </div>
      <div class="space-y-4 p-6">
        <label><span class="mb-1 block text-sm font-medium">Tên tag</span><input v-model="form.name" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">Mô tả</span><textarea v-model="form.description" rows="4" class="w-full rounded-xl border px-3 py-2.5 text-sm"></textarea></label>
      </div>
      <div class="flex justify-end gap-3 border-t px-6 py-4">
        <button class="rounded-xl border px-4 py-2 text-sm font-medium" @click="emit('close')">Đóng</button>
        <button class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white" :disabled="saving" @click="emit('submit')">{{ saving ? 'Đang lưu...' : editingId ? 'Lưu thay đổi' : 'Tạo tag' }}</button>
      </div>
    </div>
  </div>
</template>
