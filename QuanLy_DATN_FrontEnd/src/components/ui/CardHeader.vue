<script setup lang="ts">
import { cn } from '@/app/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="cn('flex flex-col space-y-1.5 p-3', $props.class)">
    <slot />
  </div>
</template>
