// CustomerOrdersTable.vue
<template>
  <table class="table">
    <thead>
      <tr>
        <th>Mã đơn</th>
        <th>Ngày đặt</th>
        <th>Trạng thái</th>
        <th>Thanh toán</th>
        <th>Kênh</th>
        <th>Tổng tiền</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in orders" :key="item.orderId">
        <td>{{ item.orderId }}</td>
        <td>{{ item.orderDate }}</td>
        <td>{{ item.status }}</td>
        <td>{{ item.paymentMethod }}</td>
        <td>{{ item.channel }}</td>
        <td>{{ item.total }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { CustomerOrderHistory } from '../types/customer'

defineProps<{
  orders: CustomerOrderHistory[]
}>()
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
  text-align: left;
}
</style>