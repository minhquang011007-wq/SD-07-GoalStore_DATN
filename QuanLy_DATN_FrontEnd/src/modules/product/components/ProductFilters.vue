<script setup lang="ts">
import { Search } from "lucide-vue-next"

defineProps<{
  filters: {
    keyword: string
    categoryId: string
    displayStatus: string
    inStock: string
    sort: string
  }
  categoryItems: { id: number; name: string }[]
  displayStatusOptions: { label: string; value: string }[]
  sortOptions: { label: string; value: string }[]
}>()

const emit = defineEmits<{
  (e: "apply"): void
  (e: "reset"): void
  (e: "create"): void
  (e: "go-categories"): void
  (e: "go-tags"): void
}>()
</script>

<template>
  <div class="space-y-4 rounded-2xl border bg-white p-4 shadow-sm">
    <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-5">
      <label class="relative xl:col-span-2">
        <Search class="absolute left-3 top-3 text-slate-400" :size="16" />
        <input v-model="filters.keyword" class="w-full rounded-xl border px-9 py-2.5 text-sm" placeholder="Tên sản phẩm, SKU..." />
      </label>
      <select v-model="filters.categoryId" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Tất cả category</option>
        <option v-for="item in categoryItems" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
      </select>
      <select v-model="filters.displayStatus" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Trạng thái hiển thị</option>
        <option v-for="item in displayStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <select v-model="filters.sort" class="rounded-xl border px-3 py-2.5 text-sm">
        <option v-for="item in sortOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
    </div>

    <div class="flex flex-wrap items-center gap-3">
      <select v-model="filters.inStock" class="rounded-xl border px-3 py-2.5 text-sm">
        <option value="">Tồn kho</option>
        <option value="true">Chỉ còn hàng</option>
        <option value="false">Chỉ hết hàng</option>
      </select>
      <button @click="emit('apply')" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Áp dụng</button>
      <button @click="emit('reset')" class="rounded-xl border px-4 py-2 text-sm font-medium">Xóa filter</button>
      <button @click="emit('create')" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">Thêm sản phẩm</button>
      <button @click="emit('go-categories')" class="rounded-xl border px-4 py-2 text-sm font-medium">Category</button>
      <button @click="emit('go-tags')" class="rounded-xl border px-4 py-2 text-sm font-medium">Tag</button>
    </div>
  </div>
</template>
