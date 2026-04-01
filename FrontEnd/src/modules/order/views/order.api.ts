import api from "@/shared/lib/api"
import type { CreateReturnRequest, OrderFilters, OrderResponse, ReturnResponse } from "./types"

export async function fetchOrders(filters: OrderFilters = {}) {
  const { data } = await api.get<OrderResponse[]>("/api/orders", { params: filters })
  return data
}

export async function fetchOrderDetail(orderId: number) {
  const { data } = await api.get<OrderResponse>(`/api/orders/${orderId}`)
  return data
}

export async function updateOrderStatus(orderId: number, status: string) {
  const { data } = await api.patch<OrderResponse>(`/api/orders/${orderId}/status`, { status })
  return data
}

export async function updateOrderItem(orderId: number, itemId: number, quantity: number) {
  const { data } = await api.patch<OrderResponse>(`/api/orders/${orderId}/items/${itemId}`, { quantity })
  return data
}

export async function cancelOrder(orderId: number) {
  const { data } = await api.delete<OrderResponse | string>(`/api/orders/${orderId}`)
  return data
}

export async function fetchReturns() {
  const { data } = await api.get<ReturnResponse[]>("/api/returns")
  return data
}

export async function createReturn(orderId: number, payload: CreateReturnRequest) {
  const { data } = await api.post<ReturnResponse>(`/api/returns/order/${orderId}`, payload)
  return data
}
