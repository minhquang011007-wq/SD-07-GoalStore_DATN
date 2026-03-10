<script setup lang="ts">
import { ImagePlus } from "lucide-vue-next"
import type { ProductImageResponse } from "@/modules/product/types"

defineProps<{
  items: ProductImageResponse[]
  uploading: boolean
  normalizeAsset: (url?: string | null) => string
}>()

const emit = defineEmits<{
  (e: "upload", event: Event): void
  (e: "avatar", id: number): void
  (e: "delete", id: number): void
}>()
</script>

<template>
  <div class="space-y-3">
    <div class="flex items-center justify-between">
      <div class="text-sm font-medium text-slate-700">Ảnh sản phẩm</div>
      <label class="inline-flex cursor-pointer items-center gap-2 rounded-xl border px-3 py-2 text-xs font-medium hover:bg-slate-50">
        <ImagePlus class="h-3.5 w-3.5" />
        {{ uploading ? 'Đang upload...' : 'Thêm ảnh' }}
        <input type="file" multiple class="hidden" @change="emit('upload', $event)" />
      </label>
    </div>
    <div class="grid grid-cols-2 gap-3">
      <div v-for="image in items" :key="image.id" class="rounded-2xl border p-2">
        <img :src="normalizeAsset(image.imageUrl)" class="h-28 w-full rounded-xl object-cover" alt="product image" />
        <div class="mt-2 flex items-center justify-between text-xs text-slate-500">
          <span>{{ image.avatar ? 'Ảnh đại diện' : `Thứ tự: ${image.sortOrder}` }}</span>
          <div class="flex gap-2">
            <button class="rounded-lg border px-2 py-1" @click="emit('avatar', image.id)">Đặt avatar</button>
            <button class="rounded-lg border border-red-200 px-2 py-1 text-red-600" @click="emit('delete', image.id)">Xóa</button>
          </div>
        </div>
      </div>
      <div v-if="!items.length" class="col-span-2 rounded-2xl border border-dashed p-5 text-center text-sm text-slate-500">Chưa có ảnh nào cho sản phẩm này</div>
    </div>
  </div>
</template>
