<script setup lang="ts">
import { Flame, Sparkles, ArrowRight, Package } from "lucide-vue-next"
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

function stockText(item: ProductSummaryResponse) {
  const totalStock =
    item.totalStock ??
    item.stockQuantity ??
    0

  return totalStock > 0 ? `Tồn: ${totalStock}` : "Hết hàng"
}
</script>

<template>
  <div class="grid gap-4 lg:grid-cols-2">
    <!-- Bán chạy -->
    <div class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <div class="border-b border-slate-100 bg-gradient-to-r from-orange-50 to-rose-50 px-5 py-4">
        <div class="flex items-center justify-between gap-3">
          <div class="flex items-center gap-3">
            <div class="flex h-10 w-10 items-center justify-center rounded-2xl bg-white shadow-sm ring-1 ring-orange-100">
              <Flame :size="18" class="text-orange-600" />
            </div>
            <div>
              <div class="text-sm font-semibold text-slate-900">Sản phẩm bán chạy</div>
              <div class="text-xs text-slate-500">Ưu tiên theo số lượng đã bán</div>
            </div>
          </div>
          <div class="rounded-full bg-white px-3 py-1 text-xs font-medium text-slate-600 ring-1 ring-slate-200">
            {{ props.topSelling.length }} sản phẩm
          </div>
        </div>
      </div>

      <div class="p-4">
        <div v-if="props.loading" class="space-y-3">
          <div
            v-for="i in 4"
            :key="i"
            class="h-[76px] animate-pulse rounded-2xl border border-slate-200 bg-slate-100"
          />
        </div>

        <div v-else-if="props.topSelling.length" class="space-y-3">
          <button
            v-for="item in props.topSelling"
            :key="item.id"
            @click="emit('open', item.id)"
            class="group flex w-full items-center gap-3 rounded-2xl border border-slate-200 bg-white p-3 text-left transition hover:-translate-y-0.5 hover:border-orange-200 hover:bg-orange-50/40"
          >
            <img
              v-if="item.thumbnailUrl"
              :src="resolveProductImageUrl(item.thumbnailUrl)"
              class="h-14 w-14 rounded-2xl border border-slate-200 object-cover"
            />

            <div
              v-else
              class="flex h-14 w-14 items-center justify-center rounded-2xl border border-slate-200 bg-slate-50 text-[11px] text-slate-400"
            >
              Chưa có ảnh
            </div>

            <div class="min-w-0 flex-1">
              <div class="truncate text-sm font-semibold text-slate-900">
                {{ item.name }}
              </div>
              <div class="mt-1 truncate text-xs text-slate-500">
                SKU: {{ item.baseSku }}
              </div>
              <div class="mt-2 flex items-center gap-2 text-xs text-slate-500">
                <span class="rounded-full bg-slate-100 px-2 py-1">{{ stockText(item) }}</span>
              </div>
            </div>

            <div class="text-right">
              <div class="text-xs text-slate-500">Đã bán</div>
              <div class="mt-1 text-base font-bold text-slate-900">
                {{ item.soldQuantity ?? 0 }}
              </div>
              <ArrowRight
                :size="16"
                class="ml-auto mt-2 text-slate-300 transition group-hover:text-orange-500"
              />
            </div>
          </button>
        </div>

        <div
          v-else
          class="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-10 text-center"
        >
          <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-white ring-1 ring-slate-200">
            <Flame :size="18" class="text-slate-400" />
          </div>
          <div class="mt-3 text-sm font-medium text-slate-700">Chưa có dữ liệu bán chạy</div>
          <div class="mt-1 text-xs text-slate-500">Sản phẩm bán chạy sẽ hiển thị tại đây.</div>
        </div>
      </div>
    </div>

    <!-- Mới nhất -->
    <div class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <div class="border-b border-slate-100 bg-gradient-to-r from-sky-50 to-indigo-50 px-5 py-4">
        <div class="flex items-center justify-between gap-3">
          <div class="flex items-center gap-3">
            <div class="flex h-10 w-10 items-center justify-center rounded-2xl bg-white shadow-sm ring-1 ring-sky-100">
              <Sparkles :size="18" class="text-sky-600" />
            </div>
            <div>
              <div class="text-sm font-semibold text-slate-900">Sản phẩm mới nhất</div>
              <div class="text-xs text-slate-500">Danh sách sản phẩm vừa thêm gần đây</div>
            </div>
          </div>
          <div class="rounded-full bg-white px-3 py-1 text-xs font-medium text-slate-600 ring-1 ring-slate-200">
            {{ props.newest.length }} sản phẩm
          </div>
        </div>
      </div>

      <div class="p-4">
        <div v-if="props.loading" class="space-y-3">
          <div
            v-for="i in 4"
            :key="i"
            class="h-[76px] animate-pulse rounded-2xl border border-slate-200 bg-slate-100"
          />
        </div>

        <div v-else-if="props.newest.length" class="space-y-3">
          <button
            v-for="item in props.newest"
            :key="item.id"
            @click="emit('open', item.id)"
            class="group flex w-full items-center gap-3 rounded-2xl border border-slate-200 bg-white p-3 text-left transition hover:-translate-y-0.5 hover:border-sky-200 hover:bg-sky-50/40"
          >
            <img
              v-if="item.thumbnailUrl"
              :src="resolveProductImageUrl(item.thumbnailUrl)"
              class="h-14 w-14 rounded-2xl border border-slate-200 object-cover"
            />

            <div
              v-else
              class="flex h-14 w-14 items-center justify-center rounded-2xl border border-slate-200 bg-slate-50 text-[11px] text-slate-400"
            >
              <Package :size="16" class="text-slate-400" />
            </div>

            <div class="min-w-0 flex-1">
              <div class="truncate text-sm font-semibold text-slate-900">
                {{ item.name }}
              </div>
              <div class="mt-1 truncate text-xs text-slate-500">
                SKU: {{ item.baseSku }}
              </div>
              <div class="mt-2 flex items-center gap-2 text-xs text-slate-500">
                <span class="rounded-full bg-slate-100 px-2 py-1">{{ stockText(item) }}</span>
              </div>
            </div>

            <div class="text-right">
              <div class="text-xs text-slate-500">Giá từ</div>
              <div class="mt-1 text-sm font-bold text-slate-900">
                {{ props.formatCurrency(item.minPrice) }}
              </div>
              <ArrowRight
                :size="16"
                class="ml-auto mt-2 text-slate-300 transition group-hover:text-sky-500"
              />
            </div>
          </button>
        </div>

        <div
          v-else
          class="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-10 text-center"
        >
          <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-white ring-1 ring-slate-200">
            <Sparkles :size="18" class="text-slate-400" />
          </div>
          <div class="mt-3 text-sm font-medium text-slate-700">Chưa có sản phẩm mới</div>
          <div class="mt-1 text-xs text-slate-500">Sản phẩm mới thêm sẽ hiển thị tại đây.</div>
        </div>
      </div>
    </div>
  </div>
</template>