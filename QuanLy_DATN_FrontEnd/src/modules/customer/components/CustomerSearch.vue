// CustomerSearch.vue
<template>
  <div class="box">
    <input v-model="keyword" placeholder="Tìm theo tên..." />
    <button @click="$emit('search', keyword)">Tìm</button>
    <button @click="reset">Reset</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineEmits<{
  (e: 'search', keyword: string): void
}>()

const keyword = ref('')

const reset = () => {
  keyword.value = ''
}
</script>

<style scoped>
.box {
  display: flex;
  gap: 8px;
}
input {
  padding: 10px;
  min-width: 220px;
}
button {
  padding: 10px 14px;
}
</style>