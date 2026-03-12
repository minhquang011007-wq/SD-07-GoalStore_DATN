// CustomerFilter.vue
<template>
  <div class="box">
    <select v-model="value" @change="$emit('filter', value)">
      <option value="">Tất cả</option>
      <option value="THUONG">THƯỜNG</option>
      <option value="VIP">VIP</option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineEmits<{
  (e: 'filter', value: string): void
}>()

const value = ref('')
</script>

<style scoped>
select {
  padding: 10px;
  min-width: 160px;
}
</style>