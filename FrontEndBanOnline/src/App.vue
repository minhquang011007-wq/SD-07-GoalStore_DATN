<script setup lang="ts">
import { computed } from "vue"
import { RouterView, useRoute } from "vue-router"
import AppHeader from "@/components/layout/AppHeader.vue"
import AppFooter from "@/components/layout/AppFooter.vue"

const route = useRoute()
const hideLayout = computed(() => ["/login", "/register"].includes(route.path))
</script>

<template>
  <AppHeader v-if="!hideLayout" />
  <RouterView />
  <AppFooter v-if="!hideLayout" />
</template>
