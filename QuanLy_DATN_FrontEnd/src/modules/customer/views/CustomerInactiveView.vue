// CustomerInactiveView.vue
<template>
  <section>
    <div class="head">
      <h1>Khách hàng lâu chưa mua</h1>
      <div>
        <input v-model.number="days" type="number" min="1" />
        <button @click="loadData">Lọc</button>
      </div>
    </div>

    <InactiveCustomerTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import InactiveCustomerTable from '../components/InactiveCustomerTable.vue'
import type { InactiveCustomer } from '../types/customer'
import { getInactiveCustomers } from '../services/customerApi'

const days = ref(30)
const data = ref<InactiveCustomer[]>([])

const loadData = async () => {
  data.value = await getInactiveCustomers(days.value)
}

onMounted(loadData)
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
input {
  padding: 8px;
  width: 100px;
}
button {
  margin-left: 8px;
  padding: 8px 12px;
}
</style>