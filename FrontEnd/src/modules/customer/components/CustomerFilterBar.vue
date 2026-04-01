<template>
  <div class="rounded-2xl border bg-white p-4 shadow-sm">
    <div class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-4">
      <input
        v-model="localFilter.keyword"
        type="text"
        placeholder="Tìm theo tên / email / số điện thoại"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />

      <select
        v-model="localFilter.loai_khach"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      >
        <option value="">Tất cả loại khách</option>
        <option value="THUONG">Khách thường</option>
        <option value="VIP">VIP</option>
      </select>

      <input
        v-model.number="localFilter.inactiveDays"
        type="number"
        min="0"
        placeholder="Không mua từ bao nhiêu ngày"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />

      <select
        v-model="localFilter.sortBy"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      >
        <option value="newest">Mới nhất</option>
        <option value="name">Tên A-Z</option>
        <option value="points-desc">Điểm giảm dần</option>
        <option value="spending-desc">Chi tiêu giảm dần</option>
        <option value="inactive-desc">Không mua lâu nhất</option>
      </select>

      <input
        v-model.number="localFilter.minPoints"
        type="number"
        min="0"
        placeholder="Điểm từ"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />

      <input
        v-model.number="localFilter.maxPoints"
        type="number"
        min="0"
        placeholder="Điểm đến"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />

      <input
        v-model.number="localFilter.minSpending"
        type="number"
        min="0"
        placeholder="Chi tiêu từ"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />

      <input
        v-model.number="localFilter.maxSpending"
        type="number"
        min="0"
        placeholder="Chi tiêu đến"
        class="rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
      />
    </div>

    <div class="mt-4 flex flex-wrap gap-2">
      <button
        type="button"
        class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-semibold text-white hover:bg-emerald-700"
        @click="$emit('apply', normalizeFilter(localFilter))"
      >
        Áp dụng
      </button>

      <button
        type="button"
        class="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
        @click="resetFilter"
      >
        Đặt lại
      </button>

      <button
        type="button"
        class="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
        @click="$emit('create')"
      >
        Thêm khách hàng
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from "vue";
import type { CustomerFilter } from "../types/customerFilter";

const props = defineProps<{
  modelValue: CustomerFilter;
}>();

const emit = defineEmits<{
  (e: "apply", value: CustomerFilter): void;
  (e: "create"): void;
}>();

const localFilter = reactive<CustomerFilter>({ ...props.modelValue });

watch(
  () => props.modelValue,
  (value) => {
    Object.assign(localFilter, value);
  },
  { deep: true }
);

function normalizeFilter(filter: CustomerFilter): CustomerFilter {
  return {
    keyword: filter.keyword || "",
    loai_khach: filter.loai_khach || "",
    inactiveDays: Number(filter.inactiveDays || 0),
    minPoints: filter.minPoints ?? null,
    maxPoints: filter.maxPoints ?? null,
    minSpending: filter.minSpending ?? null,
    maxSpending: filter.maxSpending ?? null,
    sortBy: filter.sortBy || "newest",
  };
}

function resetFilter() {
  const value: CustomerFilter = {
    keyword: "",
    loai_khach: "",
    inactiveDays: 0,
    minPoints: null,
    maxPoints: null,
    minSpending: null,
    maxSpending: null,
    sortBy: "newest",
  };

  Object.assign(localFilter, value);
  emit("apply", value);
}
</script>