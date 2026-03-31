<script setup lang="ts">
import { Upload } from "lucide-vue-next"
import type { ProductImageResponse } from "@/modules/product/types"
import { resolveProductImageUrl } from "@/modules/product/utils/image"

const props = defineProps<{
  images: ProductImageResponse[]
  uploading: boolean
}>()

const emit = defineEmits<{
  (e: 'upload', event: Event): void
  (e: 'set-avatar', image: ProductImageResponse): void
  (e: 'remove', id: number): void
  (e: 'image-order-change', payload: { image: ProductImageResponse; event: Event }): void
}>()
</script>

<template>
  <div>
    <div class="mb-2 flex items-center justify-between">
      <h4 class="font-semibold text-slate-900">Ảnh sản phẩm</h4>
      <label class="inline-flex cursor-pointer items-center gap-2 rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
        <Upload :size="14" />
        {{ props.uploading ? 'Đang tải ảnh lên...' : 'Tải ảnh lên' }}
        <input type="file" class="hidden" accept="image/*" multiple @change="emit('upload', $event)" />
      </label>
    </div>
    <div class="grid gap-3 sm:grid-cols-2">
      <div v-for="image in props.images" :key="image.id" class="rounded-xl border p-3">
        <img :src="resolveProductImageUrl(image.imageUrl)" class="h-32 w-full rounded-xl border bg-slate-50 object-cover" />
        <div class="mt-3 flex items-center justify-between gap-2 text-xs">
          <span class="rounded-full px-2 py-1" :class="image.avatar ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'">
            {{ image.avatar ? 'Ảnh đại diện' : 'Ảnh phụ' }}
          </span>
          <div class="flex gap-2">
            <button @click="emit('set-avatar', image)" class="rounded-lg border px-2 py-1 hover:bg-slate-50">Đặt làm ảnh đại diện</button>
            <button @click="emit('remove', image.id)" class="rounded-lg border px-2 py-1 text-rose-600 hover:bg-rose-50">Xóa</button>
          </div>
        </div>
        <label class="mt-2 block text-xs text-slate-500">
          Thứ tự sắp xếp
          <input :value="image.sortOrder" type="number" class="mt-1 w-full rounded-lg border px-2 py-1 text-sm" @change="emit('image-order-change', { image, event: $event })" />
        </label>
      </div>
      <div v-if="props.images.length === 0" class="rounded-xl border p-6 text-sm text-slate-500">Chưa có ảnh sản phẩm.</div>
    </div>
  </div>
</template>
