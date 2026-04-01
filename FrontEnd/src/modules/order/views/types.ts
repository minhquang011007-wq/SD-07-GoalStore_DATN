export type OrderStatus = "MOI" | "DANG_XU_LY" | "DANG_GIAO" | "HOAN_TAT" | "HUY" | "TRA_HANG"
export type OrderChannel = "ONLINE" | "OFFLINE"
export type PaymentMethod = "COD" | "BANKING" | "MOMO" | "VNPAY"

export type OrderItemResponse = {
  itemId: number
  variantId: number | null
  sku: string | null
  quantity: number
  unitPrice: number
  lineTotal: number
}

export type OrderResponse = {
  id: number
  code: string
  customerId: number | null
  customerName: string | null
  staffId: number | null
  staffUsername: string | null
  status: OrderStatus | string
  paymentMethod: PaymentMethod | string | null
  paymentStatus: string | null
  channel: OrderChannel | string | null
  receiverName: string | null
  receiverPhone: string | null
  shippingAddress: string | null
  note: string | null
  subtotal: number
  shippingFee: number
  discountAmount: number
  total: number
  orderDate: string | null
  items: OrderItemResponse[]
}

export type ReturnResponse = {
  id: number
  orderId: number
  reason: string
  note: string | null
  refundTotal: number
  returnDate: string | null
}

export type OrderFilters = {
  status?: string
  customerId?: number
  channel?: string
  date?: string
}

export type CreateReturnRequest = {
  reason: string
  note?: string
}
