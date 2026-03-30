<script setup lang="ts">
defineProps<{
  open: boolean
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
}>()

const emit = defineEmits<{
  (e: "confirm"): void
  (e: "cancel"): void
}>()
</script>

<template>
  <div v-if="open" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
    <div class="bg-card w-full max-w-md rounded-md border p-4 space-y-4">
      <div>
        <div class="font-semibold">{{ title || "Xác nhận" }}</div>
        <div class="text-sm text-slate-600 mt-1">{{ message }}</div>
      </div>
      <div class="flex justify-end gap-2">
        <button class="px-4 py-2 rounded-md border" @click="emit('cancel')">
          {{ cancelText || "Hủy" }}
        </button>
        <button class="px-4 py-2 rounded-md bg-red-600 text-white" @click="emit('confirm')">
          {{ confirmText || "Xác nhận" }}
        </button>
      </div>
    </div>
  </div>
</template>