<template>
  <table class="table">
    <thead>
      <tr>
        <th>ID</th>
        <th>Điểm</th>
        <th>Loại</th>
        <th>Ghi chú</th>
        <th>Ngày tạo</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.points }}</td>
        <td>{{ item.type }}</td>
        <td>{{ item.note }}</td>
        <td>{{ item.createdAt }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { LoyaltyHistory } from '../types/loyalty'

defineProps<{
  data: LoyaltyHistory[]
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