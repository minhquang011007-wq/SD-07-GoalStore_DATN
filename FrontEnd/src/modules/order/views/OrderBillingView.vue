<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue"
import {
  AlertCircle,
  CheckCircle2,
  ClipboardList,
  LoaderCircle,
  PackageCheck,
  Receipt,
  RefreshCcw,
  RotateCcw,
  Search,
  Truck,
  XCircle,
} from "lucide-vue-next"
import {
  cancelOrder,
  createReturn,
  fetchOrderDetail,
  fetchOrders,
  fetchReturns,
  updateOrderItem,
  updateOrderStatus,
} from "./order.api"
import type { OrderDetailResponse, OrderResponse, OrderStatus, ReturnResponse } from "./types"

const STATUS_OPTIONS: Array<{ label: string; value: OrderStatus }> = [
  { label: "Mới", value: "MOI" },
  { label: "Đang xử lý", value: "DANG_XU_LY" },
  { label: "Đang giao", value: "DANG_GIAO" },
  { label: "Hoàn tất", value: "HOAN_TAT" },
]

const CHANNEL_OPTIONS = [
  { label: "Tất cả kênh", value: "" },
  { label: "Online", value: "ONLINE" },
  { label: "Offline", value: "OFFLINE" },
]

const TAB_OPTIONS = [
  { label: "Đơn hàng", value: "orders" },
  { label: "Phiếu trả hàng", value: "returns" },
] as const

const activeTab = ref<(typeof TAB_OPTIONS)[number]["value"]>("orders")
const loadingOrders = ref(false)
const loadingReturns = ref(false)
const loadingDetail = ref(false)
const actionLoading = ref(false)
const errorMessage = ref("")
const successMessage = ref("")

const orders = ref<OrderResponse[]>([])
const returns = ref<ReturnResponse[]>([])
const selectedOrderId = ref<number | null>(null)
const selectedOrder = ref<OrderDetailResponse | null>(null)
const quantityDrafts = ref<Record<number, number>>({})

const filters = reactive({
  keyword: "",
  status: "",
  channel: "",
  date: "",
  customerId: "",
})

const detailState = reactive({
  status: "" as OrderStatus | "",
  returnReason: "",
  returnNote: "",
})

const summaryCards = computed(() => {
  const list = filteredOrders.value
  return [
    { label: "Tổng đơn", value: list.length, icon: ClipboardList },
    { label: "Đơn mới", value: list.filter((item) => item.status === "MOI").length, icon: Receipt },
    { label: "Đang giao", value: list.filter((item) => item.status === "DANG_GIAO").length, icon: Truck },
    { label: "Hoàn tất", value: list.filter((item) => item.status === "HOAN_TAT").length, icon: PackageCheck },
  ]
})

const filteredOrders = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  let list = [...orders.value]

  if (filters.channel) {
    list = list.filter((order) => String(order.channel || "").toUpperCase() === filters.channel)
  }

  if (filters.date) {
    list = list.filter((order) => {
      if (!order.orderDate) return false
      return String(order.orderDate).slice(0, 10) === filters.date
    })
  }

  if (!keyword) return list

  return list.filter((order) => {
    const values = [
      order.code,
      order.customerName,
      order.receiverName,
      order.receiverPhone,
      order.shippingAddress,
    ]
    return values.some((value) => String(value || "").toLowerCase().includes(keyword))
  })
})

const canEditItems = computed(() => {
  const status = selectedOrder.value?.status
  return status === "MOI" || status === "DANG_XU_LY"
})

const canCancelOrder = computed(() => {
  const status = selectedOrder.value?.status
  return status === "MOI" || status === "DANG_XU_LY" || status === "DANG_GIAO"
})

const canCreateReturn = computed(() => {
  const status = selectedOrder.value?.status
  return status === "HOAN_TAT" || status === "DANG_GIAO"
})

watch(selectedOrder, (order) => {
  detailState.status = getNextStatus(order?.status) || ""
  detailState.returnReason = ""
  detailState.returnNote = ""
  quantityDrafts.value = {}

  for (const item of order?.items || []) {
    quantityDrafts.value[item.itemId] = item.quantity
  }
})

function getNextStatus(status?: string | null): OrderStatus | "" {
  switch (status) {
    case "MOI":
      return "DANG_XU_LY"
    case "DANG_XU_LY":
      return "DANG_GIAO"
    case "DANG_GIAO":
      return "HOAN_TAT"
    default:
      return ""
  }
}

function setMessage(type: "success" | "error", message: string) {
  if (type === "success") {
    successMessage.value = message
    errorMessage.value = ""
  } else {
    errorMessage.value = message
    successMessage.value = ""
  }
}

function clearMessage() {
  successMessage.value = ""
  errorMessage.value = ""
}

function formatCurrency(value: number | null | undefined) {
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(value || 0))
}

function formatDate(value: string | null | undefined) {
  if (!value) return "--"
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date)
}

function labelStatus(status: string | null | undefined) {
  switch (status) {
    case "MOI":
      return "Mới"
    case "DANG_XU_LY":
      return "Đang xử lý"
    case "DANG_GIAO":
      return "Đang giao"
    case "HOAN_TAT":
      return "Hoàn tất"
    case "HUY":
      return "Đã hủy"
    case "TRA_HANG":
      return "Trả hàng"
    default:
      return status || "--"
  }
}

function statusClass(status: string | null | undefined) {
  switch (status) {
    case "MOI":
      return "bg-blue-50 text-blue-700 border-blue-200"
    case "DANG_XU_LY":
      return "bg-amber-50 text-amber-700 border-amber-200"
    case "DANG_GIAO":
      return "bg-violet-50 text-violet-700 border-violet-200"
    case "HOAN_TAT":
      return "bg-emerald-50 text-emerald-700 border-emerald-200"
    case "HUY":
      return "bg-rose-50 text-rose-700 border-rose-200"
    case "TRA_HANG":
      return "bg-orange-50 text-orange-700 border-orange-200"
    default:
      return "bg-slate-50 text-slate-700 border-slate-200"
  }
}

function normalizeSummaryOrder(order: OrderResponse): OrderResponse {
  return {
    ...order,
    code: order.code || `#${order.id}`,
    subtotal: Number(order.subtotal ?? order.total ?? 0),
    shippingFee: Number(order.shippingFee ?? 0),
    discountAmount: Number(order.discountAmount ?? 0),
    total: Number(order.total ?? 0),
  }
}

async function loadOrders() {
  loadingOrders.value = true
  clearMessage()

  try {
    const data = await fetchOrders({
      status: filters.status || undefined,
      customerId: filters.customerId ? Number(filters.customerId) : undefined,
      channel: filters.channel || undefined,
      date: filters.date || undefined,
    })

    orders.value = data
      .map(normalizeSummaryOrder)
      .sort((a, b) => new Date(b.orderDate || 0).getTime() - new Date(a.orderDate || 0).getTime())

    const stillExists = selectedOrderId.value && orders.value.some((item) => item.id === selectedOrderId.value)
    if (!stillExists) {
      selectedOrderId.value = orders.value[0]?.id ?? null
    }

    if (selectedOrderId.value) {
      await loadOrderDetail(selectedOrderId.value)
    } else {
      selectedOrder.value = null
    }
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Không tải được danh sách đơn hàng")
  } finally {
    loadingOrders.value = false
  }
}

async function loadOrderDetail(orderId: number) {
  selectedOrderId.value = orderId
  loadingDetail.value = true

  try {
    const detail = await fetchOrderDetail(orderId)

    selectedOrder.value = {
      ...detail,
      code: detail.code || `#${detail.id}`,
      subtotal: Number(
        detail.subtotal ??
          (detail.items || []).reduce((sum, item) => sum + Number(item.lineTotal || 0), 0),
      ),
      shippingFee: Number(detail.shippingFee ?? 0),
      discountAmount: Number(detail.discountAmount ?? 0),
      total: Number(detail.total ?? 0),
      items: (detail.items || []).map((item) => ({
        ...item,
        itemId: Number(item.itemId),
        lineTotal: Number(item.lineTotal ?? Number(item.unitPrice || 0) * Number(item.quantity || 0)),
      })),
    }
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Không tải được chi tiết đơn hàng")
  } finally {
    loadingDetail.value = false
  }
}

async function loadReturns() {
  loadingReturns.value = true

  try {
    returns.value = await fetchReturns()
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Không tải được phiếu trả hàng")
  } finally {
    loadingReturns.value = false
  }
}

async function handleUpdateStatus() {
  if (!selectedOrder.value || !detailState.status) return

  actionLoading.value = true
  clearMessage()

  try {
    const updated = await updateOrderStatus(selectedOrder.value.id, detailState.status)
    selectedOrder.value = updated

    const index = orders.value.findIndex((item) => item.id === updated.id)
    if (index >= 0) {
      orders.value[index] = { ...orders.value[index], ...updated, code: updated.code || `#${updated.id}` }
    }

    setMessage("success", "Đã cập nhật trạng thái đơn hàng")
    await Promise.all([loadOrderDetail(updated.id), loadReturns()])
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Cập nhật trạng thái thất bại")
  } finally {
    actionLoading.value = false
  }
}

async function handleUpdateItem(itemId: number) {
  if (!selectedOrder.value) return

  const newQuantity = Number(quantityDrafts.value[itemId])
  if (!Number.isInteger(newQuantity) || newQuantity <= 0) {
    setMessage("error", "Số lượng sản phẩm phải lớn hơn 0")
    return
  }

  actionLoading.value = true
  clearMessage()

  try {
    const updated = await updateOrderItem(selectedOrder.value.id, selectedOrder.value, itemId, newQuantity)
    selectedOrder.value = updated

    const index = orders.value.findIndex((item) => item.id === updated.id)
    if (index >= 0) {
      orders.value[index] = { ...orders.value[index], ...updated, code: updated.code || `#${updated.id}` }
    }

    setMessage("success", "Đã cập nhật số lượng sản phẩm trong đơn")
    await loadOrderDetail(updated.id)
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Cập nhật sản phẩm thất bại")
  } finally {
    actionLoading.value = false
  }
}

async function handleCancelOrder() {
  if (!selectedOrder.value) return

  const confirmed = window.confirm(`Xác nhận hủy đơn ${selectedOrder.value.code}?`)
  if (!confirmed) return

  actionLoading.value = true
  clearMessage()

  try {
    const updated = await cancelOrder(selectedOrder.value.id)
    selectedOrder.value = updated
    setMessage("success", "Đã hủy đơn hàng và hoàn lại tồn kho")
    await Promise.all([loadOrders(), loadReturns()])
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Hủy đơn hàng thất bại")
  } finally {
    actionLoading.value = false
  }
}

async function handleCreateReturn() {
  if (!selectedOrder.value) return
  if (!detailState.returnReason.trim()) {
    setMessage("error", "Nhập lý do trả hàng trước khi tạo phiếu trả")
    return
  }

  actionLoading.value = true
  clearMessage()

  try {
    await createReturn(selectedOrder.value.id, {
      reason: detailState.returnReason.trim(),
      note: detailState.returnNote.trim() || undefined,
      refundTotal: Number(selectedOrder.value.total || 0),
    })

    setMessage("success", "Đã tạo phiếu trả hàng")
    await Promise.all([loadOrders(), loadReturns()])

    if (selectedOrderId.value) {
      await loadOrderDetail(selectedOrderId.value)
    }

    activeTab.value = "returns"
  } catch (error: any) {
    setMessage("error", error?.response?.data?.message || error?.message || "Tạo phiếu trả hàng thất bại")
  } finally {
    actionLoading.value = false
  }
}

async function refreshCurrentTab() {
  if (activeTab.value === "orders") {
    await loadOrders()
    return
  }
  await loadReturns()
}

onMounted(async () => {
  await Promise.all([loadOrders(), loadReturns()])
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <h1 class="text-2xl font-semibold">Quản lý đơn hàng</h1>
        <p class="text-sm text-slate-600">
          Admin theo dõi danh sách đơn, cập nhật trạng thái, chỉnh sửa số lượng sản phẩm, hủy đơn và tạo phiếu trả hàng.
        </p>
      </div>
      <button
        class="inline-flex items-center gap-2 rounded-md border px-4 py-2 text-sm font-medium hover:bg-slate-50"
        :disabled="loadingOrders || loadingReturns || loadingDetail || actionLoading"
        @click="refreshCurrentTab"
      >
        <RefreshCcw :size="16" :class="(loadingOrders || loadingReturns || loadingDetail) ? 'animate-spin' : ''" />
        Làm mới dữ liệu
      </button>
    </div>

    <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
      <div v-for="card in summaryCards" :key="card.label" class="rounded-xl border bg-white p-4 shadow-sm">
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="text-sm text-slate-500">{{ card.label }}</div>
            <div class="mt-1 text-2xl font-semibold text-slate-900">{{ card.value }}</div>
          </div>
          <div class="rounded-full bg-slate-100 p-3 text-slate-700">
            <component :is="card.icon" :size="18" />
          </div>
        </div>
      </div>
    </div>

    <div class="flex flex-wrap gap-2">
      <button
        v-for="tab in TAB_OPTIONS"
        :key="tab.value"
        class="rounded-md border px-4 py-2 text-sm font-medium"
        :class="activeTab === tab.value ? 'bg-primary text-white border-primary' : 'hover:bg-slate-50'"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="successMessage" class="flex items-start gap-2 rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
      <CheckCircle2 :size="18" class="mt-0.5 shrink-0" />
      <span>{{ successMessage }}</span>
    </div>

    <div v-if="errorMessage" class="flex items-start gap-2 rounded-md border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
      <AlertCircle :size="18" class="mt-0.5 shrink-0" />
      <span>{{ errorMessage }}</span>
    </div>

    <template v-if="activeTab === 'orders'">
      <div class="rounded-xl border bg-white p-4 shadow-sm">
        <div class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-5">
          <label class="space-y-1 xl:col-span-2">
            <span class="text-xs font-medium text-slate-500">Tìm nhanh</span>
            <div class="relative">
              <Search :size="16" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                v-model="filters.keyword"
                type="text"
                placeholder="Mã đơn, khách hàng, số điện thoại..."
                class="w-full rounded-md border px-3 py-2 pl-9 outline-none focus:border-primary"
              />
            </div>
          </label>

          <label class="space-y-1">
            <span class="text-xs font-medium text-slate-500">Trạng thái</span>
            <select v-model="filters.status" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary">
              <option value="">Tất cả trạng thái</option>
              <option value="MOI">Mới</option>
              <option value="DANG_XU_LY">Đang xử lý</option>
              <option value="DANG_GIAO">Đang giao</option>
              <option value="HOAN_TAT">Hoàn tất</option>
              <option value="HUY">Đã hủy</option>
              <option value="TRA_HANG">Trả hàng</option>
            </select>
          </label>

          <label class="space-y-1">
            <span class="text-xs font-medium text-slate-500">Kênh bán</span>
            <select v-model="filters.channel" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary">
              <option v-for="option in CHANNEL_OPTIONS" :key="option.value" :value="option.value">{{ option.label }}</option>
            </select>
          </label>

          <label class="space-y-1">
            <span class="text-xs font-medium text-slate-500">Ngày đặt</span>
            <input v-model="filters.date" type="date" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" />
          </label>

          <label class="space-y-1">
            <span class="text-xs font-medium text-slate-500">Mã khách hàng</span>
            <input
              v-model="filters.customerId"
              type="number"
              min="1"
              placeholder="Ví dụ: 12"
              class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary"
            />
          </label>

          <div class="flex items-end gap-2 xl:col-span-2">
            <button class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-white" :disabled="loadingOrders" @click="loadOrders">
              {{ loadingOrders ? 'Đang tải...' : 'Áp dụng bộ lọc' }}
            </button>
            <button
              class="rounded-md border px-4 py-2 text-sm font-medium hover:bg-slate-50"
              @click="filters.keyword=''; filters.status=''; filters.channel=''; filters.date=''; filters.customerId=''; loadOrders()"
            >
              Xóa lọc
            </button>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <div class="rounded-xl border bg-white shadow-sm">
          <div class="flex items-center justify-between border-b px-4 py-3">
            <div>
              <div class="font-semibold text-slate-900">Danh sách đơn hàng</div>
              <div class="text-sm text-slate-500">{{ filteredOrders.length }} đơn phù hợp</div>
            </div>
            <div v-if="loadingOrders" class="flex items-center gap-2 text-sm text-slate-500">
              <LoaderCircle :size="16" class="animate-spin" />
              Đang tải...
            </div>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full text-sm">
              <thead class="bg-slate-50 text-left text-slate-500">
                <tr>
                  <th class="px-4 py-3 font-medium">Mã đơn</th>
                  <th class="px-4 py-3 font-medium">Khách hàng</th>
                  <th class="px-4 py-3 font-medium">Trạng thái</th>
                  <th class="px-4 py-3 font-medium">Thanh toán</th>
                  <th class="px-4 py-3 font-medium">Tổng tiền</th>
                  <th class="px-4 py-3 font-medium">Ngày đặt</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="!filteredOrders.length">
                  <td colspan="6" class="px-4 py-8 text-center text-slate-500">Chưa có đơn hàng phù hợp.</td>
                </tr>
                <tr
                  v-for="order in filteredOrders"
                  :key="order.id"
                  class="cursor-pointer border-t hover:bg-slate-50"
                  :class="selectedOrderId === order.id ? 'bg-slate-50' : ''"
                  @click="loadOrderDetail(order.id)"
                >
                  <td class="px-4 py-3 font-medium text-slate-900">{{ order.code }}</td>
                  <td class="px-4 py-3">
                    <div>{{ order.customerName || '--' }}</div>
                    <div class="text-xs text-slate-500">#{{ order.customerId || '--' }}</div>
                  </td>
                  <td class="px-4 py-3">
                    <span class="inline-flex rounded-full border px-2.5 py-1 text-xs font-medium" :class="statusClass(order.status)">
                      {{ labelStatus(order.status) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <div>{{ order.paymentMethod || '--' }}</div>
                    <div class="text-xs text-slate-500">{{ order.paymentStatus || '--' }}</div>
                  </td>
                  <td class="px-4 py-3 font-medium">{{ formatCurrency(order.total) }}</td>
                  <td class="px-4 py-3 text-slate-600">{{ formatDate(order.orderDate) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="rounded-xl border bg-white shadow-sm">
          <div class="flex items-center justify-between border-b px-4 py-3">
            <div>
              <div class="font-semibold text-slate-900">Chi tiết đơn hàng</div>
              <div class="text-sm text-slate-500">Xem nhanh và thao tác trực tiếp trên đơn</div>
            </div>
            <div v-if="loadingDetail" class="flex items-center gap-2 text-sm text-slate-500">
              <LoaderCircle :size="16" class="animate-spin" />
              Đang tải...
            </div>
          </div>

          <div v-if="!selectedOrder" class="p-6 text-sm text-slate-500">
            Chọn một đơn hàng ở bảng bên trái để xem chi tiết.
          </div>

          <div v-else class="space-y-5 p-4">
            <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Mã đơn</div>
                <div class="mt-1 font-semibold text-slate-900">{{ selectedOrder.code }}</div>
              </div>
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Khách hàng</div>
                <div class="mt-1 font-semibold text-slate-900">{{ selectedOrder.customerName || '--' }}</div>
                <div class="text-xs text-slate-500">Mã KH: {{ selectedOrder.customerId || '--' }}</div>
              </div>
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Người nhận</div>
                <div class="mt-1 font-semibold text-slate-900">{{ selectedOrder.receiverName || '--' }}</div>
                <div class="text-xs text-slate-500">{{ selectedOrder.receiverPhone || '--' }}</div>
              </div>
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Kênh bán</div>
                <div class="mt-1 font-semibold text-slate-900">{{ selectedOrder.channel || '--' }}</div>
                <div class="text-xs text-slate-500">{{ formatDate(selectedOrder.orderDate) }}</div>
              </div>
            </div>

            <div class="rounded-lg border p-3">
              <div class="text-xs text-slate-500">Địa chỉ giao hàng</div>
              <div class="mt-1 text-sm text-slate-800">{{ selectedOrder.shippingAddress || '--' }}</div>
              <div v-if="selectedOrder.note" class="mt-2 text-xs text-slate-500">Ghi chú: {{ selectedOrder.note }}</div>
            </div>

            <div class="rounded-lg border p-3 space-y-3">
              <div class="flex flex-wrap items-center gap-2 justify-between">
                <div>
                  <div class="text-sm font-semibold text-slate-900">Trạng thái đơn hàng</div>
                  <div class="text-xs text-slate-500">Cập nhật đúng luồng: Mới → Đang xử lý → Đang giao → Hoàn tất</div>
                </div>
                <span class="inline-flex rounded-full border px-2.5 py-1 text-xs font-medium" :class="statusClass(selectedOrder.status)">
                  {{ labelStatus(selectedOrder.status) }}
                </span>
              </div>

              <div class="flex flex-col gap-2 sm:flex-row">
                <select
                  v-model="detailState.status"
                  class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary"
                  :disabled="!getNextStatus(selectedOrder.status)"
                >
                  <option value="">Không có bước tiếp theo</option>
                  <option v-for="option in STATUS_OPTIONS" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>

                <button
                  class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-60"
                  :disabled="!detailState.status || actionLoading || !getNextStatus(selectedOrder.status)"
                  @click="handleUpdateStatus"
                >
                  Cập nhật trạng thái
                </button>

                <button
                  v-if="canCancelOrder"
                  class="rounded-md border border-rose-200 px-4 py-2 text-sm font-medium text-rose-600 hover:bg-rose-50 disabled:cursor-not-allowed disabled:opacity-60"
                  :disabled="actionLoading"
                  @click="handleCancelOrder"
                >
                  Hủy đơn
                </button>
              </div>
            </div>

            <div class="rounded-lg border">
              <div class="border-b px-3 py-2">
                <div class="font-semibold text-slate-900">Sản phẩm trong đơn</div>
                <div class="text-xs text-slate-500">Admin có thể chỉnh số lượng khi đơn còn mới hoặc đang xử lý</div>
              </div>

              <div class="overflow-x-auto">
                <table class="min-w-full text-sm">
                  <thead class="bg-slate-50 text-left text-slate-500">
                    <tr>
                      <th class="px-3 py-2 font-medium">Sản phẩm</th>
                      <th class="px-3 py-2 font-medium">SKU</th>
                      <th class="px-3 py-2 font-medium">Đơn giá</th>
                      <th class="px-3 py-2 font-medium">Số lượng</th>
                      <th class="px-3 py-2 font-medium">Thành tiền</th>
                      <th class="px-3 py-2 font-medium">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in selectedOrder.items" :key="item.itemId" class="border-t">
                      <td class="px-3 py-3">
                        <div class="font-medium text-slate-900">{{ item.productName || '--' }}</div>
                        <div class="text-xs text-slate-500">
                          {{ item.color || '--' }} / {{ item.size || '--' }}
                        </div>
                      </td>
                      <td class="px-3 py-3 font-medium text-slate-900">{{ item.sku || 'Không có SKU' }}</td>
                      <td class="px-3 py-3">{{ formatCurrency(item.unitPrice) }}</td>
                      <td class="px-3 py-3">
                        <input
                          v-model.number="quantityDrafts[item.itemId]"
                          type="number"
                          min="1"
                          :disabled="!canEditItems"
                          class="w-20 rounded-md border px-2 py-1 outline-none focus:border-primary disabled:bg-slate-100"
                        />
                      </td>
                      <td class="px-3 py-3 font-medium">{{ formatCurrency(item.lineTotal) }}</td>
                      <td class="px-3 py-3">
                        <button
                          class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                          :disabled="!canEditItems || actionLoading"
                          @click="handleUpdateItem(item.itemId)"
                        >
                          Lưu số lượng
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Tạm tính</div>
                <div class="mt-1 font-semibold">{{ formatCurrency(selectedOrder.subtotal) }}</div>
              </div>
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Phí ship</div>
                <div class="mt-1 font-semibold">{{ formatCurrency(selectedOrder.shippingFee) }}</div>
              </div>
              <div class="rounded-lg border p-3">
                <div class="text-xs text-slate-500">Giảm giá</div>
                <div class="mt-1 font-semibold">{{ formatCurrency(selectedOrder.discountAmount) }}</div>
              </div>
              <div class="rounded-lg border bg-slate-50 p-3">
                <div class="text-xs text-slate-500">Tổng thanh toán</div>
                <div class="mt-1 text-lg font-semibold text-slate-900">{{ formatCurrency(selectedOrder.total) }}</div>
              </div>
            </div>

            <div v-if="canCreateReturn" class="rounded-lg border border-orange-200 bg-orange-50 p-4 space-y-3">
              <div class="flex items-start gap-3">
                <RotateCcw :size="18" class="mt-0.5 text-orange-600" />
                <div>
                  <div class="font-semibold text-orange-900">Tạo phiếu trả hàng</div>
                  <div class="text-sm text-orange-700">Chỉ dùng khi đơn đang giao hoặc đã hoàn tất.</div>
                </div>
              </div>

              <div class="grid grid-cols-1 gap-3">
                <label class="space-y-1">
                  <span class="text-xs font-medium text-orange-800">Lý do trả hàng</span>
                  <input
                    v-model="detailState.returnReason"
                    type="text"
                    placeholder="Ví dụ: Sai size, lỗi sản phẩm..."
                    class="w-full rounded-md border border-orange-200 px-3 py-2 outline-none focus:border-orange-400"
                  />
                </label>
                <label class="space-y-1">
                  <span class="text-xs font-medium text-orange-800">Ghi chú</span>
                  <textarea
                    v-model="detailState.returnNote"
                    rows="3"
                    placeholder="Thông tin bổ sung cho phiếu trả hàng"
                    class="w-full rounded-md border border-orange-200 px-3 py-2 outline-none focus:border-orange-400"
                  />
                </label>
              </div>

              <button
                class="rounded-md bg-orange-600 px-4 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="actionLoading"
                @click="handleCreateReturn"
              >
                Tạo phiếu trả hàng
              </button>
            </div>

            <div v-if="selectedOrder.status === 'TRA_HANG'" class="rounded-lg border border-orange-200 bg-orange-50 p-4 text-sm text-orange-800">
              Đơn hàng này đã được chuyển sang trạng thái trả hàng. Xem tab <strong>Phiếu trả hàng</strong>.
            </div>

            <div v-if="selectedOrder.status === 'HUY'" class="rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">
              Đơn hàng đã bị hủy. Tồn kho đã được hoàn lại theo logic backend.
            </div>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="rounded-xl border bg-white shadow-sm">
        <div class="flex items-center justify-between border-b px-4 py-3">
          <div>
            <div class="font-semibold text-slate-900">Danh sách phiếu trả hàng</div>
            <div class="text-sm text-slate-500">Theo dõi các đơn đã tạo yêu cầu hoàn trả</div>
          </div>
          <div v-if="loadingReturns" class="flex items-center gap-2 text-sm text-slate-500">
            <LoaderCircle :size="16" class="animate-spin" />
            Đang tải...
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="min-w-full text-sm">
            <thead class="bg-slate-50 text-left text-slate-500">
              <tr>
                <th class="px-4 py-3 font-medium">Mã phiếu</th>
                <th class="px-4 py-3 font-medium">Đơn hàng</th>
                <th class="px-4 py-3 font-medium">Lý do</th>
                <th class="px-4 py-3 font-medium">Ghi chú</th>
                <th class="px-4 py-3 font-medium">Hoàn tiền</th>
                <th class="px-4 py-3 font-medium">Ngày tạo</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!returns.length">
                <td colspan="6" class="px-4 py-8 text-center text-slate-500">Chưa có phiếu trả hàng nào.</td>
              </tr>
              <tr v-for="item in returns" :key="item.id" class="border-t">
                <td class="px-4 py-3 font-medium text-slate-900">#RT{{ item.id }}</td>
                <td class="px-4 py-3">#{{ item.orderId }}</td>
                <td class="px-4 py-3">{{ item.reason }}</td>
                <td class="px-4 py-3 text-slate-600">{{ item.note || '--' }}</td>
                <td class="px-4 py-3 font-medium">{{ formatCurrency(item.refundTotal) }}</td>
                <td class="px-4 py-3 text-slate-600">{{ formatDate(item.returnDate) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <div class="rounded-xl border border-dashed bg-slate-50 p-4 text-sm text-slate-600">
      <div class="flex items-start gap-2">
        <XCircle :size="16" class="mt-0.5 text-slate-500" />
        <div>
          Luồng đúng: <strong>Mới → Đang xử lý → Đang giao → Hoàn tất</strong>. Hủy đơn dùng nút
          <strong>Hủy đơn</strong>. Trả hàng dùng khối <strong>Tạo phiếu trả hàng</strong>.
        </div>
      </div>
    </div>
  </div>
</template>