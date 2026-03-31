<script setup lang="ts">
import type { ProductDisplayStatus, ProductBatchUpdateItemRequest } from "@/modules/product/types"

const props = defineProps<{
  selectedCount: number
  saving?: boolean
  displayStatusOptions: Array<{ label: string; value: ProductDisplayStatus }>
  form: Omit<ProductBatchUpdateItemRequest, "id">
}>()

const emit = defineEmits<{
  (e: "apply"): void
  (e: "clear"): void
}>()
</script>

<template>
  <div v-if="props.selectedCount > 0" class="mt-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
    <div class="space-y-4">
      <div>
        <div class="text-sm font-semibold text-slate-900">Cập nhật hàng loạt sản phẩm</div>
        <div class="text-xs text-slate-500">
          Đã chọn {{ props.selectedCount }} sản phẩm. Chỉ nhập trường nào bạn muốn cập nhật hàng loạt.
        </div>
      </div>

      <div class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <input
          v-model="props.form.brand"
          class="min-w-0 rounded-xl border bg-white px-3 py-2 text-sm"
          placeholder="Thương hiệu"
        />
        <input
          v-model="props.form.season"
          class="min-w-0 rounded-xl border bg-white px-3 py-2 text-sm"
          placeholder="Mùa giải"
        />
        <input
          v-model="props.form.material"
          class="min-w-0 rounded-xl border bg-white px-3 py-2 text-sm"
          placeholder="Chất liệu"
        />
        <select v-model="props.form.displayStatus" class="min-w-0 rounded-xl border bg-white px-3 py-2 text-sm">
          <option :value="undefined">Trạng thái hiển thị</option>
          <option v-for="item in props.displayStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>

      <div class="flex flex-wrap items-center justify-end gap-2 pt-1">
        <button @click="emit('clear')" class="rounded-xl border px-4 py-2 text-sm font-medium">Bỏ chọn</button>
        <button
          @click="emit('apply')"
          :disabled="props.saving"
          class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
        >
          {{ props.saving ? "Đang cập nhật..." : "Áp dụng" }}
        </button>
      </div>
    </div>
  </div>
</template>
