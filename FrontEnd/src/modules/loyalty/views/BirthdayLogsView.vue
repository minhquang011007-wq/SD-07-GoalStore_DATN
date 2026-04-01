<template>
  <section>
    <h1>Birthday Logs</h1>
    <BirthdayTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import BirthdayTable from '../components/BirthdayTable.vue'
import type { BirthdayLog } from '../types/loyalty'
import { getBirthdayLogs } from '../services/loyaltyApi'

const route = useRoute()
const data = ref<BirthdayLog[]>([])

onMounted(async () => {
  data.value = await getBirthdayLogs(Number(route.params.customerId))
})
</script>