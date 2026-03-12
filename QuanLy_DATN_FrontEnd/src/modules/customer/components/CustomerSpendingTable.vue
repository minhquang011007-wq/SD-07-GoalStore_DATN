// CustomerSpendingTable.vue
<template>
  <table class="table">
    <thead>
      <tr>
        <th>Khách hàng</th>
        <th>Email</th>
        <th>SĐT</th>
        <th>Loại khách</th>
        <th>Điểm</th>
        <th>Tổng chi tiêu</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.customerId">
        <td>{{ item.ten }}</td>
        <td>{{ item.email }}</td>
        <td>{{ item.sdt }}</td>
        <td>{{ item.loaiKhach }}</td>
        <td>{{ item.diemTichLuy }}</td>
        <td>{{ item.tongChiTieu }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { CustomerSpending } from '../types/customer'

defineProps<{
  data: CustomerSpending[]
}>()
</script>

<style scoped>
.table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}
th, td {
  border: 1px solid #ddd;
  padding: 10px;
}
</style>