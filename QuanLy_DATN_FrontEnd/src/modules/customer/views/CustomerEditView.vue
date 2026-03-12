// CustomerEditView.vue
<template>
  <section>
    <h1>Sửa khách hàng</h1>
    <CustomerForm :model-value="customer" @submit="handleSubmit" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CustomerForm from '../components/CustomerForm.vue'
import type { Customer } from '../types/customer'
import { getCustomerById, updateCustomer } from '../services/customerApi'

const route = useRoute()
const router = useRouter()
const customer = ref<Customer | null>(null)

const id = Number(route.params.id)

onMounted(async () => {
  customer.value = await getCustomerById(id)
})

const handleSubmit = async (payload: Customer) => {
  await updateCustomer(id, payload)
  router.push('/sales/customer-loyalty/customers')
}
</script>