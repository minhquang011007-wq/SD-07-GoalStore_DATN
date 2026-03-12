// CustomerDetailView.vue
<template>
  <section v-if="customer" class="card">
    <h1>Chi tiết khách hàng</h1>
    <p><b>ID:</b> {{ customer.id }}</p>
    <p><b>Tên:</b> {{ customer.ten }}</p>
    <p><b>Email:</b> {{ customer.email }}</p>
    <p><b>SĐT:</b> {{ customer.sdt }}</p>
    <p><b>Ngày sinh:</b> {{ customer.ngaySinh }}</p>
    <p><b>Loại khách:</b> {{ customer.loaiKhach }}</p>
    <p><b>Điểm tích lũy:</b> {{ customer.diemTichLuy }}</p>
    <p><b>Ghi chú:</b> {{ customer.ghiChu }}</p>
    <p><b>Ngày tạo:</b> {{ customer.createdAt }}</p>

    <div class="actions">
      <RouterLink :to="`/sales/customer-loyalty/customers/${customer.id}/edit`">Sửa</RouterLink>
      <RouterLink :to="`/sales/customer-loyalty/customers/${customer.id}/orders`">Xem đơn hàng</RouterLink>
      <RouterLink :to="`/sales/customer-loyalty/loyalty/${customer.id}/history`">Xem lịch sử điểm</RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import type { Customer } from '../types/customer'
import { getCustomerById } from '../services/customerApi'

const route = useRoute()
const customer = ref<Customer | null>(null)

onMounted(async () => {
  customer.value = await getCustomerById(Number(route.params.id))
})
</script>

<style scoped>
.card {
  background: white;
  padding: 20px;
  border-radius: 10px;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
a {
  padding: 10px 14px;
  background: #2563eb;
  color: white;
  text-decoration: none;
  border-radius: 8px;
}
</style>