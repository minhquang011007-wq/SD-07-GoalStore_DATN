<template>
  <section>
    <h1>Lịch sử điểm</h1>
    <LoyaltyHistoryTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import LoyaltyHistoryTable from '../components/LoyaltyHistoryTable.vue'
import type { LoyaltyHistory } from '../types/loyalty'
import { getLoyaltyHistory } from '../services/loyaltyApi'

const route = useRoute()
const data = ref<LoyaltyHistory[]>([])

onMounted(async () => {
  data.value = await getLoyaltyHistory(Number(route.params.customerId))
})
</script>