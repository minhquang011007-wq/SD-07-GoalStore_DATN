<script setup lang="ts">
import { Plus, Tags, Pencil, Trash2 } from "lucide-vue-next"
import type { TagResponse } from "@/modules/product/types"

defineProps<{
  loading?: boolean
  items: TagResponse[]
}>()

const emit = defineEmits<{
  (e: "create"): void
  (e: "edit", tag: TagResponse): void
  (e: "delete", id: number): void
}>()
</script>

<template>
  <div>
    <div class="mt-4 flex flex-wrap items-center gap-3">
      <button
        @click="emit('create')"
        class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white"
      >
        <span class="inline-flex items-center gap-2">
          <Plus :size="16" />
          Thêm tag
        </span>
      </button>
    </div>

    <div v-if="loading" class="mt-4 rounded-2xl border p-10 text-center text-sm text-slate-500">
      Đang tải tags...
    </div>

    <div v-else class="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      <div
        v-for="tag in items"
        :key="tag.id"
        class="rounded-2xl border p-4"
      >
        <div class="flex items-start justify-between gap-3">
          <div>
            <h3 class="font-semibold text-slate-900">{{ tag.name }}</h3>
            <p class="mt-1 text-sm text-slate-500">
              {{ tag.description || "Chưa có mô tả" }}
            </p>
          </div>

          <span class="rounded-full bg-slate-100 p-2">
            <Tags :size="14" />
          </span>
        </div>

        <div class="mt-3 flex gap-2">
          <button
            @click="emit('edit', tag)"
            class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50"
          >
            <span class="inline-flex items-center gap-2">
              <Pencil :size="14" />
              Sửa
            </span>
          </button>

          <button
            @click="emit('delete', tag.id)"
            class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50"
          >
            <span class="inline-flex items-center gap-2">
              <Trash2 :size="14" />
              Xóa
            </span>
          </button>
        </div>
      </div>

      <div
        v-if="items.length === 0"
        class="rounded-2xl border p-10 text-center text-sm text-slate-500"
      >
        Chưa có tag.
      </div>
    </div>
  </div>
</template>