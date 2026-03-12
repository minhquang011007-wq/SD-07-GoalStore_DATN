<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { AuditDashboardResponse, AuditLogResponse, LoginAttemptResponse, SecurityAlertResponse } from "./types"
import { auditApi } from "./audit.api"


const tab = ref<"dashboard" | "logs" | "login" | "alerts">("dashboard")

const loading = ref(false)
const errorMsg = ref<string | null>(null)

const dashboard = ref<AuditDashboardResponse | null>(null)

const logsPage = ref<{ content: AuditLogResponse[]; totalPages: number } | null>(null)
const loginPage = ref<{ content: LoginAttemptResponse[]; totalPages: number } | null>(null)
const alertsPage = ref<{ content: SecurityAlertResponse[]; totalPages: number } | null>(null)

const range = reactive({
  from: "",
  to: "",
})

const logsFilter = reactive({
  action: "",
  entity: "",
  userId: "" as any,
  page: 0,
  size: 10,
})

const loginFilter = reactive({
  username: "",
  page: 0,
  size: 10,
})

const alertsFilter = reactive({
  resolved: "" as "" | "true" | "false",
  alertType: "",
  page: 0,
  size: 10,
})

const logsRows = computed(() => logsPage.value?.content || [])
const loginRows = computed(() => loginPage.value?.content || [])
const alertRows = computed(() => alertsPage.value?.content || [])

function paramsRange() {
  // backend dùng ISO.DATE_TIME -> input type=datetime-local trả dạng "YYYY-MM-DDTHH:mm"
  // thường Spring parse OK nếu có seconds? Nếu lỗi, bạn có thể thêm ":00"
  return {
    from: range.from || undefined,
    to: range.to || undefined,
  }
}

async function loadDashboard() {
  loading.value = true
  errorMsg.value = null
  try {
    dashboard.value = await auditApi.getDashboard(paramsRange())
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load dashboard failed"
  } finally {
    loading.value = false
  }
}

async function loadLogs() {
  loading.value = true
  errorMsg.value = null
  try {
    logsPage.value = await auditApi.getAuditLogs({
      ...paramsRange(),
      action: logsFilter.action || undefined,
      entity: logsFilter.entity || undefined,
      userId: logsFilter.userId || undefined,
      page: logsFilter.page,
      size: logsFilter.size,
    })
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load audit logs failed"
  } finally {
    loading.value = false
  }
}

async function loadLoginHistory() {
  loading.value = true
  errorMsg.value = null
  try {
    loginPage.value = await auditApi.getLoginHistory({
      ...paramsRange(),
      username: loginFilter.username || undefined,
      page: loginFilter.page,
      size: loginFilter.size,
    })
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load login history failed"
  } finally {
    loading.value = false
  }
}

async function loadAlerts() {
  loading.value = true
  errorMsg.value = null
  try {
    alertsPage.value = await auditApi.getAlerts({
      resolved: alertsFilter.resolved ? alertsFilter.resolved === "true" : undefined,
      alertType: alertsFilter.alertType || undefined,
      page: alertsFilter.page,
      size: alertsFilter.size,
    })
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "Load alerts failed"
  } finally {
    loading.value = false
  }
}

async function resolveAlert(id: number) {
  await auditApi.resolveAlert(id)
  await loadAlerts()
}

async function downloadCsv() {
  const blob = await auditApi.exportCsv({
    ...paramsRange(),
    action: logsFilter.action || undefined,
    entity: logsFilter.entity || undefined,
    userId: logsFilter.userId || undefined,
  })
  const url = URL.createObjectURL(blob)
  const a = document.createElement("a")
  a.href = url
  a.download = "audit-log.csv"
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(async () => {
  await loadDashboard()
  await loadLogs()
  await loadLoginHistory()
  await loadAlerts()
})
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-semibold">Audit & Security Reports</h1>
      <p class="text-slate-600">Dashboard, audit logs, login history, security alerts.</p>
    </div>

    <div class="rounded-md border bg-card p-4 space-y-3">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
        <div>
          <div class="text-xs text-slate-500 mb-1">From</div>
          <input v-model="range.from" type="datetime-local" class="border rounded-md px-3 py-2 w-full" />
        </div>
        <div>
          <div class="text-xs text-slate-500 mb-1">To</div>
          <input v-model="range.to" type="datetime-local" class="border rounded-md px-3 py-2 w-full" />
        </div>
        <div class="flex items-end gap-2">
          <button class="px-4 py-2 rounded-md border" @click="loadDashboard(); loadLogs(); loadLoginHistory();">
            Apply range
          </button>
          <button class="px-4 py-2 rounded-md bg-primary text-white" @click="downloadCsv">
            Export CSV
          </button>
        </div>
      </div>

      <div v-if="errorMsg" class="text-red-600 text-sm">{{ errorMsg }}</div>
      <div v-if="loading" class="text-sm text-slate-600">Loading...</div>
    </div>

    <div class="flex gap-2">
      <button class="px-4 py-2 rounded-md border" :class="tab==='dashboard' ? 'bg-accent' : ''" @click="tab='dashboard'">
        Dashboard
      </button>
      <button class="px-4 py-2 rounded-md border" :class="tab==='logs' ? 'bg-accent' : ''" @click="tab='logs'">
        Audit Logs
      </button>
      <button class="px-4 py-2 rounded-md border" :class="tab==='login' ? 'bg-accent' : ''" @click="tab='login'">
        Login History
      </button>
      <button class="px-4 py-2 rounded-md border" :class="tab==='alerts' ? 'bg-accent' : ''" @click="tab='alerts'">
        Alerts
      </button>
    </div>

    <!-- DASHBOARD -->
    <div v-if="tab==='dashboard'" class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Total Logs</div>
        <div class="text-2xl font-semibold">{{ dashboard?.totalLogs ?? "-" }}</div>
      </div>
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Login Success / Failed</div>
        <div class="text-2xl font-semibold">
          {{ dashboard?.totalLoginSuccess ?? "-" }} / {{ dashboard?.totalLoginFailed ?? "-" }}
        </div>
      </div>
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Alerts (critical)</div>
        <div class="text-2xl font-semibold">
          {{ dashboard?.totalAlerts ?? "-" }} ({{ dashboard?.totalCriticalAlerts ?? "-" }})
        </div>
      </div>

      <div class="rounded-md border bg-card p-4 md:col-span-3">
        <div class="font-medium mb-2">Logs by module</div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-2 text-sm">
          <div v-for="(v,k) in dashboard?.logsByModule" :key="k" class="border rounded-md p-2">
            <div class="text-xs text-slate-500">{{ k }}</div>
            <div class="font-semibold">{{ v }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- LOGS -->
    <div v-else-if="tab==='logs'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-4 gap-3">
        <input v-model="logsFilter.action" class="border rounded-md px-3 py-2" placeholder="action" />
        <input v-model="logsFilter.entity" class="border rounded-md px-3 py-2" placeholder="entity" />
        <input v-model="logsFilter.userId" class="border rounded-md px-3 py-2" placeholder="userId" />
        <button class="px-4 py-2 rounded-md border" @click="logsFilter.page=0; loadLogs()">Search</button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-muted">
            <tr class="text-left">
              <th class="p-3">Time</th>
              <th class="p-3">User</th>
              <th class="p-3">Action</th>
              <th class="p-3">Entity</th>
              <th class="p-3">Detail</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="x in logsRows" :key="x.id" class="border-t">
              <td class="p-3">{{ x.createdAt }}</td>
              <td class="p-3">{{ x.username || x.userId || "-" }}</td>
              <td class="p-3 font-medium">{{ x.action }}</td>
              <td class="p-3">{{ x.entity }}#{{ x.entityId ?? "-" }}</td>
              <td class="p-3">{{ x.detail || "-" }}</td>
            </tr>
            <tr v-if="logsRows.length===0">
              <td class="p-4 text-slate-600" colspan="5">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>

        <div class="p-3 border-t flex justify-between text-sm">
          <div>Page: {{ logsFilter.page + 1 }} / {{ logsPage?.totalPages ?? 0 }}</div>
          <div class="flex gap-2">
            <button class="px-3 py-1 rounded-md border" @click="logsFilter.page=Math.max(0, logsFilter.page-1); loadLogs()">Prev</button>
            <button class="px-3 py-1 rounded-md border" @click="logsFilter.page=logsFilter.page+1; loadLogs()">Next</button>
          </div>
        </div>
      </div>
    </div>

    <!-- LOGIN -->
    <div v-else-if="tab==='login'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-4 gap-3">
        <input v-model="loginFilter.username" class="border rounded-md px-3 py-2" placeholder="username" />
        <button class="px-4 py-2 rounded-md border" @click="loginFilter.page=0; loadLoginHistory()">Search</button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-muted">
            <tr class="text-left">
              <th class="p-3">Time</th>
              <th class="p-3">Username</th>
              <th class="p-3">Success</th>
              <th class="p-3">IP</th>
              <th class="p-3">UserAgent</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="x in loginRows" :key="x.id" class="border-t">
              <td class="p-3">{{ x.createdAt }}</td>
              <td class="p-3 font-medium">{{ x.username }}</td>
              <td class="p-3">
                <span :class="x.success ? 'text-green-600' : 'text-red-600'">
                  {{ x.success ? "SUCCESS" : "FAILED" }}
                </span>
              </td>
              <td class="p-3">{{ x.ipAddress || "-" }}</td>
              <td class="p-3 truncate max-w-[420px]">{{ x.userAgent || "-" }}</td>
            </tr>
            <tr v-if="loginRows.length===0">
              <td class="p-4 text-slate-600" colspan="5">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ALERTS -->
    <div v-else class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-4 gap-3">
        <select v-model="alertsFilter.resolved" class="border rounded-md px-3 py-2">
          <option value="">All</option>
          <option value="false">Unresolved</option>
          <option value="true">Resolved</option>
        </select>
        <input v-model="alertsFilter.alertType" class="border rounded-md px-3 py-2" placeholder="alertType" />
        <button class="px-4 py-2 rounded-md border" @click="alertsFilter.page=0; loadAlerts()">Search</button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-muted">
            <tr class="text-left">
              <th class="p-3">Time</th>
              <th class="p-3">Type</th>
              <th class="p-3">Severity</th>
              <th class="p-3">Message</th>
              <th class="p-3">Resolved</th>
              <th class="p-3">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in alertRows" :key="a.id" class="border-t">
              <td class="p-3">{{ a.createdAt }}</td>
              <td class="p-3 font-medium">{{ a.alertType }}</td>
              <td class="p-3">{{ a.severity }}</td>
              <td class="p-3">{{ a.message }}</td>
              <td class="p-3">{{ a.resolved ? "YES" : "NO" }}</td>
              <td class="p-3">
                <button
                  v-if="!a.resolved"
                  class="px-3 py-1 rounded-md border"
                  @click="resolveAlert(a.id)"
                >
                  Resolve
                </button>
              </td>
            </tr>
            <tr v-if="alertRows.length===0">
              <td class="p-4 text-slate-600" colspan="6">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>