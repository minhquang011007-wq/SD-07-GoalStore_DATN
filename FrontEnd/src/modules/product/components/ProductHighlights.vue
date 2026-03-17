<script setup lang="ts">
import { Flame, Sparkles } from "lucide-vue-next"
import type { ProductSummaryResponse } from "@/modules/product/types"
import { resolveProductImageUrl } from "@/modules/product/utils/image"

const props = defineProps<{
  topSelling: ProductSummaryResponse[]
  newest: ProductSummaryResponse[]
  loading?: boolean
  formatCurrency: (value?: number | null) => string
}>()

const emit = defineEmits<{
  (e: "open", id: number): void
}>()
</script>

<template>
  <div class="grid gap-4 lg:grid-cols-2">
    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <div class="mb-3 flex items-center gap-2 text-sm font-semibold text-slate-900"><Flame :size="16" /> Bán chạy</div>
      <div v-if="props.loading" class="text-sm text-slate-500">Đang tải...</div>
      <div v-else class="space-y-3">
        <button
          v-for="item in props.topSelling"
          :key="item.id"
          @click="emit('open', item.id)"
          class="flex w-full items-center gap-3 rounded-xl border p-3 text-left hover:bg-slate-50"
        >
          <img v-if="item.thumbnailUrl" :src="resolveProductImageUrl(item.thumbnailUrl)" class="h-12 w-12 rounded-xl border object-cover" />
          <div v-else class="flex h-12 w-12 items-center justify-center rounded-xl border bg-slate-50 text-[11px] text-slate-400">No image</div>
          <div class="min-w-0 flex-1">
            <div class="truncate text-sm font-medium text-slate-900">{{ item.name }}</div>
            <div class="truncate text-xs text-slate-500">{{ item.baseSku }}</div>
          </div>
          <div class="text-right">
            <div class="text-xs text-slate-500">Đã bán</div>
            <div class="text-sm font-semibold text-slate-900">{{ item.soldQuantity ?? 0 }}</div>
          </div>
        </button>
        <div v-if="!props.topSelling.length" class="rounded-xl border border-dashed p-4 text-sm text-slate-500">Chưa có dữ liệu bán chạy.</div>
      </div>
    </div>

    <div class="rounded-2xl border bg-white p-4 shadow-sm">
      <div class="mb-3 flex items-center gap-2 text-sm font-semibold text-slate-900"><Sparkles :size="16" /> Mới nhất</div>
      <div v-if="props.loading" class="text-sm text-slate-500">Đang tải...</div>
      <div v-else class="space-y-3">
        <button
          v-for="item in props.newest"
          :key="item.id"
          @click="emit('open', item.id)"
          class="flex w-full items-center gap-3 rounded-xl border p-3 text-left hover:bg-slate-50"
        >
          <img v-if="item.thumbnailUrl" :src="resolveProductImageUrl(item.thumbnailUrl)" class="h-12 w-12 rounded-xl border object-cover" />
          <div v-else class="flex h-12 w-12 items-center justify-center rounded-xl border bg-slate-50 text-[11px] text-slate-400">No image</div>
          <div class="min-w-0 flex-1">
            <div class="truncate text-sm font-medium text-slate-900">{{ item.name }}</div>
            <div class="truncate text-xs text-slate-500">{{ item.baseSku }}</div>
          </div>
          <div class="text-right">
            <div class="text-xs text-slate-500">Giá từ</div>
            <div class="text-sm font-semibold text-slate-900">{{ props.formatCurrency(item.minPrice) }}</div>
          </div>
        </button>
        <div v-if="!props.newest.length" class="rounded-xl border border-dashed p-4 text-sm text-slate-500">Chưa có dữ liệu sản phẩm mới.</div>
      </div>
    </div>
  </div>
</template>
