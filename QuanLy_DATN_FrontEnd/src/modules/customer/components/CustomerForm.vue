
// CustomerForm.vue

<template>
  <form class="form" @submit.prevent="submitForm">
    <div class="grid">
      <div>
        <label>Tên</label>
        <input v-model="form.ten" required />
      </div>

      <div>
        <label>Email</label>
        <input v-model="form.email" />
      </div>

      <div>
        <label>SĐT</label>
        <input v-model="form.sdt" />
      </div>

      <div>
        <label>Ngày sinh</label>
        <input v-model="form.ngaySinh" type="date" />
      </div>

      <div>
        <label>Loại khách</label>
        <select v-model="form.loaiKhach">
          <option value="THUONG">THƯỜNG</option>
          <option value="VIP">VIP</option>
        </select>
      </div>

      <div>
        <label>Điểm tích lũy</label>
        <input v-model.number="form.diemTichLuy" type="number" />
      </div>
    </div>

    <div>
      <label>Ghi chú</label>
      <textarea v-model="form.ghiChu"></textarea>
    </div>

    <button type="submit">Lưu</button>
  </form>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { Customer } from '../types/customer'

const props = defineProps<{
  modelValue?: Customer | null
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Customer): void
}>()

const form = reactive<Customer>({
  ten: '',
  email: '',
  sdt: '',
  ngaySinh: null,
  loaiKhach: 'THUONG',
  diemTichLuy: 0,
  ghiChu: ''
})

watch(
  () => props.modelValue,
  (value) => {
    if (value) {
      form.id = value.id
      form.ten = value.ten || ''
      form.email = value.email || ''
      form.sdt = value.sdt || ''
      form.ngaySinh = value.ngaySinh || null
      form.loaiKhach = value.loaiKhach || 'THUONG'
      form.diemTichLuy = value.diemTichLuy || 0
      form.ghiChu = value.ghiChu || ''
    }
  },
  { immediate: true }
)

const submitForm = () => {
  emit('submit', { ...form })
}
</script>

<style scoped>
.form {
  background: white;
  padding: 20px;
  border-radius: 10px;
}
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
}
input, select, textarea {
  width: 100%;
  padding: 10px;
}
textarea {
  min-height: 100px;
  margin-bottom: 16px;
}
button {
  padding: 10px 16px;
}
</style>