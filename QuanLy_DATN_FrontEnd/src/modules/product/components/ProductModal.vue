<script setup lang="ts">
import { X } from "lucide-vue-next"
import type { CategoryResponse, ProductDisplayStatus, ProductRequest, ProductType, TagResponse, TargetGender } from "@/modules/product/types"

defineProps<{
  open: boolean
  saving: boolean
  editingProductId: number | null
  form: ProductRequest
  categories: CategoryResponse[]
  tags: TagResponse[]
  productTypes: Array<{ label: string; value: ProductType }>
  genders: Array<{ label: string; value: TargetGender }>
  displayStatusOptions: Array<{ label: string; value: ProductDisplayStatus }>
}>()

const emit = defineEmits<{ (e: "close"): void; (e: "submit"): void }>()

function toggle(list: number[], id: number) {
  const index = list.indexOf(id)
  if (index >= 0) list.splice(index, 1)
  else list.push(id)
}
</script>

<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
    <div class="max-h-[90vh] w-full max-w-4xl overflow-y-auto rounded-3xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b px-6 py-4">
        <h3 class="text-lg font-semibold text-slate-900">{{ editingProductId ? 'Sửa sản phẩm' : 'Tạo sản phẩm mới' }}</h3>
        <button class="rounded-full border p-2" @click="emit('close')"><X class="h-4 w-4" /></button>
      </div>
      <div class="grid gap-4 p-6 md:grid-cols-2">
        <label><span class="mb-1 block text-sm font-medium">Tên sản phẩm</span><input v-model="form.name" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">SKU chuẩn</span><input v-model="form.baseSku" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">Brand</span><input v-model="form.brand" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">Season</span><input v-model="form.season" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">Loại sản phẩm</span><select v-model="form.productType" class="w-full rounded-xl border px-3 py-2.5 text-sm"><option v-for="type in productTypes" :key="type.value" :value="type.value">{{ type.label }}</option></select></label>
        <label><span class="mb-1 block text-sm font-medium">Đối tượng</span><select v-model="form.targetGender" class="w-full rounded-xl border px-3 py-2.5 text-sm"><option v-for="gender in genders" :key="gender.value" :value="gender.value">{{ gender.label }}</option></select></label>
        <label><span class="mb-1 block text-sm font-medium">Chất liệu</span><input v-model="form.material" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label><span class="mb-1 block text-sm font-medium">Năm phát hành</span><input v-model.number="form.releaseYear" type="number" class="w-full rounded-xl border px-3 py-2.5 text-sm" /></label>
        <label class="md:col-span-2"><span class="mb-1 block text-sm font-medium">Trạng thái hiển thị</span><select v-model="form.displayStatus" class="w-full rounded-xl border px-3 py-2.5 text-sm"><option v-for="status in displayStatusOptions" :key="status.value" :value="status.value">{{ status.label }}</option></select></label>
        <label class="md:col-span-2"><span class="mb-1 block text-sm font-medium">Mô tả</span><textarea v-model="form.description" rows="4" class="w-full rounded-xl border px-3 py-2.5 text-sm"></textarea></label>
        <div>
          <div class="mb-2 text-sm font-medium">Category</div>
          <div class="max-h-48 space-y-2 overflow-auto rounded-2xl border p-3">
            <label v-for="category in categories" :key="category.id" class="flex items-center gap-2 text-sm"><input type="checkbox" :checked="form.categoryIds.includes(category.id)" @change="toggle(form.categoryIds, category.id)" /> {{ category.name }}</label>
          </div>
        </div>
        <div>
          <div class="mb-2 text-sm font-medium">Tag</div>
          <div class="max-h-48 space-y-2 overflow-auto rounded-2xl border p-3">
            <label v-for="tag in tags" :key="tag.id" class="flex items-center gap-2 text-sm"><input type="checkbox" :checked="form.tagIds.includes(tag.id)" @change="toggle(form.tagIds, tag.id)" /> {{ tag.name }}</label>
          </div>
        </div>
      </div>
      <div class="flex justify-end gap-3 border-t px-6 py-4">
        <button class="rounded-xl border px-4 py-2 text-sm font-medium" @click="emit('close')">Đóng</button>
        <button class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white" :disabled="saving" @click="emit('submit')">{{ saving ? 'Đang lưu...' : editingProductId ? 'Lưu thay đổi' : 'Tạo sản phẩm' }}</button>
      </div>
    </div>
  </div>
</template>
