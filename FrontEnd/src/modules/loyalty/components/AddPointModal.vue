<template>
  <div class="modal">
    <div class="card">
      <h3>Cộng điểm</h3>
      <input v-model.number="points" type="number" placeholder="Số điểm" />
      <textarea v-model="note" placeholder="Ghi chú"></textarea>
      <div class="actions">
        <button @click="$emit('close')">Đóng</button>
        <button @click="save">Lưu</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  customerId: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', payload: { customerId: number; points: number; note: string }): void
}>()

const points = ref(0)
const note = ref('')

const save = () => {
  emit('save', {
    customerId: props.customerId,
    points: points.value,
    note: note.value
  })
}
</script>

<style scoped>
.modal {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: grid;
  place-items: center;
}
.card {
  width: 420px;
  background: white;
  padding: 20px;
  border-radius: 10px;
}
input, textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>