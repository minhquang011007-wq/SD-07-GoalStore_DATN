<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { permissionApi } from "@/modules/permission/permission.api"
import type { PermissionResponse, Role } from "@/modules/permission/types"

const roles: Role[] = ["ADMIN", "SALES", "INVENTORY"]

const loading = ref(false)
const errorMsg = ref<string | null>(null)

const state = reactive({
  tab: "role" as "role" | "all",
  role: "ADMIN" as Role,
  module: "",
  grantCode: "",
})

const allPermissions = ref<PermissionResponse[]>([])
const rolePermissions = ref<PermissionResponse[]>([])

const modules = computed(() => {
  const set = new Set(allPermissions.value.map((p) => p.module))
  return Array.from(set).sort()
})

const selectedCodes = ref<string[]>([])

async function loadAll() {
  loading.value = true
  errorMsg.value = null
  try {
    allPermissions.value = await permissionApi.getAll()
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load permissions failed"
  } finally {
    loading.value = false
  }
}

async function loadRole() {
  loading.value = true
  errorMsg.value = null
  try {
    rolePermissions.value = await permissionApi.getByRole(state.role)
    selectedCodes.value = rolePermissions.value.map((p) => p.code)
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load role permissions failed"
  } finally {
    loading.value = false
  }
}

const filteredAll = computed(() => {
  if (!state.module) return allPermissions.value
  return allPermissions.value.filter((p) => p.module === state.module)
})

async function saveRolePermissions() {
  await permissionApi.updateRolePermissions({
    role: state.role,
    permissionCodes: selectedCodes.value,
  })
  await loadRole()
  alert("Cập nhật quyền thành công")
}

async function grant() {
  if (!state.grantCode.trim()) return
  await permissionApi.grant(state.role, state.grantCode.trim().toUpperCase())
  state.grantCode = ""
  await loadRole()
}

async function revoke(code: string) {
  await permissionApi.revoke(state.role, code)
  await loadRole()
}

onMounted(async () => {
  await loadAll()
  await loadRole()
})
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-semibold">Role & Permission</h1>
      <p class="text-slate-600">Quản lý permission theo role (ADMIN/SALES/INVENTORY).</p>
    </div>

    <div class="flex gap-2">
      <button class="px-4 py-2 rounded-md border" :class="state.tab==='role' ? 'bg-accent' : ''" @click="state.tab='role'">
        Theo role
      </button>
      <button class="px-4 py-2 rounded-md border" :class="state.tab==='all' ? 'bg-accent' : ''" @click="state.tab='all'">
        Tất cả permissions
      </button>
    </div>

    <div v-if="errorMsg" class="text-red-600 text-sm">{{ errorMsg }}</div>

    <!-- TAB ROLE -->
    <div v-if="state.tab==='role'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 flex flex-wrap items-end gap-3">
        <div>
          <div class="text-xs text-slate-500 mb-1">Role</div>
          <select v-model="state.role" class="border rounded-md px-3 py-2" @change="loadRole">
            <option v-for="r in roles" :key="r" :value="r">{{ r }}</option>
          </select>
        </div>

        <div class="flex-1 min-w-[240px]">
          <div class="text-xs text-slate-500 mb-1">Grant permission code</div>
          <div class="flex gap-2">
            <input v-model="state.grantCode" class="border rounded-md px-3 py-2 w-full" placeholder="VD: USER_READ" />
            <button class="px-4 py-2 rounded-md bg-primary text-white" @click="grant">Grant</button>
          </div>
        </div>

        <button class="px-4 py-2 rounded-md border" @click="loadRole">Reload</button>
        <button class="px-4 py-2 rounded-md bg-primary text-white" @click="saveRolePermissions">
          Save all (PUT)
        </button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <div class="p-3 border-b text-sm font-medium">Chọn permissions cho role: {{ state.role }}</div>

        <div class="p-3 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
          <label v-for="p in allPermissions" :key="p.code" class="flex items-start gap-2 p-2 rounded-md border">
            <input type="checkbox" :value="p.code" v-model="selectedCodes" class="mt-1" />
            <div class="min-w-0">
              <div class="text-sm font-medium">{{ p.code }}</div>
              <div class="text-xs text-slate-600 truncate">{{ p.name }}</div>
              <div class="text-[11px] text-slate-500">Module: {{ p.module }}</div>
              <button class="text-xs text-red-600 mt-1" @click.prevent="revoke(p.code)">Revoke (DELETE)</button>
            </div>
          </label>
        </div>
      </div>
    </div>

    <!-- TAB ALL -->
    <div v-else class="space-y-4">
      <div class="rounded-md border bg-card p-4 flex flex-wrap items-end gap-3">
        <div>
          <div class="text-xs text-slate-500 mb-1">Filter module</div>
          <select v-model="state.module" class="border rounded-md px-3 py-2">
            <option value="">All</option>
            <option v-for="m in modules" :key="m" :value="m">{{ m }}</option>
          </select>
        </div>

        <button class="px-4 py-2 rounded-md border" @click="loadAll">Reload</button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-muted">
            <tr class="text-left">
              <th class="p-3">Code</th>
              <th class="p-3">Name</th>
              <th class="p-3">Module</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in filteredAll" :key="p.id" class="border-t">
              <td class="p-3 font-medium">{{ p.code }}</td>
              <td class="p-3">{{ p.name }}</td>
              <td class="p-3">{{ p.module }}</td>
            </tr>
            <tr v-if="filteredAll.length===0">
              <td class="p-4 text-slate-600" colspan="3">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="loading" class="text-sm text-slate-600">Loading...</div>
  </div>
</template>