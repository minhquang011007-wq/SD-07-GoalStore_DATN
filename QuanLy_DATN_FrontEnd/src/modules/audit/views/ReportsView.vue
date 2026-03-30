<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue"
import { auditApi } from "./audit.api"
import type {
  AuditDashboardResponse,
  AuditLogResponse,
  LoginAttemptResponse,
  SecurityAlertResponse,
} from "./types"
import { getHttpErrorMessage } from "../../../shared/lib/httpError"
import { showToast } from "../../../shared/lib/toast"

type Tab = "dashboard" | "logs" | "login" | "alerts"
type Preset = "today" | "thisWeek" | "thisMonth" | "custom"

const ui = reactive({
  tab: "dashboard" as Tab,
  preset: "thisWeek" as Preset,
  loading: false,
  error: "" as string | "",
})

/**
 * DateTime strategy
 * - backend expects ISO DATE_TIME
 * - datetime-local gives YYYY-MM-DDTHH:mm
 * -> append ":00" for seconds for safer parsing
 */
function normalizeDT(v: string) {
  if (!v) return ""
  return v.length === 16 ? `${v}:00` : v
}

function startOfTodayLocal() {
  const d = new Date()
  d.setHours(0, 0, 0, 0)
  return d
}

function toLocalDatetimeInputValue(d: Date) {
  const pad = (n: number) => String(n).padStart(2, "0")
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function getPresetRange(preset: Preset) {
  const now = new Date()
  const to = toLocalDatetimeInputValue(now)

  if (preset === "today") {
    const from = toLocalDatetimeInputValue(startOfTodayLocal())
    return { from, to }
  }

  if (preset === "thisWeek") {
    // tuần bắt đầu Thứ 2 (ISO). JS getDay(): CN=0
    const d = new Date()
    const day = d.getDay() || 7
    d.setDate(d.getDate() - day + 1)
    d.setHours(0, 0, 0, 0)
    const from = toLocalDatetimeInputValue(d)
    return { from, to }
  }

  if (preset === "thisMonth") {
    const d = new Date(now.getFullYear(), now.getMonth(), 1)
    d.setHours(0, 0, 0, 0)
    const from = toLocalDatetimeInputValue(d)
    return { from, to }
  }

  return { from: range.from, to: range.to }
}

const range = reactive({
  from: "",
  to: "",
})

watch(
  () => ui.preset,
  (p) => {
    const r = getPresetRange(p)
    range.from = r.from
    range.to = r.to
  },
  { immediate: true }
)

function baseRangeParams() {
  return {
    from: range.from ? normalizeDT(range.from) : undefined,
    to: range.to ? normalizeDT(range.to) : undefined,
  }
}

/** Data holders */
const dashboard = ref<AuditDashboardResponse | null>(null)

const logs = reactive({
  rows: [] as AuditLogResponse[],
  page: 0,
  size: 10,
  totalPages: 0,
  action: "",
  entity: "",
  userId: "",
  keyword: "",
})

const login = reactive({
  rows: [] as LoginAttemptResponse[],
  page: 0,
  size: 10,
  totalPages: 0,
  username: "",
})

const alerts = reactive({
  rows: [] as SecurityAlertResponse[],
  page: 0,
  size: 10,
  totalPages: 0,
  resolved: "" as "" | "true" | "false",
  alertType: "",
})

const hasRange = computed(() => !!range.from && !!range.to)

function badgeClass(severity: string) {
  const s = (severity || "").toUpperCase()
  if (s === "CRITICAL") return "bg-red-600 text-white"
  if (s === "HIGH") return "bg-orange-500 text-white"
  if (s === "MEDIUM") return "bg-yellow-500 text-black"
  return "bg-slate-200 text-slate-900"
}

async function loadDashboard() {
  ui.loading = true
  ui.error = ""
  try {
    dashboard.value = await auditApi.getDashboard(baseRangeParams())
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load dashboard failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

async function loadLogs() {
  ui.loading = true
  ui.error = ""
  try {
    const data = await auditApi.getAuditLogs({
      ...baseRangeParams(),
      action: logs.action || undefined,
      entity: logs.entity || undefined,
      userId: logs.userId || undefined,
      // keyword not supported by backend yet, keep for UI only
      page: logs.page,
      size: logs.size,
    })

    // keyword filter client-side (optional)
    let rows = data.content
    const kw = logs.keyword.trim().toLowerCase()
    if (kw) {
      rows = rows.filter((x) => {
        const s =
          `${x.action} ${x.entity} ${x.username || ""} ${x.detail || ""} ${x.entityId ?? ""}`.toLowerCase()
        return s.includes(kw)
      })
    }

    logs.rows = rows
    logs.totalPages = data.totalPages
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load audit logs failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

async function loadLoginHistory() {
  ui.loading = true
  ui.error = ""
  try {
    const data = await auditApi.getLoginHistory({
      ...baseRangeParams(),
      username: login.username || undefined,
      page: login.page,
      size: login.size,
    })
    login.rows = data.content
    login.totalPages = data.totalPages
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load login history failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

async function loadAlerts() {
  ui.loading = true
  ui.error = ""
  try {
    const data = await auditApi.getAlerts({
      resolved: alerts.resolved ? alerts.resolved === "true" : undefined,
      alertType: alerts.alertType || undefined,
      page: alerts.page,
      size: alerts.size,
    })
    alerts.rows = data.content
    alerts.totalPages = data.totalPages
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Load alerts failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

async function resolveAlert(id: number) {
  ui.loading = true
  ui.error = ""
  try {
    await auditApi.resolveAlert(id)
    showToast("Resolved alert", "success")
    await loadAlerts()
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Resolve failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

async function exportCsvThisRange() {
  if (!hasRange.value) {
    showToast("Chọn khoảng thời gian trước khi export", "info")
    return
  }
  ui.loading = true
  ui.error = ""
  try {
    const blob = await auditApi.exportCsv({
      ...baseRangeParams(),
      action: logs.action || undefined,
      entity: logs.entity || undefined,
      userId: logs.userId || undefined,
    })

    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `audit-${ui.preset}-${new Date().toISOString().slice(0, 10)}.csv`
    a.click()
    URL.revokeObjectURL(url)
    showToast("Export CSV thành công", "success")
  } catch (e: any) {
    ui.error = getHttpErrorMessage(e, "Export CSV failed")
    showToast(ui.error, "error")
  } finally {
    ui.loading = false
  }
}

/** Auto load tab */
watch(
  () => ui.tab,
  async (t) => {
    if (t === "dashboard") return loadDashboard()
    if (t === "logs") return loadLogs()
    if (t === "login") return loadLoginHistory()
    if (t === "alerts") return loadAlerts()
  }
)

onMounted(async () => {
  await loadDashboard()
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h1 class="text-2xl font-semibold">Audit & Reports</h1>
        <p class="text-slate-600">Dashboard, logs, login history, alerts, export CSV.</p>
      </div>

      <button class="px-4 py-2 rounded-md bg-primary text-white" @click="exportCsvThisRange">
        Export CSV
      </button>
    </div>

    <!-- Range preset -->
    <div class="rounded-md border bg-card p-4 space-y-3">
      <div class="flex flex-wrap items-end gap-3">
        <div>
          <div class="text-xs text-slate-500 mb-1">Preset</div>
          <select v-model="ui.preset" class="border rounded-md px-3 py-2">
            <option value="today">Today</option>
            <option value="thisWeek">This week</option>
            <option value="thisMonth">This month</option>
            <option value="custom">Custom</option>
          </select>
        </div>

        <div>
          <input v-model="range.from" type="datetime-local" class="border rounded-md px-3 py-2" />
          <input v-model="range.to" type="datetime-local" class="border rounded-md px-3 py-2" />
        </div>

        <button class="px-4 py-2 rounded-md border"
          @click="ui.tab === 'dashboard' ? loadDashboard() : (ui.tab === 'logs' ? loadLogs() : (ui.tab === 'login' ? loadLoginHistory() : loadAlerts()))">
          Apply
        </button>
      </div>

      <div v-if="ui.error" class="text-sm text-red-600">{{ ui.error }}</div>
      <div v-if="ui.loading" class="text-sm text-slate-600">Loading...</div>
    </div>

    <!-- Tabs -->
    <div class="flex flex-wrap gap-2">
      <button class="px-4 py-2 rounded-md border" :class="ui.tab === 'dashboard' ? 'bg-accent' : ''"
        @click="ui.tab = 'dashboard'">Dashboard</button>
      <button class="px-4 py-2 rounded-md border" :class="ui.tab === 'logs' ? 'bg-accent' : ''"
        @click="ui.tab = 'logs'">Audit Logs</button>
      <button class="px-4 py-2 rounded-md border" :class="ui.tab === 'login' ? 'bg-accent' : ''"
        @click="ui.tab = 'login'">Login History</button>
      <button class="px-4 py-2 rounded-md border" :class="ui.tab === 'alerts' ? 'bg-accent' : ''"
        @click="ui.tab = 'alerts'">Alerts</button>
    </div>

    <!-- DASHBOARD -->
    <div v-if="ui.tab === 'dashboard'" class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Total Logs</div>
        <div class="text-2xl font-semibold">{{ dashboard?.totalLogs ?? "-" }}</div>
      </div>
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Login Success</div>
        <div class="text-2xl font-semibold">{{ dashboard?.totalLoginSuccess ?? "-" }}</div>
      </div>
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Login Failed</div>
        <div class="text-2xl font-semibold">{{ dashboard?.totalLoginFailed ?? "-" }}</div>
      </div>
      <div class="rounded-md border bg-card p-4">
        <div class="text-xs text-slate-500">Alerts (Critical)</div>
        <div class="text-2xl font-semibold">
          {{ dashboard?.totalAlerts ?? "-" }} ({{ dashboard?.totalCriticalAlerts ?? "-" }})
        </div>
      </div>

      <div class="rounded-md border bg-card p-4 md:col-span-4">
        <div class="font-medium mb-3">Logs by module</div>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-2 text-sm">
          <div v-for="(v, k) in dashboard?.logsByModule" :key="k" class="border rounded-md p-2">
            <div class="text-xs text-slate-500">{{ k }}</div>
            <div class="font-semibold">{{ v }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- LOGS -->
    <div v-else-if="ui.tab === 'logs'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-5 gap-3">
        <input v-model="logs.action" class="border rounded-md px-3 py-2" placeholder="action" />
        <input v-model="logs.entity" class="border rounded-md px-3 py-2" placeholder="entity" />
        <input v-model="logs.userId" class="border rounded-md px-3 py-2" placeholder="userId" />
        <input v-model="logs.keyword" class="border rounded-md px-3 py-2" placeholder="keyword (client filter)" />
        <button class="px-4 py-2 rounded-md border" @click="logs.page = 0; loadLogs()">Search</button>
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
            <tr v-for="x in logs.rows" :key="x.id" class="border-t hover:bg-accent">
              <td class="p-3">{{ x.createdAt }}</td>
              <td class="p-3">{{ x.username || x.userId || "-" }}</td>
              <td class="p-3 font-medium">{{ x.action }}</td>
              <td class="p-3">{{ x.entity }}#{{ x.entityId ?? "-" }}</td>
              <td class="p-3">{{ x.detail || "-" }}</td>
            </tr>
            <tr v-if="logs.rows.length === 0">
              <td class="p-4 text-slate-600" colspan="5">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>

        <div class="p-3 border-t flex items-center justify-between text-sm">
          <div>Page: {{ logs.page + 1 }} / {{ logs.totalPages }}</div>
          <div class="flex gap-2">
            <button class="px-3 py-1 rounded-md border" :disabled="logs.page <= 0"
              @click="logs.page--; loadLogs()">Prev</button>
            <button class="px-3 py-1 rounded-md border" :disabled="logs.page + 1 >= logs.totalPages"
              @click="logs.page++; loadLogs()">Next</button>
          </div>
        </div>
      </div>
    </div>

    <!-- LOGIN -->
    <div v-else-if="ui.tab === 'login'" class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-4 gap-3">
        <input v-model="login.username" class="border rounded-md px-3 py-2" placeholder="username" />
        <button class="px-4 py-2 rounded-md border" @click="login.page = 0; loadLoginHistory()">Search</button>
      </div>

      <div class="rounded-md border bg-card overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-muted">
            <tr class="text-left">
              <th class="p-3">Time</th>
              <th class="p-3">Username</th>
              <th class="p-3">Result</th>
              <th class="p-3">IP</th>
              <th class="p-3">UserAgent</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="x in login.rows" :key="x.id" class="border-t hover:bg-accent">
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
            <tr v-if="login.rows.length === 0">
              <td class="p-4 text-slate-600" colspan="5">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>

        <div class="p-3 border-t flex items-center justify-between text-sm">
          <div>Page: {{ login.page + 1 }} / {{ login.totalPages }}</div>
          <div class="flex gap-2">
            <button class="px-3 py-1 rounded-md border" :disabled="login.page <= 0"
              @click="login.page--; loadLoginHistory()">Prev</button>
            <button class="px-3 py-1 rounded-md border" :disabled="login.page + 1 >= login.totalPages"
              @click="login.page++; loadLoginHistory()">Next</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ALERTS -->
    <div v-else class="space-y-4">
      <div class="rounded-md border bg-card p-4 grid grid-cols-1 md:grid-cols-4 gap-3">
        <select v-model="alerts.resolved" class="border rounded-md px-3 py-2">
          <option value="">All</option>
          <option value="false">Unresolved</option>
          <option value="true">Resolved</option>
        </select>
        <input v-model="alerts.alertType" class="border rounded-md px-3 py-2" placeholder="alertType" />
        <button class="px-4 py-2 rounded-md border" @click="alerts.page = 0; loadAlerts()">Search</button>
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
            <tr v-for="a in alerts.rows" :key="a.id" class="border-t hover:bg-accent">
              <td class="p-3">{{ a.createdAt }}</td>
              <td class="p-3 font-medium">{{ a.alertType }}</td>
              <td class="p-3">
                <span class="px-2 py-1 rounded text-xs" :class="badgeClass(a.severity)">
                  {{ a.severity }}
                </span>
              </td>
              <td class="p-3">{{ a.message }}</td>
              <td class="p-3">{{ a.resolved ? "YES" : "NO" }}</td>
              <td class="p-3">
                <button v-if="!a.resolved" class="px-3 py-1 rounded-md border" @click="resolveAlert(a.id)">
                  Resolve
                </button>
              </td>
            </tr>
            <tr v-if="alerts.rows.length === 0">
              <td class="p-4 text-slate-600" colspan="6">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>

        <div class="p-3 border-t flex items-center justify-between text-sm">
          <div>Page: {{ alerts.page + 1 }} / {{ alerts.totalPages }}</div>
          <div class="flex gap-2">
            <button class="px-3 py-1 rounded-md border" :disabled="alerts.page <= 0"
              @click="alerts.page--; loadAlerts()">Prev</button>
            <button class="px-3 py-1 rounded-md border" :disabled="alerts.page + 1 >= alerts.totalPages"
              @click="alerts.page++; loadAlerts()">Next</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>