<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { Role, UserResponse, UserStatus } from "./types"
import { userApi } from "./user.api"


const roles: Role[] = ["ADMIN", "SALES", "INVENTORY"]
const statuses: UserStatus[] = ["ACTIVE", "INACTIVE", "LOCKED"]

const loading = ref(false)
const errorMsg = ref<string | null>(null)

const filters = reactive({
  q: "",
  role: "" as "" | Role,
  trangThai: "" as "" | UserStatus,
  page: 0,
  size: 10,
})

const pageData = ref<{ content: UserResponse[]; totalElements: number; totalPages: number } | null>(null)

const rows = computed(() => pageData.value?.content || [])
const totalPages = computed(() => pageData.value?.totalPages || 0)

const createForm = reactive({
  open: false,
  username: "",
  email: "",
  password: "",
  role: "SALES" as Role,
  trangThai: "ACTIVE" as UserStatus,
})

const editForm = reactive({
  open: false,
  id: 0,
  email: "",
  role: "" as "" | Role,
  trangThai: "" as "" | UserStatus,
})

const statusForm = reactive({
  open: false,
  id: 0,
  trangThai: "ACTIVE" as UserStatus,
})

const resetForm = reactive({
  open: false,
  id: 0,
  newPassword: "",
})

async function load() {
  loading.value = true
  errorMsg.value = null
  try {
    const data = await userApi.getUsers({
      q: filters.q || undefined,
      role: filters.role || undefined,
      trangThai: filters.trangThai || undefined,
      page: filters.page,
      size: filters.size,
    })
    pageData.value = { content: data.content, totalElements: data.totalElements, totalPages: data.totalPages }
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load users failed"
  } finally {
    loading.value = false
  }
}

function openEdit(u: UserResponse) {
  editForm.open = true
  editForm.id = u.id
  editForm.email = u.email || ""
  editForm.role = u.role
  editForm.trangThai = u.trangThai
}

function openStatus(u: UserResponse) {
  statusForm.open = true
  statusForm.id = u.id
  statusForm.trangThai = u.trangThai
}

function openReset(u: UserResponse) {
  resetForm.open = true
  resetForm.id = u.id
  resetForm.newPassword = ""
}

async function submitCreate() {
  await userApi.createUser({
    username: createForm.username,
    email: createForm.email || null,
    password: createForm.password,
    role: createForm.role,
    trangThai: createForm.trangThai,
  })
  createForm.open = false
  createForm.username = ""
  createForm.email = ""
  createForm.password = ""
  await load()
}

async function submitEdit() {
  await userApi.updateUser(editForm.id, {
    email: editForm.email || null,
    role: editForm.role || null,
    trangThai: editForm.trangThai || null,
  })
  editForm.open = false
  await load()
}

async function submitStatus() {
  await userApi.changeStatus(statusForm.id, { trangThai: statusForm.trangThai })
  statusForm.open = false
  await load()
}

async function submitReset() {
  await userApi.resetPassword(resetForm.id, { newPassword: resetForm.newPassword })
  resetForm.open = false
  await load()
}

async function removeUser(u: UserResponse) {
  if (!confirm(`Xóa user ${u.username}?`)) return
  await userApi.deleteUser(u.id)
  await load()
}

function prevPage() {
  if (filters.page <= 0) return
  filters.page--
  load()
}
function nextPage() {
  if (filters.page + 1 >= totalPages.value) return
  filters.page++
  load()
}

onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-2xl font-semibold">User Management</h1>
        <p class="text-slate-600">Quản lý tài khoản, role, trạng thái, reset password.</p>
      </div>

      <button class="px-4 py-2 rounded-md bg-primary text-white" @click="createForm.open = true">
        + Tạo user
      </button>
    </div>

    <div class="rounded-md border bg-card p-4 space-y-3">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-3">
        <input v-model="filters.q" class="border rounded-md px-3 py-2" placeholder="Tìm username/email..." />
        <select v-model="filters.role" class="border rounded-md px-3 py-2">
          <option value="">All roles</option>
          <option v-for="r in roles" :key="r" :value="r">{{ r }}</option>
        </select>
        <select v-model="filters.trangThai" class="border rounded-md px-3 py-2">
          <option value="">All status</option>
          <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
        </select>
        <select v-model.number="filters.size" class="border rounded-md px-3 py-2">
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
        <button class="px-4 py-2 rounded-md bg-accent border" @click="filters.page = 0; load()">
          Lọc
        </button>
      </div>

      <div v-if="errorMsg" class="text-red-600 text-sm">{{ errorMsg }}</div>
    </div>

    <div class="rounded-md border bg-card overflow-hidden">
      <div v-if="loading" class="p-4 text-sm text-slate-600">Loading...</div>

      <table v-else class="w-full text-sm">
        <thead class="bg-muted">
          <tr class="text-left">
            <th class="p-3">ID</th>
            <th class="p-3">Username</th>
            <th class="p-3">Email</th>
            <th class="p-3">Role</th>
            <th class="p-3">Status</th>
            <th class="p-3">Created</th>
            <th class="p-3 w-[320px]">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in rows" :key="u.id" class="border-t">
            <td class="p-3">{{ u.id }}</td>
            <td class="p-3 font-medium">{{ u.username }}</td>
            <td class="p-3">{{ u.email || "-" }}</td>
            <td class="p-3">{{ u.role }}</td>
            <td class="p-3">{{ u.trangThai }}</td>
            <td class="p-3">{{ u.createdAt }}</td>
            <td class="p-3 flex flex-wrap gap-2">
              <button class="px-3 py-1 rounded-md border" @click="openEdit(u)">Edit</button>
              <button class="px-3 py-1 rounded-md border" @click="openStatus(u)">Status</button>
              <button class="px-3 py-1 rounded-md border" @click="openReset(u)">Reset PW</button>
              <button class="px-3 py-1 rounded-md border text-red-600" @click="removeUser(u)">Delete</button>
            </td>
          </tr>

          <tr v-if="rows.length === 0">
            <td class="p-4 text-slate-600" colspan="7">Không có dữ liệu</td>
          </tr>
        </tbody>
      </table>

      <div class="p-3 border-t flex items-center justify-between text-sm">
        <div>Page: {{ filters.page + 1 }} / {{ totalPages }}</div>
        <div class="flex gap-2">
          <button class="px-3 py-1 rounded-md border" @click="prevPage">Prev</button>
          <button class="px-3 py-1 rounded-md border" @click="nextPage">Next</button>
        </div>
      </div>
    </div>

    <!-- MODAL CREATE -->
    <div v-if="createForm.open" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-card w-full max-w-xl rounded-md border p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h2 class="font-semibold">Tạo user</h2>
          <button class="text-slate-600" @click="createForm.open = false">X</button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <input v-model="createForm.username" class="border rounded-md px-3 py-2" placeholder="username" />
          <input v-model="createForm.email" class="border rounded-md px-3 py-2" placeholder="email (optional)" />
          <input v-model="createForm.password" type="password" class="border rounded-md px-3 py-2" placeholder="password" />
          <select v-model="createForm.role" class="border rounded-md px-3 py-2">
            <option v-for="r in roles" :key="r" :value="r">{{ r }}</option>
          </select>
          <select v-model="createForm.trangThai" class="border rounded-md px-3 py-2">
            <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>

        <div class="flex justify-end gap-2">
          <button class="px-4 py-2 rounded-md border" @click="createForm.open = false">Cancel</button>
          <button class="px-4 py-2 rounded-md bg-primary text-white" @click="submitCreate">Create</button>
        </div>
      </div>
    </div>

    <!-- MODAL EDIT -->
    <div v-if="editForm.open" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-card w-full max-w-xl rounded-md border p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h2 class="font-semibold">Update user #{{ editForm.id }}</h2>
          <button class="text-slate-600" @click="editForm.open = false">X</button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <input v-model="editForm.email" class="border rounded-md px-3 py-2" placeholder="email" />
          <select v-model="editForm.role" class="border rounded-md px-3 py-2">
            <option value="">(no change)</option>
            <option v-for="r in roles" :key="r" :value="r">{{ r }}</option>
          </select>
          <select v-model="editForm.trangThai" class="border rounded-md px-3 py-2">
            <option value="">(no change)</option>
            <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>

        <div class="flex justify-end gap-2">
          <button class="px-4 py-2 rounded-md border" @click="editForm.open = false">Cancel</button>
          <button class="px-4 py-2 rounded-md bg-primary text-white" @click="submitEdit">Save</button>
        </div>
      </div>
    </div>

    <!-- MODAL STATUS -->
    <div v-if="statusForm.open" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-card w-full max-w-md rounded-md border p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h2 class="font-semibold">Change status #{{ statusForm.id }}</h2>
          <button class="text-slate-600" @click="statusForm.open = false">X</button>
        </div>

        <select v-model="statusForm.trangThai" class="border rounded-md px-3 py-2 w-full">
          <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
        </select>

        <div class="flex justify-end gap-2">
          <button class="px-4 py-2 rounded-md border" @click="statusForm.open = false">Cancel</button>
          <button class="px-4 py-2 rounded-md bg-primary text-white" @click="submitStatus">Save</button>
        </div>
      </div>
    </div>

    <!-- MODAL RESET -->
    <div v-if="resetForm.open" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-card w-full max-w-md rounded-md border p-4 space-y-3">
        <div class="flex items-center justify-between">
          <h2 class="font-semibold">Reset password #{{ resetForm.id }}</h2>
          <button class="text-slate-600" @click="resetForm.open = false">X</button>
        </div>

        <input v-model="resetForm.newPassword" type="password" class="border rounded-md px-3 py-2 w-full" placeholder="new password" />

        <div class="flex justify-end gap-2">
          <button class="px-4 py-2 rounded-md border" @click="resetForm.open = false">Cancel</button>
          <button class="px-4 py-2 rounded-md bg-primary text-white" @click="submitReset">Reset</button>
        </div>
      </div>
    </div>
  </div>
</template>