<script setup lang="ts">
import type { ProductVariantRequest, ProductVariantResponse, VariantStockStatus } from "@/modules/product/types"

defineProps<{
  items: ProductVariantResponse[]
  form: ProductVariantRequest
  editingVariantId: number | null
  stockStatusOptions: Array<{ label: string; value: VariantStockStatus }>
  formatCurrency: (value?: number | null) => string
  stockStatusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: "edit", variant: ProductVariantResponse): void
  (e: "delete", id: number): void
  (e: "submit"): void
  (e: "reset"): void
}>()
</script>

<template>
  <div class="space-y-4">
    <div>
      <div class="mb-2 text-sm font-medium text-slate-700">Variants</div>
      <div class="space-y-2">
        <div v-for="variant in items" :key="variant.id" class="rounded-2xl border px-3 py-3 text-sm">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <div class="font-medium text-slate-900">{{ variant.sku }}</div>
              <div class="text-slate-500">{{ variant.size }} • {{ variant.color }} • {{ stockStatusLabel(variant.stockStatus) }}</div>
              <div class="text-slate-500">{{ formatCurrency(variant.salePrice ?? variant.price) }} · Tồn {{ variant.stockQuantity }}</div>
            </div>
            <div class="flex gap-2">
              <button class="rounded-lg border px-3 py-2 text-xs" @click="emit('edit', variant)">Sửa</button>
              <button class="rounded-lg border border-red-200 px-3 py-2 text-xs text-red-600" @click="emit('delete', variant.id)">Xóa</button>
            </div>
          </div>
        </div>
        <div v-if="!items.length" class="rounded-2xl border border-dashed p-5 text-center text-sm text-slate-500">Chưa có variant nào</div>
      </div>
    </div>

    <div class="rounded-2xl border bg-slate-50 p-4">
      <div class="mb-3 flex items-center justify-between">
        <div>
          <h4 class="font-medium text-slate-900">{{ editingVariantId ? 'Sửa variant' : 'Thêm variant' }}</h4>
          <p class="text-xs text-slate-500">Mỗi size + màu là 1 SKU riêng</p>
        </div>
        <button v-if="editingVariantId" class="text-xs text-slate-500" @click="emit('reset')">Hủy sửa</button>
      </div>
      <div class="grid gap-3 md:grid-cols-2">
        <input v-model="form.sku" class="rounded-xl border px-3 py-2 text-sm" placeholder="SKU variant" />
        <input v-model="form.size" class="rounded-xl border px-3 py-2 text-sm" placeholder="Size" />
        <input v-model="form.color" class="rounded-xl border px-3 py-2 text-sm" placeholder="Màu" />
        <select v-model="form.stockStatus" class="rounded-xl border px-3 py-2 text-sm"><option v-for="item in stockStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select>
        <input v-model.number="form.price" type="number" min="0" class="rounded-xl border px-3 py-2 text-sm" placeholder="Giá bán" />
        <input v-model.number="form.salePrice" type="number" min="0" class="rounded-xl border px-3 py-2 text-sm" placeholder="Giá khuyến mãi" />
        <input v-model.number="form.stockQuantity" type="number" min="0" class="rounded-xl border px-3 py-2 text-sm md:col-span-2" placeholder="Tồn kho" />
      </div>
      <div class="mt-3 flex gap-3">
        <button class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white" @click="emit('submit')">{{ editingVariantId ? 'Lưu variant' : 'Thêm variant' }}</button>
        <button class="rounded-xl border px-4 py-2 text-sm font-medium" @click="emit('reset')">Làm mới form</button>
      </div>
    </div>
  </div>
</template>
