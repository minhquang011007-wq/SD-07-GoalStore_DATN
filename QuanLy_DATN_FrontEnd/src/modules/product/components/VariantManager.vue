<script setup lang="ts">
import { Pencil, Trash2 } from "lucide-vue-next"
import type { ProductVariantResponse } from "@/modules/product/types"

const props = defineProps<{
  variants: ProductVariantResponse[]
  formatCurrency: (value?: number | null) => string
  statusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: 'create'): void
  (e: 'edit', variant: ProductVariantResponse): void
  (e: 'remove', id: number): void
}>()
</script>

<template>
  <div>
    <div class="mb-2 flex items-center justify-between">
      <h4 class="font-semibold text-slate-900">Variants</h4>
      <button @click="emit('create')" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Thêm variant</button>
    </div>
    <div class="space-y-2">
      <div v-for="variant in props.variants" :key="variant.id" class="rounded-xl border p-3 text-sm">
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="font-medium">{{ variant.sku }}</div>
            <div class="text-slate-500">{{ variant.size }} • {{ variant.color }}</div>
            <div class="mt-1 text-slate-500">{{ props.formatCurrency(variant.salePrice || variant.price) }} • {{ variant.stockQuantity }} • {{ props.statusLabel(variant.stockStatus) }}</div>
          </div>
          <div class="flex gap-2">
            <button @click="emit('edit', variant)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="14" /></button>
            <button @click="emit('remove', variant.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="14" /></button>
          </div>
        </div>
      </div>
      <div v-if="props.variants.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có variant.</div>
    </div>
  </div>
</template>
