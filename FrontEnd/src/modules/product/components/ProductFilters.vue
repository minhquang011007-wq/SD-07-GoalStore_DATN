<script setup lang="ts">
import { computed, ref } from "vue"
import { Funnel, Plus, Search, SlidersHorizontal } from "lucide-vue-next"
import type {
  CategoryResponse,
  ProductDisplayStatus,
  ProductType,
  TagResponse,
  TargetGender,
  VariantStockStatus,
} from "@/modules/product/types"

interface Option<T = string> {
  label: string
  value: T
}

const props = defineProps<{
  filters: {
    keyword: string
    categoryIds: number[]
    tagIds: number[]
    displayStatus: ProductDisplayStatus | ""
    productType: ProductType | ""
    targetGender: TargetGender | ""
    stockStatus: VariantStockStatus | ""
    brand: string
    material: string
    inStock: "" | "true" | "false"
    hideOutOfStock: boolean
    sort: string
    releaseYear: number | null
    minPrice: number | null
    maxPrice: number | null
    createdWithinDays: number | null
    categoryMatchAll: boolean
  }
  categoryItems: CategoryResponse[]
  tagItems: TagResponse[]
  displayStatusOptions: Option<ProductDisplayStatus>[]
  productTypes: Option<ProductType>[]
  genders: Option<TargetGender>[]
  stockStatusOptions: Option<VariantStockStatus>[]
  sortOptions: Option[]
}>()

const emit = defineEmits<{
  (e: "apply"): void
  (e: "reset"): void
  (e: "create-product"): void
}>()

const showAdvanced = ref(false)

const activeFilterCount = computed(() => {
  let count = 0
  if (props.filters.keyword) count++
  if (props.filters.displayStatus) count++
  if (props.filters.productType) count++
  if (props.filters.targetGender) count++
  if (props.filters.stockStatus) count++
  if (props.filters.brand) count++
  if (props.filters.material) count++
  if (props.filters.inStock) count++
  if (props.filters.hideOutOfStock) count++
  if (props.filters.releaseYear) count++
  if (props.filters.minPrice !== null) count++
  if (props.filters.maxPrice !== null) count++
  if (props.filters.createdWithinDays !== null) count++
  if (props.filters.categoryIds.length) count++
  if (props.filters.tagIds.length) count++
  if (props.filters.categoryMatchAll) count++
  if (props.filters.sort && props.filters.sort !== "newest") count++
  return count
})
</script>

<template>
  <div class="rounded-3xl border border-slate-200 bg-white p-4 shadow-sm md:p-5">
    <div class="flex flex-col gap-4">
      <div class="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <div class="flex items-center gap-2 text-sm font-semibold text-slate-900">
            <Funnel :size="16" />
            Bộ lọc sản phẩm
          </div>
          <p class="mt-1 text-xs text-slate-500">
            Tìm nhanh sản phẩm theo tên, SKU, trạng thái và các thuộc tính khác.
          </p>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            @click="showAdvanced = !showAdvanced"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
          >
            <SlidersHorizontal :size="16" />
            {{ showAdvanced ? "Ẩn bộ lọc nâng cao" : "Bộ lọc nâng cao" }}
            <span
              v-if="activeFilterCount > 0"
              class="inline-flex min-w-5 items-center justify-center rounded-full bg-slate-900 px-1.5 py-0.5 text-[11px] text-white"
            >
              {{ activeFilterCount }}
            </span>
          </button>

          <button
            @click="emit('create-product')"
            class="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-emerald-700"
          >
            <Plus :size="16" />
            Thêm sản phẩm
          </button>
        </div>
      </div>

      <div class="grid gap-3 lg:grid-cols-[1.6fr,1fr,1fr,auto]">
        <label class="relative block">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" :size="16" />
          <input
            v-model="props.filters.keyword"
            class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-10 py-3 text-sm outline-none transition focus:border-slate-400 focus:bg-white"
            placeholder="Tìm theo tên sản phẩm hoặc SKU..."
          />
        </label>

        <select
          v-model="props.filters.displayStatus"
          class="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-3 text-sm outline-none transition focus:border-slate-400 focus:bg-white"
        >
          <option value="">Trạng thái hiển thị</option>
          <option v-for="item in props.displayStatusOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>

        <select
          v-model="props.filters.productType"
          class="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-3 text-sm outline-none transition focus:border-slate-400 focus:bg-white"
        >
          <option value="">Loại sản phẩm</option>
          <option v-for="item in props.productTypes" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>

        <button
          @click="emit('apply')"
          class="rounded-2xl bg-slate-900 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800"
        >
          Áp dụng
        </button>
      </div>

      <div v-if="showAdvanced" class="rounded-2xl border border-slate-200 bg-slate-50/70 p-4">
        <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
          <select
            v-model="props.filters.targetGender"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
          >
            <option value="">Đối tượng</option>
            <option v-for="item in props.genders" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>

          <select
            v-model="props.filters.stockStatus"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
          >
            <option value="">Trạng thái tồn kho</option>
            <option v-for="item in props.stockStatusOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>

          <select
            v-model="props.filters.inStock"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
          >
            <option value="">Còn hàng / hết hàng</option>
            <option value="true">Chỉ còn hàng</option>
            <option value="false">Chỉ hết hàng</option>
          </select>

          <select
            v-model="props.filters.sort"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
          >
            <option v-for="item in props.sortOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>

          <input
            v-model="props.filters.brand"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Thương hiệu"
          />
          <input
            v-model="props.filters.material"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Chất liệu"
          />
          <input
            v-model.number="props.filters.releaseYear"
            type="number"
            min="2000"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Năm ra mắt"
          />
          <input
            v-model.number="props.filters.createdWithinDays"
            type="number"
            min="1"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Tạo trong X ngày"
          />

          <input
            v-model.number="props.filters.minPrice"
            type="number"
            min="0"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Giá từ"
          />
          <input
            v-model.number="props.filters.maxPrice"
            type="number"
            min="0"
            class="rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            placeholder="Giá đến"
          />
        </div>

        <div class="mt-4 grid gap-3 lg:grid-cols-2">
          <label class="space-y-2">
            <span class="text-xs font-medium text-slate-500">Lọc theo danh mục</span>
            <select
              v-model="props.filters.categoryIds"
              multiple
              class="min-h-32 w-full rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            >
              <option v-for="category in props.categoryItems" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </label>

          <label class="space-y-2">
            <span class="text-xs font-medium text-slate-500">Lọc theo tag</span>
            <select
              v-model="props.filters.tagIds"
              multiple
              class="min-h-32 w-full rounded-2xl border border-slate-200 bg-white px-3 py-3 text-sm outline-none"
            >
              <option v-for="tag in props.tagItems" :key="tag.id" :value="tag.id">
                {{ tag.name }}
              </option>
            </select>
          </label>
        </div>

        <div class="mt-4 flex flex-wrap items-center gap-3">
          <label class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700">
            <input v-model="props.filters.hideOutOfStock" type="checkbox" />
            Ẩn sản phẩm hết hàng
          </label>

          <label class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700">
            <input v-model="props.filters.categoryMatchAll" type="checkbox" />
            Khớp tất cả danh mục
          </label>

          <button
            @click="emit('reset')"
            class="rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
          >
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>
  </div>
</template>