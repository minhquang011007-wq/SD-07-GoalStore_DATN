// CustomerCreateView.vue
<template>
  <section>
    <h1>Thêm khách hàng</h1>
    <CustomerForm @submit="handleSubmit" />
  </section>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router"
import CustomerForm from "../components/CustomerForm.vue"
import type { Customer } from "../types/customer"
import { createCustomer } from "../services/customerApi"

const router = useRouter()

const handleSubmit = async (payload: Customer) => {
  await createCustomer(payload)
  router.push("/sales/customer-loyalty/customers")
}
</script>