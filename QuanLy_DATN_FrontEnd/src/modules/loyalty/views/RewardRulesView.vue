<template>
  <section>
    <h1>Reward Rules</h1>
    <RewardRulesTable :data="data" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import RewardRulesTable from '../components/RewardRulesTable.vue'
import type { RewardRule } from '../types/loyalty'
import { getRewardRules } from '../services/loyaltyApi'

const data = ref<RewardRule[]>([])

onMounted(async () => {
  data.value = await getRewardRules()
})
</script>