<template>
  <div class="rounded-2xl border bg-white p-5 shadow-xl">
    <div class="mb-4 flex items-center justify-between">
      <h3 class="text-xl font-bold text-slate-800">
        {{ localCustomer.id ? "Cập nhật khách hàng" : "Thêm khách hàng" }}
      </h3>
      <button
        type="button"
        class="rounded-lg px-3 py-1 text-sm font-semibold text-slate-500 hover:bg-slate-100"
        @click="$emit('cancel')"
      >
        Đóng
      </button>
    </div>

    <form class="space-y-4" @submit.prevent="submitForm">
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Tên khách hàng</label>
          <input
            v-model="localCustomer.ten"
            type="text"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
            placeholder="Nhập tên khách hàng"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Email</label>
          <input
            v-model="localCustomer.email"
            type="email"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
            placeholder="Nhập email"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Số điện thoại</label>
          <input
            v-model="localCustomer.sdt"
            type="text"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
            placeholder="Nhập số điện thoại"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Ngày sinh</label>
          <input
            v-model="localCustomer.ngay_sinh"
            type="date"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Loại khách</label>
          <select
            v-model="localCustomer.loai_khach"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
          >
            <option value="THUONG">THƯỜNG</option>
            <option value="VIP">VIP</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Điểm tích lũy</label>
          <input
            v-model.number="localCustomer.diem_tich_luy"
            type="number"
            min="0"
            class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
          />
        </div>
      </div>

      <div>
        <label class="mb-1 block text-sm font-medium text-slate-700">Ghi chú</label>
        <textarea
          v-model="localCustomer.ghi_chu"
          rows="4"
          class="w-full rounded-xl border px-4 py-2.5 outline-none focus:border-emerald-500"
          placeholder="Nhập ghi chú"
        />
      </div>

      <div class="flex justify-end gap-2">
        <button
          type="button"
          class="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
          @click="$emit('cancel')"
        >
          Hủy
        </button>

        <button
          type="submit"
          class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-semibold text-white hover:bg-emerald-700"
        >
          {{ localCustomer.id ? "Lưu thay đổi" : "Tạo mới" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from "vue";
import type { Customer } from "../types/customer";
import { defaultCustomer } from "../types/customer";

const props = defineProps<{
  modelValue: Customer;
}>();

const emit = defineEmits<{
  (e: "save", value: Customer): void;
  (e: "cancel"): void;
}>();

const localCustomer = reactive<Customer>({ ...defaultCustomer });

watch(
  () => props.modelValue,
  (value) => {
    Object.assign(localCustomer, { ...defaultCustomer, ...value });
  },
  { immediate: true, deep: true }
);

function submitForm() {
  if (!String(localCustomer.ten || "").trim()) {
    alert("Tên khách hàng không được để trống");
    return;
  }

  emit("save", {
    ...localCustomer,
    ten: String(localCustomer.ten || "").trim(),
    email: String(localCustomer.email || "").trim(),
    sdt: String(localCustomer.sdt || "").trim(),
    ghi_chu: String(localCustomer.ghi_chu || "").trim(),
    diem_tich_luy: Number(localCustomer.diem_tich_luy || 0),
    loai_khach: localCustomer.loai_khach || "THUONG",
  });
}
</script>