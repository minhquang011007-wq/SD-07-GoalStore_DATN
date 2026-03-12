// InactiveCustomerTable.vue
<template>
  <table class="table">
    <thead>
      <tr>
        <th>Khách hàng</th>
        <th>Email</th>
        <th>SĐT</th>
        <th>Ngày mua cuối</th>
        <th>Số ngày không mua</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.customerId">
        <td>{{ item.ten }}</td>
        <td>{{ item.email }}</td>
        <td>{{ item.sdt }}</td>
        <td>{{ item.lastOrderDate }}</td>
        <td>{{ item.soNgayKhongMua }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { InactiveCustomer } from '../types/customer'

defineProps<{
  data: InactiveCustomer[]
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