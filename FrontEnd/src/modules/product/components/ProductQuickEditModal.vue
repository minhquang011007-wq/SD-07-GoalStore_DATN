<script setup lang="ts">
import { X } from "lucide-vue-next"
import type { ProductDisplayStatus, ProductQuickUpdateRequest } from "@/modules/product/types"

const props = defineProps<{
  open: boolean
  productName: string
  saving?: boolean
  displayStatusOptions: Array<{ label: string; value: ProductDisplayStatus }>
  form: ProductQuickUpdateRequest
}>()

const emit = defineEmits<{
  (e: "close"): void
  (e: "submit"): void
}>()
</script>

<template>
  <div v-if="props.open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
    <div class="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
      <div class="flex items-center justify-between gap-3">
        <div>
          <h3 class="text-lg font-semibold text-slate-900">Quick update sản phẩm</h3>
          <p class="text-sm text-slate-500">{{ props.productName || "Chỉnh sửa nhanh thông tin cơ bản" }}</p>
        </div>
        <button @click="emit('close')" class="rounded-lg border p-2 hover:bg-slate-50"><X :size="16" /></button>
      </div>

      <div class="mt-5 grid gap-4 md:grid-cols-2">
        <label class="space-y-2 md:col-span-2">
          <span class="text-sm font-medium text-slate-700">Tên sản phẩm</span>
          <input v-model="props.form.name" class="w-full rounded-xl border px-3 py-2.5 text-sm" placeholder="Tên sản phẩm" />
        </label>

        <label class="space-y-2">
          <span class="text-sm font-medium text-slate-700">Brand</span>
          <input v-model="props.form.brand" class="w-full rounded-xl border px-3 py-2.5 text-sm" placeholder="Brand" />
        </label>

        <label class="space-y-2">
          <span class="text-sm font-medium text-slate-700">Season</span>
          <input v-model="props.form.season" class="w-full rounded-xl border px-3 py-2.5 text-sm" placeholder="Season" />
        </label>

        <label class="space-y-2">
          <span class="text-sm font-medium text-slate-700">Material</span>
          <input v-model="props.form.material" class="w-full rounded-xl border px-3 py-2.5 text-sm" placeholder="Material" />
        </label>

        <label class="space-y-2">
          <span class="text-sm font-medium text-slate-700">Trạng thái hiển thị</span>
          <select v-model="props.form.displayStatus" class="w-full rounded-xl border px-3 py-2.5 text-sm">
            <option :value="undefined">Giữ nguyên</option>
            <option v-for="item in props.displayStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
        </label>
      </div>

      <div class="mt-6 flex justify-end gap-3">
        <button @click="emit('close')" class="rounded-xl border px-4 py-2 text-sm font-medium">Hủy</button>
        <button @click="emit('submit')" :disabled="props.saving" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
          {{ props.saving ? "Đang lưu..." : "Lưu nhanh" }}
        </button>
      </div>
    </div>
  </div>
</template>
