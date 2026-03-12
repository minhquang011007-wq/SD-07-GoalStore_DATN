<template>
  <table class="table">
    <thead>
      <tr>
        <th>Tên ưu đãi</th>
        <th>Điểm yêu cầu</th>
        <th>Giá trị giảm</th>
        <th>Mô tả</th>
        <th>Active</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.id">
        <td>{{ item.rewardName }}</td>
        <td>{{ item.requiredPoints }}</td>
        <td>{{ item.discountValue }}</td>
        <td>{{ item.description }}</td>
        <td>{{ item.isActive ? 'Yes' : 'No' }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { RewardRule } from '../types/loyalty'

defineProps<{
  data: RewardRule[]
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