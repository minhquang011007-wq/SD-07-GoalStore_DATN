<template>
  <table class="table">
    <thead>
      <tr>
        <th>ID</th>
        <th>Khách hàng</th>
        <th>Kênh</th>
        <th>Ghi chú</th>
        <th>Ngày gửi</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.ten }}</td>
        <td>{{ item.channel }}</td>
        <td>{{ item.note }}</td>
        <td>{{ item.sentDate }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { BirthdayLog } from '../types/loyalty'

defineProps<{
  data: BirthdayLog[]
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