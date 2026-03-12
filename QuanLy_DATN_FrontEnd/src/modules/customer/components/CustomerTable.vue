// CustomerTable.vue
<template>
  <table class="table">
    <thead>
      <tr>
        <th>ID</th>
        <th>Tên</th>
        <th>Email</th>
        <th>SĐT</th>
        <th>Loại khách</th>
        <th>Điểm</th>
        <th>Ngày tạo</th>
        <th>Hành động</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="customer in customers" :key="customer.id">
        <td>{{ customer.id }}</td>
        <td>{{ customer.ten }}</td>
        <td>{{ customer.email }}</td>
        <td>{{ customer.sdt }}</td>
        <td>{{ customer.loaiKhach }}</td>
        <td>{{ customer.diemTichLuy }}</td>
        <td>{{ customer.createdAt }}</td>

        <td class="actions">
          <button @click="$emit('view', customer)">Xem</button>
          <button @click="$emit('edit', customer)">Sửa</button>
          <button class="delete" @click="$emit('delete', customer.id)">Xóa</button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { Customer } from "../types/customer"

defineProps<{
  customers: Customer[]
}>()

defineEmits(["view", "edit", "delete"])
</script>

<style scoped>
.table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th,
td {
  border: 1px solid #ddd;
  padding: 10px;
}

th {
  background: #f3f4f6;
}

.actions {
  display: flex;
  gap: 6px;
}

button {
  padding: 6px 10px;
  border: none;
  background: #2563eb;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

button.delete {
  background: #dc2626;
}
</style>