<template>
  <section>
    <h1>VIP Programs</h1>
    <VipProgramsTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import VipProgramsTable from '../components/VipProgramsTable.vue'
import type { VipProgram } from '../types/loyalty'
import { getVipPrograms } from '../services/loyaltyApi'

const data = ref<VipProgram[]>([])

onMounted(async () => {
  data.value = await getVipPrograms()
})
</script>