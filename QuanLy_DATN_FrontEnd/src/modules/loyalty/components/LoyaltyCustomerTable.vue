<template>
  <table class="table">
    <thead>
      <tr>
        <th>Khách hàng</th>
        <th>Email</th>
        <th>SĐT</th>
        <th>Điểm</th>
        <th>Loại khách</th>
        <th>Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.customerId">
        <td>{{ item.ten }}</td>
        <td>{{ item.email }}</td>
        <td>{{ item.sdt }}</td>
        <td>{{ item.diemTichLuy }}</td>
        <td>{{ item.loaiKhach }}</td>
        <td class="actions">
          <button @click="$emit('add', item.customerId)">Cộng điểm</button>
          <button @click="$emit('deduct', item.customerId)">Trừ điểm</button>
          <RouterLink :to="`/loyalty/${item.customerId}/history`">Lịch sử điểm</RouterLink>
          <RouterLink :to="`/loyalty/${item.customerId}/vip-history`">Lịch sử VIP</RouterLink>
          <RouterLink :to="`/loyalty/${item.customerId}/birthday-logs`">Sinh nhật</RouterLink>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { LoyaltyCustomer } from '../types/loyalty'

defineProps<{
  data: LoyaltyCustomer[]
}>()

defineEmits<{
  (e: 'add', customerId: number): void
  (e: 'deduct', customerId: number): void
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
.actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
button, a {
  padding: 8px 10px;
}
</style>