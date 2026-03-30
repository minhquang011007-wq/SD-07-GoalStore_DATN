<script setup lang="ts">
import { X } from "lucide-vue-next"
import type { CategoryRequest } from "@/modules/product/types"

defineProps<{
  open: boolean
  saving?: boolean
  editingCategoryId: number | null
  form: CategoryRequest
}>()

const emit = defineEmits<{
  (e: "close"): void
  (e: "submit"): void
}>()
</script>

<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
    <div class="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold">{{ editingCategoryId ? 'Sửa category' : 'Thêm category' }}</h3>
        <button @click="emit('close')" class="rounded-lg border p-2"><X :size="16" /></button>
      </div>
      <div class="mt-4 grid gap-4">
        <label class="space-y-2"><span class="text-sm">Tên category</span><input v-model="form.name" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Mô tả</span><textarea v-model="form.description" rows="4" class="w-full rounded-xl border px-3 py-2"></textarea></label>
      </div>
      <div class="mt-6 flex justify-end gap-3">
        <button @click="emit('close')" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
        <button @click="emit('submit')" :disabled="saving" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">{{ editingCategoryId ? 'Lưu thay đổi' : 'Tạo category' }}</button>
      </div>
    </div>
  </div>
</template>
