// order.api.ts
import api from "@/shared/lib/api"
import type {
  CreateReturnRequest,
  OrderDetailResponse,
  OrderFilters,
  OrderResponse,
  ReturnResponse,
  UpdateOrderRequest,
} from "./types"

function normalizeFilters(filters: OrderFilters = {}) {
  const params: Record<string, string | number> = {}

  if (filters.status) params.status = filters.status
  if (filters.customerId) params.customerId = filters.customerId

  return params
}

function mapOrderSummary(data: any): OrderResponse {
  return {
    id: Number(data?.id || 0),
    code: data?.code || `#${data?.id ?? "--"}`,
    customerId: data?.customerId ?? null,
    customerName: data?.customerName ?? null,
    status: data?.status || "MOI",
    paymentMethod: data?.paymentMethod ?? null,
    paymentStatus: data?.paymentStatus ?? null,
    channel: data?.channel ?? null,
    receiverName: data?.receiverName ?? null,
    receiverPhone: data?.receiverPhone ?? null,
    shippingAddress: data?.shippingAddress ?? null,
    note: data?.note ?? null,
    subtotal: Number(data?.subtotal ?? data?.total ?? 0),
    shippingFee: Number(data?.shippingFee ?? 0),
    discountAmount: Number(data?.discountAmount ?? 0),
    voucherId: data?.voucherId == null ? null : Number(data.voucherId),
    voucherCode: data?.voucherCode ?? null,
    voucherName: data?.voucherName ?? null,
    voucherPercent: data?.voucherPercent == null ? null : Number(data.voucherPercent),
    total: Number(data?.total ?? 0),
    orderDate: data?.orderDate ?? null,
    items: Array.isArray(data?.items)
      ? data.items.map((item: any) => ({
          itemId: Number(item?.itemId ?? item?.id ?? 0),
          variantId: item?.variantId ?? null,
          sku: item?.sku ?? item?.variantSku ?? null,
          productName: item?.productName ?? null,
          size: item?.size ?? null,
          color: item?.color ?? null,
          quantity: Number(item?.quantity ?? 0),
          unitPrice: Number(item?.unitPrice ?? 0),
          lineTotal: Number(item?.lineTotal ?? 0),
        }))
      : [],
    totalItems: Number(data?.totalItems ?? 0),
    returnInfo: data?.returnInfo
      ? {
          id: Number(data.returnInfo.id || 0),
          orderId: Number(data.returnInfo.orderId || 0),
          reason: data.returnInfo.reason || "",
          note: data.returnInfo.note ?? null,
          refundTotal: Number(data.returnInfo.refundTotal ?? 0),
          returnDate: data.returnInfo.returnDate ?? null,
        }
      : null,
  }
}

export async function fetchOrders(filters: OrderFilters = {}) {
  const { data } = await api.get<any[]>("/api/orders", {
    params: normalizeFilters(filters),
  })

  let list = (data || []).map(mapOrderSummary)

  if (filters.channel) {
    list = list.filter((item) => String(item.channel || "").toUpperCase() === String(filters.channel).toUpperCase())
  }

  if (filters.date) {
    list = list.filter((item) => String(item.orderDate || "").slice(0, 10) === filters.date)
  }

  return list
}

export async function fetchOrderDetail(orderId: number) {
  const { data } = await api.get<any>(`/api/orders/${orderId}`)
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function updateOrderStatus(orderId: number, status: string) {
  const { data } = await api.put<any>(`/api/orders/${orderId}/status`, { status })
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function updateOrderPaymentStatus(orderId: number, paymentStatus: string) {
  const { data } = await api.put<any>(`/api/orders/${orderId}/payment-status`, { paymentStatus })
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function updateOrder(orderId: number, payload: UpdateOrderRequest) {
  const { data } = await api.put<any>(`/api/orders/${orderId}`, payload)
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function updateOrderItem(orderId: number, order: OrderDetailResponse, itemId: number, quantity: number) {
  const items = (order.items || []).map((item) => ({
    variantId: Number(item.variantId),
    quantity: item.itemId === itemId ? quantity : item.quantity,
  }))

  const payload: UpdateOrderRequest = {
    paymentMethod: order.paymentMethod || undefined,
    channel: order.channel || undefined,
    items,
  }

  return await updateOrder(orderId, payload)
}

export async function cancelOrder(orderId: number) {
  const { data } = await api.put<any>(`/api/orders/${orderId}/cancel`)
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function createReturn(orderId: number, payload: CreateReturnRequest) {
  const { data } = await api.post<any>(`/api/orders/${orderId}/return`, payload)
  return mapOrderSummary(data) as OrderDetailResponse
}

export async function fetchReturns() {
  const orders = await fetchOrders()
  const returnOrders = orders.filter((item) => item.status === "TRA_HANG" || item.returnInfo)

  const details = await Promise.all(returnOrders.map((item) => fetchOrderDetail(item.id)))

  const returns: ReturnResponse[] = details
    .filter((item) => item.returnInfo)
    .map((item) => ({
      id: Number(item.returnInfo!.id),
      orderId: Number(item.returnInfo!.orderId),
      reason: item.returnInfo!.reason,
      note: item.returnInfo!.note ?? null,
      refundTotal: Number(item.returnInfo!.refundTotal ?? 0),
      returnDate: item.returnInfo!.returnDate ?? null,
    }))

  return returns.sort(
    (a, b) => new Date(b.returnDate || 0).getTime() - new Date(a.returnDate || 0).getTime(),
  )
}