<template>
  <section>
    <h1>Danh sách Loyalty</h1>

    <LoyaltyCustomerTable
      :data="data"
      @add="openAdd"
      @deduct="openDeduct"
    />

    <AddPointModal
      v-if="showAdd"
      :customer-id="selectedCustomerId"
      @close="showAdd = false"
      @save="handleAdd"
    />

    <DeductPointModal
      v-if="showDeduct"
      :customer-id="selectedCustomerId"
      @close="showDeduct = false"
      @save="handleDeduct"
    />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AddPointModal from '../components/AddPointModal.vue'
import DeductPointModal from '../components/DeductPointModal.vue'
import LoyaltyCustomerTable from '../components/LoyaltyCustomerTable.vue'
import type { LoyaltyCustomer } from '../types/loyalty'
import { addPoint, deductPoint, getLoyaltyCustomers } from '../services/loyaltyApi'

const data = ref<LoyaltyCustomer[]>([])
const showAdd = ref(false)
const showDeduct = ref(false)
const selectedCustomerId = ref(0)

const loadData = async () => {
  data.value = await getLoyaltyCustomers()
}

const openAdd = (customerId: number) => {
  selectedCustomerId.value = customerId
  showAdd.value = true
}

const openDeduct = (customerId: number) => {
  selectedCustomerId.value = customerId
  showDeduct.value = true
}

const handleAdd = async (payload: { customerId: number; points: number; note: string }) => {
  await addPoint(payload)
  showAdd.value = false
  await loadData()
}

const handleDeduct = async (payload: { customerId: number; points: number; note: string }) => {
  await deductPoint(payload)
  showDeduct.value = false
  await loadData()
}

onMounted(loadData)
</script>