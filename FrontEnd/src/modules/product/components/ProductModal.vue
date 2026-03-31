<script setup lang="ts">
import { LoaderCircle, X } from "lucide-vue-next"
import type { CategoryResponse, ProductDisplayStatus, ProductRequest, ProductType, TagResponse, TargetGender } from "@/modules/product/types"

interface Option<T = string> {
  label: string
  value: T
}

const props = defineProps<{
  open: boolean
  editingProductId: number | null
  form: ProductRequest
  categoryItems: CategoryResponse[]
  tagItems: TagResponse[]
  savingProduct: boolean
  productTypes: Option<ProductType>[]
  genders: Option<TargetGender>[]
  displayStatusOptions: Option<ProductDisplayStatus>[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit'): void
}>()
</script>

<template>
  <div v-if="props.open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
    <div class="max-h-[95vh] w-full max-w-3xl overflow-auto rounded-2xl bg-white p-6 shadow-2xl">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold">{{ props.editingProductId ? 'Sửa sản phẩm' : 'Thêm sản phẩm' }}</h3>
        <button @click="emit('close')" class="rounded-lg border p-2"><X :size="16" /></button>
      </div>
      <div class="mt-4 grid gap-4 md:grid-cols-2">
        <label class="space-y-2"><span class="text-sm">Tên sản phẩm *</span><input v-model="props.form.name" class="w-full rounded-xl border px-3 py-2" placeholder="Ví dụ: Áo đấu CLB Arsenal" /></label>
        <label class="space-y-2"><span class="text-sm">SKU gốc *</span><input v-model="props.form.baseSku" class="w-full rounded-xl border px-3 py-2" placeholder="VD: ARS-2026-HOME" /></label>
        <label class="space-y-2"><span class="text-sm">Thương hiệu</span><input v-model="props.form.brand" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Mùa giải</span><input v-model="props.form.season" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Loại sản phẩm *</span><select v-model="props.form.productType" class="w-full rounded-xl border px-3 py-2"><option v-for="item in props.productTypes" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
        <label class="space-y-2"><span class="text-sm">Đối tượng *</span><select v-model="props.form.targetGender" class="w-full rounded-xl border px-3 py-2"><option v-for="item in props.genders" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
        <label class="space-y-2"><span class="text-sm">Chất liệu</span><input v-model="props.form.material" class="w-full rounded-xl border px-3 py-2" /></label>
        <label class="space-y-2"><span class="text-sm">Năm ra mắt</span><input v-model.number="props.form.releaseYear" type="number" class="w-full rounded-xl border px-3 py-2" min="2000" /></label>
        <label class="space-y-2 md:col-span-2"><span class="text-sm">Mô tả</span><textarea v-model="props.form.description" rows="3" class="w-full rounded-xl border px-3 py-2"></textarea></label>
        <label class="space-y-2"><span class="text-sm">Trạng thái hiển thị *</span><select v-model="props.form.displayStatus" class="w-full rounded-xl border px-3 py-2"><option v-for="item in props.displayStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
        <label class="space-y-2"><span class="text-sm">Danh mục</span><select v-model="props.form.categoryIds" multiple class="min-h-32 w-full rounded-xl border px-3 py-2"><option v-for="item in props.categoryItems" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
        <label class="space-y-2 md:col-span-2"><span class="text-sm">Thẻ</span><select v-model="props.form.tagIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2"><option v-for="item in props.tagItems" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
      </div>
      <div class="mt-6 flex justify-end gap-3">
        <button @click="emit('close')" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
        <button @click="emit('submit')" :disabled="props.savingProduct" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">
          <span class="inline-flex items-center gap-2">
            <LoaderCircle v-if="props.savingProduct" class="animate-spin" :size="16" />
            {{ props.editingProductId ? 'Lưu thay đổi' : 'Tạo sản phẩm' }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>
