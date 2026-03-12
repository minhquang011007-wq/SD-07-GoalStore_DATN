// CustomerOrdersView.vue
<template>
  <section>
    <h1>Lịch sử đơn hàng khách hàng</h1>
    <CustomerOrdersTable :orders="orders" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import CustomerOrdersTable from '../components/CustomerOrdersTable.vue'
import type { CustomerOrderHistory } from '../types/customer'
import { getCustomerOrders } from '../services/customerApi'

const route = useRoute()
const orders = ref<CustomerOrderHistory[]>([])

onMounted(async () => {
  orders.value = await getCustomerOrders(Number(route.params.id))
})
</script>