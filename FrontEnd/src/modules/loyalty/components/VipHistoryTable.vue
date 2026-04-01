<template>
  <table class="table">
    <thead>
      <tr>
        <th>ID</th>
        <th>Old Level</th>
        <th>New Level</th>
        <th>Reason</th>
        <th>Changed At</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in data" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.oldLevel }}</td>
        <td>{{ item.newLevel }}</td>
        <td>{{ item.reason }}</td>
        <td>{{ item.changedAt }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { VipHistory } from '../types/loyalty'

defineProps<{
  data: VipHistory[]
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