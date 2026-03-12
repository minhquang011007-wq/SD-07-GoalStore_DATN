<template>
  <section>
    <div class="head">
      <h1>Danh sách khách hàng</h1>
      <RouterLink to="/sales/customer-loyalty/customers/create" class="btn">
        + Thêm khách
      </RouterLink>
    </div>

    <div class="toolbar">
      <CustomerSearch @search="handleSearch" />
      <CustomerFilter @filter="handleFilter" />
      <button @click="loadData">Tải lại</button>
    </div>

    <CustomerTable
      :customers="customers"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import CustomerSearch from "../components/CustomerSearch.vue"
import CustomerFilter from "../components/CustomerFilter.vue"
import CustomerTable from "../components/CustomerTable.vue"
import type { Customer } from "../types/customer"
import {
  deleteCustomer,
  filterCustomerByLoaiKhach,
  getCustomers,
  searchCustomerByName,
} from "../services/customerApi"

const router = useRouter()
const customers = ref<Customer[]>([])

const loadData = async () => {
  customers.value = await getCustomers()
}

const handleSearch = async (keyword: string) => {
  if (!keyword.trim()) {
    await loadData()
    return
  }
  customers.value = await searchCustomerByName(keyword)
}

const handleFilter = async (value: string) => {
  if (!value) {
    await loadData()
    return
  }
  customers.value = await filterCustomerByLoaiKhach(value)
}

const handleView = (customer: Customer) => {
  router.push(`/sales/customer-loyalty/customers/${customer.id}`)
}

const handleEdit = (customer: Customer) => {
  router.push(`/sales/customer-loyalty/customers/${customer.id}/edit`)
}

const handleDelete = async (id: number) => {
  const ok = confirm("Xóa khách hàng này?")
  if (!ok) return
  await deleteCustomer(id)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.head,
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.btn,
button {
  background: #2563eb;
  color: white;
  padding: 10px 14px;
  text-decoration: none;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
</style>