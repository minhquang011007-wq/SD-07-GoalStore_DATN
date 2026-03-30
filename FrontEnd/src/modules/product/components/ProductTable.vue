<script setup lang="ts">
import { Eye, LoaderCircle, Pencil, Trash2, Zap } from "lucide-vue-next"
import type { ProductSummaryResponse } from "@/modules/product/types"
import { resolveProductImageUrl } from "@/modules/product/utils/image"

const props = defineProps<{
  items: ProductSummaryResponse[]
  loading: boolean
  selectedProductIds: number[]
  page: number
  totalPages: number
  totalElements: number
  formatCurrency: (value?: number | null) => string
  statusLabel: (value?: string | null) => string
}>()

const emit = defineEmits<{
  (e: "toggle-all"): void
  (e: "toggle-select", id: number): void
  (e: "view", id: number): void
  (e: "edit", id: number): void
  (e: "quick-edit", id: number): void
  (e: "hide-oos", id: number): void
  (e: "delete", id: number): void
  (e: "prev-page"): void
  (e: "next-page"): void
}>()

function displayBadgeClass(value?: string | null) {
  switch (value) {
    case "HIENTHI":
      return "bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200"
    case "AN":
      return "bg-slate-100 text-slate-700 ring-1 ring-slate-200"
    case "NGUNG_BAN":
      return "bg-rose-50 text-rose-700 ring-1 ring-rose-200"
    default:
      return "bg-slate-100 text-slate-700 ring-1 ring-slate-200"
  }
}

function stockBadgeClass(value?: string | null) {
  switch (value) {
    case "CON_HANG":
      return "bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200"
    case "HET_HANG":
      return "bg-amber-50 text-amber-700 ring-1 ring-amber-200"
    case "PRE_ORDER":
      return "bg-sky-50 text-sky-700 ring-1 ring-sky-200"
    default:
      return "bg-slate-100 text-slate-700 ring-1 ring-slate-200"
  }
}
</script>

<template>
  <div>
    <div class="mt-4 overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <div v-if="props.loading" class="flex items-center justify-center gap-2 p-12 text-sm text-slate-500">
        <LoaderCircle class="animate-spin" :size="18" />
        Đang tải sản phẩm...
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead class="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th class="px-4 py-4">
                <input
                  type="checkbox"
                  :checked="props.selectedProductIds.length === props.items.length && props.items.length > 0"
                  @change="emit('toggle-all')"
                />
              </th>
              <th class="px-4 py-4">Sản phẩm</th>
              <th class="px-4 py-4">Danh mục</th>
              <th class="px-4 py-4">Giá</th>
              <th class="px-4 py-4">Tồn kho</th>
              <th class="px-4 py-4">Hiển thị</th>
              <th class="px-4 py-4 text-right">Thao tác</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-slate-100">
            <tr
              v-for="item in props.items"
              :key="item.id"
              class="transition hover:bg-slate-50/80"
            >
              <td class="px-4 py-4 align-top">
                <input
                  type="checkbox"
                  :checked="props.selectedProductIds.includes(item.id)"
                  @change="emit('toggle-select', item.id)"
                />
              </td>

              <td class="px-4 py-4 align-top">
                <div class="flex min-w-[260px] items-start gap-3">
                  <img
                    v-if="item.thumbnailUrl"
                    :src="resolveProductImageUrl(item.thumbnailUrl)"
                    class="h-14 w-14 rounded-2xl border border-slate-200 object-cover"
                  />
                  <div
                    v-else
                    class="flex h-14 w-14 items-center justify-center rounded-2xl border border-slate-200 bg-slate-50 text-[11px] text-slate-400"
                  >
                    No image
                  </div>

                  <div class="min-w-0">
                    <div class="line-clamp-2 text-sm font-semibold text-slate-900">
                      {{ item.name }}
                    </div>
                    <div class="mt-1 text-xs text-slate-500">
                      SKU: {{ item.baseSku }}
                    </div>
                    <div class="mt-1 flex flex-wrap items-center gap-2 text-xs text-slate-500">
                      <span>{{ item.brand || "Chưa có thương hiệu" }}</span>
                      <span>•</span>
                      <span>{{ props.statusLabel(item.productType) }}</span>
                    </div>
                  </div>
                </div>
              </td>

              <td class="px-4 py-4 align-top">
                <div v-if="item.categoryNames?.length" class="flex max-w-[240px] flex-wrap gap-2">
                  <span
                    v-for="cat in item.categoryNames"
                    :key="cat"
                    class="inline-flex max-w-full items-center rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-700"
                  >
                    <span class="truncate">{{ cat }}</span>
                  </span>
                </div>
                <span v-else class="text-xs text-slate-400">Chưa có danh mục</span>
              </td>

              <td class="px-4 py-4 align-top">
                <div class="font-semibold text-slate-900">
                  {{ props.formatCurrency(item.minPrice) }}
                </div>
                <div
                  v-if="item.maxPrice && item.maxPrice !== item.minPrice"
                  class="mt-1 text-xs text-slate-500"
                >
                  đến {{ props.formatCurrency(item.maxPrice) }}
                </div>
              </td>

              <td class="px-4 py-4 align-top">
                <div class="font-semibold text-slate-900">
                  {{ item.totalStock ?? 0 }}
                </div>
                <div class="mt-2">
                  <span
                    class="inline-flex rounded-full px-3 py-1 text-xs font-medium"
                    :class="stockBadgeClass(item.stockStatus)"
                  >
                    {{ props.statusLabel(item.stockStatus) }}
                  </span>
                </div>
              </td>

              <td class="px-4 py-4 align-top">
                <span
                  class="inline-flex rounded-full px-3 py-1 text-xs font-medium"
                  :class="displayBadgeClass(item.displayStatus)"
                >
                  {{ props.statusLabel(item.displayStatus) }}
                </span>
              </td>

              <td class="px-4 py-4 align-top">
                <div class="flex justify-end gap-2">
                  <button
                    @click="emit('view', item.id)"
                    class="rounded-xl border border-sky-200 bg-sky-50 p-2 text-sky-700 transition hover:bg-sky-100"
                    title="Xem chi tiết"
                  >
                    <Eye :size="16" />
                  </button>

                  <button
                    @click="emit('edit', item.id)"
                    class="rounded-xl border border-amber-200 bg-amber-50 p-2 text-amber-700 transition hover:bg-amber-100"
                    title="Chỉnh sửa"
                  >
                    <Pencil :size="16" />
                  </button>

                  <button
                    @click="emit('quick-edit', item.id)"
                    class="rounded-xl border border-violet-200 bg-violet-50 p-2 text-violet-700 transition hover:bg-violet-100"
                    title="Sửa nhanh"
                  >
                    <Zap :size="16" />
                  </button>

                  <button
                    @click="emit('hide-oos', item.id)"
                    class="rounded-xl border border-slate-200 bg-white px-3 text-xs font-medium text-slate-700 transition hover:bg-slate-50"
                    title="Ẩn nếu hết hàng"
                  >
                    Ẩn OOS
                  </button>

                  <button
                    @click="emit('delete', item.id)"
                    class="rounded-xl border border-rose-200 bg-rose-50 p-2 text-rose-700 transition hover:bg-rose-100"
                    title="Xóa"
                  >
                    <Trash2 :size="16" />
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="props.items.length === 0">
              <td colspan="7" class="px-4 py-12 text-center text-sm text-slate-500">
                Chưa có dữ liệu sản phẩm.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="mt-4 flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-600 shadow-sm md:flex-row md:items-center md:justify-between">
      <div>
        Trang <span class="font-semibold text-slate-900">{{ props.page + 1 }}</span>
        / <span class="font-semibold text-slate-900">{{ Math.max(props.totalPages, 1) }}</span>
        — Tổng <span class="font-semibold text-slate-900">{{ props.totalElements }}</span> sản phẩm
      </div>

      <div class="flex items-center gap-2">
        <button
          :disabled="props.page <= 0"
          @click="emit('prev-page')"
          class="rounded-xl border border-slate-200 bg-white px-4 py-2 font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Trước
        </button>
        <button
          :disabled="props.page + 1 >= props.totalPages"
          @click="emit('next-page')"
          class="rounded-xl border border-slate-200 bg-white px-4 py-2 font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Sau
        </button>
      </div>
    </div>
  </div>
</template>