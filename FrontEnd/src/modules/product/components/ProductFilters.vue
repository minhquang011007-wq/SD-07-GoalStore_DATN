<script setup lang="ts">
import { Search, Plus } from "lucide-vue-next"
import type { CategoryResponse, ProductDisplayStatus, ProductType, TagResponse, TargetGender, VariantStockStatus } from "@/modules/product/types"

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
  (e: 'apply'): void
  (e: 'reset'): void
  (e: 'create-product'): void
}>()
</script>

<template>
  <div>
    <div class="mt-4 grid gap-3 md:grid-cols-2 xl:grid-cols-4">
      <label class="relative">
        <Search class="absolute left-3 top-3 text-slate-400" :size="16" />
        <input v-model="props.filters.keyword" class="w-full rounded-xl border px-9 py-2.5 text-sm" placeholder="Tên sản phẩm, SKU..." />
      </label>
      <select v-model="props.filters.displayStatus" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Trạng thái hiển thị</option>
        <option v-for="item in props.displayStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <select v-model="props.filters.productType" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Loại sản phẩm</option>
        <option v-for="item in props.productTypes" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <select v-model="props.filters.targetGender" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Đối tượng</option>
        <option v-for="item in props.genders" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <select v-model="props.filters.stockStatus" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Tồn kho</option>
        <option v-for="item in props.stockStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <select v-model="props.filters.inStock" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Có hàng / hết hàng</option>
        <option value="true">Chỉ còn hàng</option>
        <option value="false">Chỉ hết hàng</option>
      </select>
      <input v-model="props.filters.brand" class="rounded-xl border px-3 py-2.5 text-sm" placeholder="Brand" />
      <select v-model="props.filters.sort" class="rounded-xl border px-3 py-2.5 text-sm">
        <option v-for="item in props.sortOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
    </div>

    <div class="mt-3 grid gap-3 md:grid-cols-2">
      <label class="space-y-2">
        <span class="text-xs font-medium text-slate-500">Filter category</span>
        <select v-model="props.filters.categoryIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2 text-sm">
          <option v-for="category in props.categoryItems" :key="category.id" :value="category.id">{{ category.name }}</option>
        </select>
      </label>
      <label class="space-y-2">
        <span class="text-xs font-medium text-slate-500">Filter tag</span>
        <select v-model="props.filters.tagIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2 text-sm">
          <option v-for="tag in props.tagItems" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
        </select>
      </label>
    </div>

    <div class="mt-4 flex flex-wrap items-center gap-3">
      <label class="flex items-center gap-2 rounded-xl border px-3 py-2 text-sm">
        <input v-model="props.filters.hideOutOfStock" type="checkbox" /> Ẩn sản phẩm hết hàng
      </label>
      <button @click="emit('apply')" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Áp dụng filter</button>
      <button @click="emit('reset')" class="rounded-xl border px-4 py-2 text-sm font-medium">Xóa filter</button>
      <button @click="emit('create-product')" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">
        <span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm sản phẩm</span>
      </button>
    </div>
  </div>
</template>
