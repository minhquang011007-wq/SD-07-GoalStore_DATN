<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Menu, User, LogOut, ChevronDown } from 'lucide-vue-next'

defineProps<{
  onToggleSidebar: () => void
}>()

const router = useRouter()
const searchQuery = ref('')
const accountDropdownOpen = ref(false)

const username = computed(() => localStorage.getItem('username') || 'User')
const role = computed(() => localStorage.getItem('role') || 'Unknown')

const handleSearch = (e: Event) => {
  e.preventDefault()
  console.log('Search:', searchQuery.value)
}

const toggleAccountDropdown = () => {
  accountDropdownOpen.value = !accountDropdownOpen.value
}

const closeAccountDropdown = () => {
  accountDropdownOpen.value = false
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  closeAccountDropdown()
  router.replace('/login')
}
</script>

<template>
  <nav class="bg-card border-b sticky top-0 z-40">
    <div class="px-4 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <div class="flex items-center gap-4">
          <button @click="onToggleSidebar" class="lg:hidden p-2 hover:bg-accent rounded-md" aria-label="Toggle menu">
            <Menu :size="20" />
          </button>

          <form @submit="handleSearch" class="hidden md:block">
            <div class="relative">
              <Search :size="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
              <input
                v-model="searchQuery"
                type="search"
                placeholder="Search module..."
                class="pl-10 pr-4 py-2 bg-background border rounded-md focus:outline-none focus:ring-2 focus:ring-primary w-64 lg:w-96"
              />
            </div>
          </form>
        </div>

        <div class="relative">
          <button
            @click="toggleAccountDropdown"
            class="flex items-center gap-2 px-3 py-2 hover:bg-accent rounded-md"
            aria-label="Account menu"
          >
            <div class="w-8 h-8 rounded-full bg-primary text-primary-foreground flex items-center justify-center">
              <User :size="18" />
            </div>
            <div class="hidden sm:block text-left">
              <div class="text-sm font-medium">{{ username }}</div>
              <div class="text-xs text-muted-foreground">{{ role }}</div>
            </div>
            <ChevronDown :size="16" class="hidden sm:block" />
          </button>

          <div v-if="accountDropdownOpen" @click="closeAccountDropdown" class="fixed inset-0 z-40"></div>

          <div v-if="accountDropdownOpen" class="absolute right-0 mt-2 w-56 bg-card border rounded-md shadow-lg py-1 z-50">
            <div class="px-4 py-3 border-b">
              <p class="text-sm font-medium">{{ username }}</p>
              <p class="text-xs text-muted-foreground">{{ role }}</p>
            </div>
            <button @click="handleLogout" class="w-full flex items-center gap-2 px-4 py-2 text-sm hover:bg-accent text-red-600">
              <LogOut :size="16" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>
