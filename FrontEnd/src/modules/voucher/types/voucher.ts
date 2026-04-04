export type VoucherItem = {
  id: number
  code: string
  name: string
  description?: string | null
  discountPercent: number
  minOrderAmount: number
  totalQuantity: number
  remainingQuantity?: number
  isActive: boolean
  isDeleted?: boolean
  createdAt?: string | null
  updatedAt?: string | null
  claimedCount?: number
  usedCount?: number
}

export type VoucherPayload = {
  code: string
  name: string
  description?: string | null
  discountPercent: number
  minOrderAmount: number
  totalQuantity: number
  isActive: boolean
}
