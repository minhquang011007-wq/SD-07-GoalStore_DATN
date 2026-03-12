<template>
  <section>
    <h1>Lịch sử VIP</h1>
    <VipHistoryTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import VipHistoryTable from '../components/VipHistoryTable.vue'
import type { VipHistory } from '../types/loyalty'
import { getVipHistory } from '../services/loyaltyApi'

const route = useRoute()
const data = ref<VipHistory[]>([])

onMounted(async () => {
  data.value = await getVipHistory(Number(route.params.customerId))
})
</script>