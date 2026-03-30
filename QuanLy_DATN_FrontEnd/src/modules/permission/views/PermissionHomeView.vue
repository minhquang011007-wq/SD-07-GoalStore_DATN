<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue"
import { permissionApi } from "../permission.api"
import type { PermissionResponse, Role } from "../types"
import { getHttpErrorMessage } from "../../../shared/lib/httpError"
import { showToast } from "../../../shared/lib/toast"
import ConfirmDialog from "../../../shared/components/ConfirmDialog.vue"

const roles: Role[] = ["ADMIN", "SALES", "INVENTORY"]

/** UI State */
const ui = reactive({
  loadingAll: false,
  loadingRole: false,
  saving: false,
  error: "" as string | "",
  tab: "role" as "role" | "all",
})

/** Filters */
const filters = reactive({
  role: "ADMIN" as Role,
  search: "",
  module: "",
})

/** Data */
const allPermissions = ref<PermissionResponse[]>([])
const rolePermissions = ref<PermissionResponse[]>([])

/**
 * selectedCodes: set quyền đang chọn (checkbox)
 * originalCodes: snapshot quyền khi load role => để dirty-check + diff
 */
const selectedCodes = ref<string[]>([])
const originalCodes = ref<string[]>([])

/** Quick grant/revoke */
const quick = reactive({
  code: "",
})

/** Confirm revoke (optional) */
const confirmRevoke = reactive({
  open: false,
  code: "",
})

/** Derived: module list */
const modules = computed(() => {
  const set = new Set(allPermissions.value.map((p) => p.module))
  return Array.from(set).sort()
})

/** Derived: search normalize */
const q = computed(() => filters.search.trim().toLowerCase())

/** Derived: filtered permissions */
const filteredPermissions = computed(() => {
  let list = allPermissions.value
  if (filters.module) list = list.filter((p) => p.module === filters.module)
  if (q.value) {
    list = list.filter((p) => {
      const code = (p.code || "").toLowerCase()
      const name = (p.name || "").toLowerCase()
      return code.includes(q.value) || name.includes(q.value)
    })
  }
  return list
})

/** Group permissions by module (đẹp + dễ nhìn) */
const groupedByModule = computed(() => {
  const map: Record<string, PermissionResponse[]> = {}
  for (const p of filteredPermissions.value) {
    const m = p.module || "OTHER"
    map[m] = map[m] || []
    map[m].push(p)
  }
  // sort inside
  for (const m of Object.keys(map)) {
    map[m].sort((a, b) => a.code.localeCompare(b.code))
  }
  return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]))
})

/** Dirty check */
const isDirty = computed(() => {
  const a = new Set(originalCodes.value)
  const b = new Set(selectedCodes.value)
  if (a.size !== b.size) return true
  for (const x of a) if (!b.has(x)) return true
  return false
})

/** Diff */
const addedCodes = computed(() => {
  const o = new Set(originalCodes.value)
  return selectedCodes.value.filter((c) => !o.has(c)).sort()
})
const removedCodes = computed(() => {
  const s = new Set(selectedCodes.value)
  return originalCodes.value.filter((c) => !s.has(c)).sort()
})

async function loadAll() {
  ui.loadingAll = true
  ui.error = ""
  try {
    allPermissions.value = await permissionApi.getAll()
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load permissions failed")
    showToast(ui.error, "error")
  } finally {
    ui.loadingAll = false
  }
}

async function loadRole() {
  ui.loadingRole = true
  ui.error = ""
  try {
    rolePermissions.value = await permissionApi.getByRole(filters.role)
    const codes = rolePermissions.value.map((p) => p.code).filter(Boolean)
    selectedCodes.value = [...codes].sort()
    originalCodes.value = [...codes].sort()
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load role permissions failed")
    showToast(ui.error, "error")
  } finally {
    ui.loadingRole = false
  }
}

/** Save ALL permissions for role (PUT replace) - chỉ cho phép khi dirty */
async function saveRolePermissions() {
  if (!isDirty.value) {
    showToast("Không có thay đổi để lưu", "info")
    return
  }

  ui.saving = true
  ui.error = ""
  try {
    await permissionApi.updateRolePermissions({
      role: filters.role,
      permissionCodes: selectedCodes.value,
    })
    originalCodes.value = [...selectedCodes.value].sort()
    showToast(`Đã cập nhật permissions cho role ${filters.role}`, "success")
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Update role permissions failed")
    showToast(ui.error, "error")
  } finally {
    ui.saving = false
  }
}

/** Reset changes => quay về original */
function resetChanges() {
  selectedCodes.value = [...originalCodes.value].sort()
  showToast("Đã hoàn tác thay đổi", "info")
}

/** Quick grant (POST) */
async function quickGrant() {
  const code = quick.code.trim().toUpperCase()
  if (!code) return

  ui.saving = true
  ui.error = ""
  try {
    await permissionApi.grant(filters.role, code)

    // reload role để đảm bảo đồng bộ backend
    await loadRole()
    quick.code = ""
    showToast(`Đã grant ${code} cho ${filters.role}`, "success")
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Grant failed")
    showToast(ui.error, "error")
  } finally {
    ui.saving = false
  }
}

/** Revoke (DELETE) */
function askRevoke(code: string) {
  confirmRevoke.open = true
  confirmRevoke.code = code
}

async function doRevoke() {
  const code = confirmRevoke.code
  confirmRevoke.open = false

  ui.saving = true
  ui.error = ""
  try {
    await permissionApi.revoke(filters.role, code)
    await loadRole()
    showToast(`Đã revoke ${code} khỏi ${filters.role}`, "success")
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Revoke failed")
    showToast(ui.error, "error")
  } finally {
    ui.saving = false
  }
}

/**
 * UX logic:
 * - đổi role => auto reload rolePermissions
 * - đổi role => reset filter module/search (tuỳ bạn)
 */
watch(
  () => filters.role,
  async () => {
    filters.search = ""
    filters.module = ""
    await loadRole()
  }
)

onMounted(async () => {
  await loadAll()
  await loadRole()
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-2xl font-semibold">Role & Permission</h1>
        <p class="text-slate-600">Quản lý quyền theo role, có diff thay đổi và dirty-check.</p>
      </div>

      <div class="flex gap-2">
        <button class="px-4 py-2 rounded-md border" :class="ui.tab==='role' ? 'bg-accent' : ''" @click="ui.tab='role'">
          Theo role
        </button>
        <button class="px-4 py-2 rounded-md border" :class="ui.tab==='all' ? 'bg-accent' : ''" @click="ui.tab='all'">
          Tất cả permissions
        </button>
      </div>
    </div>

    <div v-if="ui.error" class="text-sm text-red-600">{{ ui.error }}</div>

    <!-- TAB ROLE -->
    <div v-if="ui.tab==='role'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 space-y-3">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-3 items-end">
          <div>
            <div class="text-xs text-slate-500 mb-1">Role</div>
            <select v-model="filters.role" class="border rounded-md px-3 py-2 w-full">
              <option v-for="r in roles" :key="r" :value="r">{{ r }}</option>
            </select>
          </div>

          <div>
            <div class="text-xs text-slate-500 mb-1">Module</div>
            <select v-model="filters.module" class="border rounded-md px-3 py-2 w-full">
              <option value="">All</option>
              <option v-for="m in modules" :key="m" :value="m">{{ m }}</option>
            </select>
          </div>

          <div>
            <div class="text-xs text-slate-500 mb-1">Search</div>
            <input v-model="filters.search" class="border rounded-md px-3 py-2 w-full" placeholder="code/name..." />
          </div>

          <div class="flex gap-2">
            <button class="px-4 py-2 rounded-md border w-full" :disabled="ui.loadingRole || ui.saving" @click="loadRole">
              Reload
            </button>
          </div>
        </div>

        <!-- Quick grant -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-3 items-end">
          <div class="md:col-span-3">
            <div class="text-xs text-slate-500 mb-1">Quick grant code</div>
            <input v-model="quick.code" class="border rounded-md px-3 py-2 w-full" placeholder="VD: USER_READ" />
          </div>
          <button class="px-4 py-2 rounded-md bg-primary text-white" :disabled="ui.saving" @click="quickGrant">
            Grant (POST)
          </button>
        </div>

        <!-- Diff preview -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
          <div class="rounded-md border p-3">
            <div class="font-medium mb-2">Added ({{ addedCodes.length }})</div>
            <div v-if="addedCodes.length===0" class="text-slate-500">Không có</div>
            <div v-else class="flex flex-wrap gap-2">
              <span v-for="c in addedCodes" :key="c" class="px-2 py-1 rounded bg-green-100 text-green-700 text-xs">
                {{ c }}
              </span>
            </div>
          </div>
          <div class="rounded-md border p-3">
            <div class="font-medium mb-2">Removed ({{ removedCodes.length }})</div>
            <div v-if="removedCodes.length===0" class="text-slate-500">Không có</div>
            <div v-else class="flex flex-wrap gap-2">
              <span v-for="c in removedCodes" :key="c" class="px-2 py-1 rounded bg-red-100 text-red-700 text-xs">
                {{ c }}
              </span>
            </div>
          </div>
        </div>

        <!-- Save bar -->
        <div class="flex flex-wrap gap-2 justify-end">
          <button class="px-4 py-2 rounded-md border" :disabled="!isDirty || ui.saving" @click="resetChanges">
            Hoàn tác
          </button>
          <button
            class="px-4 py-2 rounded-md bg-primary text-white"
            :class="!isDirty ? 'opacity-50 cursor-not-allowed' : ''"
            :disabled="!isDirty || ui.saving"
            @click="saveRolePermissions"
          >
            Save all (PUT)
          </button>
        </div>
      </div>

      <!-- Permission list grouped -->
      <div class="rounded-md border bg-card overflow-hidden">
        <div class="p-3 border-b text-sm font-medium">
          Permissions ({{ filteredPermissions.length }}) — role: {{ filters.role }}
          <span v-if="ui.loadingRole" class="ml-2 text-slate-500">Loading...</span>
        </div>

        <div class="p-3 space-y-4">
          <div v-for="[moduleName, list] in groupedByModule" :key="moduleName" class="rounded-md border">
            <div class="p-3 border-b font-medium text-sm flex items-center justify-between">
              <div>{{ moduleName }} ({{ list.length }})</div>
              <div class="text-xs text-slate-500">
                Checked: {{ list.filter(p => selectedCodes.includes(p.code)).length }}
              </div>
            </div>

            <div class="p-3 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
              <label v-for="p in list" :key="p.code" class="flex items-start gap-2 p-2 rounded-md border hover:bg-accent">
                <input type="checkbox" :value="p.code" v-model="selectedCodes" class="mt-1" />
                <div class="min-w-0">
                  <div class="text-sm font-medium">{{ p.code }}</div>
                  <div class="text-xs text-slate-600 truncate">{{ p.name }}</div>
                  <div class="mt-1">
                    <button
                      class="text-xs text-red-600"
                      type="button"
                      @click.prevent="askRevoke(p.code)"
                    >
                      Revoke (DELETE)
                    </button>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div v-if="filteredPermissions.length===0" class="text-sm text-slate-600">
            Không có permission phù hợp filter.
          </div>
        </div>
      </div>
    </div>

    <!-- TAB ALL -->
    <div v-else class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
        <div>
          <div class="text-xs text-slate-500 mb-1">Module</div>
          <select v-model="filters.module" class="border rounded-md px-3 py-2 w-full">
            <option value="">All</option>
            <option v-for="m in modules" :key="m" :value="m">{{ m }}</option>
          </select>
        </div>

        <div>
          <div class="text-xs text-slate-500 mb-1">Search</div>
          <input v-model="filters.search" class="border rounded-md px-3 py-2 w-full" placeholder="code/name..." />
        </div>

        <button class="px-4 py-2 rounded-md border" :disabled="ui.loadingAll" @click="loadAll">Reload</button>
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
            <tr v-for="p in filteredPermissions" :key="p.id" class="border-t">
              <td class="p-3 font-medium">{{ p.code }}</td>
              <td class="p-3">{{ p.name }}</td>
              <td class="p-3">{{ p.module }}</td>
            </tr>
            <tr v-if="filteredPermissions.length===0">
              <td class="p-4 text-slate-600" colspan="3">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <ConfirmDialog
      :open="confirmRevoke.open"
      title="Thu hồi permission"
      :message="`Bạn chắc chắn muốn revoke ${confirmRevoke.code} khỏi role ${filters.role}?`"
      confirm-text="Revoke"
      cancel-text="Hủy"
      @cancel="confirmRevoke.open=false"
      @confirm="doRevoke"
    />
  </div>
</template>