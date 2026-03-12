<template>
  <table class="table">
    <thead>
      <tr>
        <th>Level</th>
        <th>Min Points</th>
        <th>Min Spending</th>
        <th>Benefit</th>
        <th>Active</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.id">
        <td>{{ item.levelName }}</td>
        <td>{{ item.minPoints }}</td>
        <td>{{ item.minSpending }}</td>
        <td>{{ item.benefit }}</td>
        <td>{{ item.isActive ? 'Yes' : 'No' }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { VipProgram } from '../types/loyalty'

defineProps<{
  data: VipProgram[]
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