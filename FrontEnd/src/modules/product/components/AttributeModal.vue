<script setup lang="ts">
import { X } from "lucide-vue-next"
import type { ProductAttributeRequest } from "@/modules/product/types"

defineProps<{
  open: boolean
  saving?: boolean
  editingAttributeId: number | null
  form: ProductAttributeRequest
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
        <h3 class="text-lg font-semibold">{{ editingAttributeId ? 'Sửa thuộc tính' : 'Thêm thuộc tính' }}</h3>
        <button @click="emit('close')" class="rounded-lg border p-2"><X :size="16" /></button>
      </div>
      <div class="mt-4 grid gap-4">
        <label class="space-y-2"><span class="text-sm">Tên thuộc tính</span><input v-model="form.name" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Giá trị</span><input v-model="form.value" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Thứ tự</span><input v-model.number="form.sortOrder" type="number" class="w-full rounded-xl border px-3 py-2" /></label>
      </div>
      <div class="mt-6 flex justify-end gap-3">
        <button @click="emit('close')" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
        <button @click="emit('submit')" :disabled="saving" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">{{ editingAttributeId ? 'Lưu thay đổi' : 'Tạo thuộc tính' }}</button>
      </div>
    </div>
  </div>
</template>
