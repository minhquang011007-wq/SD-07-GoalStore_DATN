import { apiRequest } from '@/shared/lib/api'
import type { OrderResponse } from '@/shared/lib/shop.types'

export type VoucherDefinition = {
  id: number
  code: string
  name: string
  description?: string | null
  discountPercent: number
  minOrderAmount?: number | null
  totalQuantity?: number | null
  remainingQuantity?: number | null
  isActive?: boolean | null
}

export type CustomerVoucherWalletItem = VoucherDefinition & {
  claimed: boolean
  used: boolean
  claimedAt?: string | null
  usedAt?: string | null
  usedOrderId?: number | null
  usedOrderCode?: string | null
  discountAmount: number
}

type CustomerVoucherRecord = {
  customerId: number
  voucherId: number
  claimedAt: string
  usedAt?: string | null
  usedOrderId?: number | null
  usedOrderCode?: string | null
}

const VOUCHER_DEFINITIONS_KEY = 'goalstore_voucher_definitions'
const CUSTOMER_VOUCHER_RECORDS_KEY = 'goalstore_customer_voucher_records'
const SELECTED_VOUCHER_KEY = 'goalstore_selected_voucher'

const DEFAULT_VOUCHERS: VoucherDefinition[] = [
  { id: 1, code: 'GOAL10', name: 'Voucher giảm 10%', description: 'Giảm 10% giá trị sản phẩm trong đơn hàng.', discountPercent: 10, minOrderAmount: 0, totalQuantity: 100, isActive: true },
  { id: 2, code: 'GOAL20', name: 'Voucher giảm 20%', description: 'Giảm 20% giá trị sản phẩm trong đơn hàng.', discountPercent: 20, minOrderAmount: 0, totalQuantity: 100, isActive: true },
  { id: 3, code: 'GOAL30', name: 'Voucher giảm 30%', description: 'Giảm 30% giá trị sản phẩm trong đơn hàng.', discountPercent: 30, minOrderAmount: 0, totalQuantity: 100, isActive: true },
  { id: 4, code: 'GOAL40', name: 'Voucher giảm 40%', description: 'Giảm 40% giá trị sản phẩm trong đơn hàng.', discountPercent: 40, minOrderAmount: 0, totalQuantity: 100, isActive: true },
  { id: 5, code: 'GOAL50', name: 'Voucher giảm 50%', description: 'Giảm 50% giá trị sản phẩm trong đơn hàng.', discountPercent: 50, minOrderAmount: 0, totalQuantity: 100, isActive: true },
]

function hasWindow() {
  return typeof window !== 'undefined'
}

function getStoredToken() {
  if (!hasWindow()) return ''
  return window.localStorage.getItem('token') || ''
}

function hasMockSession() {
  const token = getStoredToken()
  if (!token) return false
  return !token.includes('.') || token.startsWith('mock-') || token.startsWith('customer-') || token.startsWith('staff-')
}

function isNetworkError(error: unknown) {
  const message = error instanceof Error ? error.message.toLowerCase() : ''
  return message.includes('failed to fetch') || message.includes('networkerror') || message.includes('load failed')
}

function shouldUseLocalFallback(error?: unknown) {
  return hasMockSession() || isNetworkError(error)
}

function delay<T>(value: T, ms = 80) {
  return new Promise<T>((resolve) => setTimeout(() => resolve(value), ms))
}

function readJson<T>(key: string, fallback: T): T {
  if (!hasWindow()) return fallback
  try {
    const raw = window.localStorage.getItem(key)
    if (!raw) return fallback
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

function writeJson<T>(key: string, value: T) {
  if (!hasWindow()) return
  window.localStorage.setItem(key, JSON.stringify(value))
}

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function readVoucherDefinitions() {
  const stored = readJson<VoucherDefinition[]>(VOUCHER_DEFINITIONS_KEY, [])
  const source = Array.isArray(stored) && stored.length ? stored : DEFAULT_VOUCHERS
  return source.map((item, index) => ({
    id: Number(item.id) || index + 1,
    code: String(item.code || `GOAL${(index + 1) * 10}`).trim().toUpperCase(),
    name: String(item.name || `Voucher giảm ${(index + 1) * 10}%`).trim(),
    description: String(item.description || '').trim(),
    discountPercent: toNumber(item.discountPercent),
    minOrderAmount: toNumber(item.minOrderAmount),
    totalQuantity: Math.max(0, toNumber(item.totalQuantity || 0)),
    remainingQuantity: Math.max(0, toNumber(item.remainingQuantity || item.totalQuantity || 0)),
    isActive: item.isActive !== false,
  }))
}

function ensureVoucherDefinitions() {
  const vouchers = readVoucherDefinitions()
  if (!readJson<VoucherDefinition[]>(VOUCHER_DEFINITIONS_KEY, []).length) {
    writeJson(VOUCHER_DEFINITIONS_KEY, vouchers)
  }
  return vouchers
}

function readVoucherRecords() {
  return readJson<CustomerVoucherRecord[]>(CUSTOMER_VOUCHER_RECORDS_KEY, [])
}

function writeVoucherRecords(records: CustomerVoucherRecord[]) {
  writeJson(CUSTOMER_VOUCHER_RECORDS_KEY, records)
}

function getLocalClaimedCount(voucherId: number) {
  return readVoucherRecords().filter((item) => Number(item.voucherId) === Number(voucherId)).length
}

function normalizeWalletItem(item: any, subtotal = 0): CustomerVoucherWalletItem {
  const discountPercent = toNumber(item?.discountPercent)
  const minOrderAmount = toNumber(item?.minOrderAmount)
  const claimed = Boolean(item?.claimed)
  const used = Boolean(item?.used)
  const totalQuantity = Math.max(0, toNumber(item?.totalQuantity))
  const remainingQuantity = Math.max(0, toNumber(item?.remainingQuantity))
  const discountAmount = claimed && !used && subtotal >= minOrderAmount
    ? calculateVoucherDiscount(discountPercent, subtotal)
    : 0

  return {
    id: Number(item?.id),
    code: String(item?.code || '').trim().toUpperCase(),
    name: String(item?.name || '').trim(),
    description: item?.description || '',
    discountPercent,
    minOrderAmount,
    totalQuantity,
    remainingQuantity,
    isActive: item?.isActive !== false,
    claimed,
    used,
    claimedAt: item?.claimedAt || null,
    usedAt: item?.usedAt || null,
    usedOrderId: item?.usedOrderId == null ? null : Number(item.usedOrderId),
    usedOrderCode: item?.usedOrderCode || null,
    discountAmount,
  }
}

function getLocalWallet(customerId: number, subtotal = 0) {
  const vouchers = ensureVoucherDefinitions().filter((item) => item.isActive !== false)
  const records = readVoucherRecords()

  return vouchers.map<CustomerVoucherWalletItem>((voucher) => {
    const record = records.find((item) => Number(item.customerId) === Number(customerId) && Number(item.voucherId) === Number(voucher.id))
    const claimedCount = getLocalClaimedCount(voucher.id)
    const totalQuantity = Math.max(0, toNumber(voucher.totalQuantity))
    const remainingQuantity = Math.max(0, totalQuantity - claimedCount)

    return normalizeWalletItem({
      ...voucher,
      totalQuantity,
      remainingQuantity,
      claimed: Boolean(record),
      used: Boolean(record?.usedAt),
      claimedAt: record?.claimedAt || null,
      usedAt: record?.usedAt || null,
      usedOrderId: record?.usedOrderId || null,
      usedOrderCode: record?.usedOrderCode || null,
    }, subtotal)
  })
}

export function calculateVoucherDiscount(percent: number, subtotal: number) {
  const safeSubtotal = Math.max(0, toNumber(subtotal))
  const safePercent = Math.max(0, Math.min(100, toNumber(percent)))
  return Math.floor((safeSubtotal * safePercent) / 100)
}

export function formatVoucherDate(value?: string | null) {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''

  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}

export async function getCustomerVoucherWallet(customerId: number, subtotal = 0) {
  try {
    if (!hasMockSession()) {
      const wallet = await apiRequest<any[]>(`/api/vouchers/customer/wallet?customerId=${customerId}`)
      return wallet.map((item) => normalizeWalletItem(item, subtotal))
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return delay(getLocalWallet(customerId, subtotal))
}

export async function claimCustomerVoucher(customerId: number, voucherId: number) {
  try {
    if (!hasMockSession()) {
      const response = await apiRequest<any>('/api/vouchers/customer/claim', {
        method: 'POST',
        body: { customerId, voucherId },
      })
      return normalizeWalletItem(response)
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const vouchers = ensureVoucherDefinitions()
  const voucher = vouchers.find((item) => Number(item.id) === Number(voucherId) && item.isActive !== false)
  if (!voucher) {
    throw new Error('Voucher không tồn tại hoặc đã ngừng hoạt động.')
  }

  const records = readVoucherRecords()
  const existed = records.find((item) => Number(item.customerId) === Number(customerId) && Number(item.voucherId) === Number(voucherId))
  if (existed) {
    throw new Error('Bạn đã nhận voucher này rồi.')
  }

  const totalQuantity = Math.max(0, toNumber(voucher.totalQuantity))
  const claimedCount = getLocalClaimedCount(voucherId)
  if (claimedCount >= totalQuantity) {
    throw new Error('Voucher này đã hết lượt nhận.')
  }

  const nextRecord: CustomerVoucherRecord = {
    customerId: Number(customerId),
    voucherId: Number(voucherId),
    claimedAt: new Date().toISOString(),
    usedAt: null,
    usedOrderId: null,
    usedOrderCode: null,
  }
  writeVoucherRecords([...records, nextRecord])
  return normalizeWalletItem({
    ...voucher,
    totalQuantity,
    remainingQuantity: Math.max(0, totalQuantity - claimedCount - 1),
    claimed: true,
    used: false,
    claimedAt: nextRecord.claimedAt,
  })
}

export function setSelectedVoucherForCustomer(customerId: number, voucherId?: number | null) {
  if (!hasWindow()) return
  if (!voucherId) {
    window.localStorage.removeItem(SELECTED_VOUCHER_KEY)
    return
  }

  window.localStorage.setItem(SELECTED_VOUCHER_KEY, JSON.stringify({ customerId: Number(customerId), voucherId: Number(voucherId) }))
}

export function clearSelectedVoucherForCustomer(customerId?: number | null) {
  if (!hasWindow()) return
  if (customerId == null) {
    window.localStorage.removeItem(SELECTED_VOUCHER_KEY)
    return
  }

  const selected = readJson<{ customerId: number; voucherId: number } | null>(SELECTED_VOUCHER_KEY, null)
  if (!selected || Number(selected.customerId) === Number(customerId)) {
    window.localStorage.removeItem(SELECTED_VOUCHER_KEY)
  }
}

export function getSelectedVoucherForCustomer(customerId: number) {
  const selected = readJson<{ customerId: number; voucherId: number } | null>(SELECTED_VOUCHER_KEY, null)
  if (!selected) return null
  if (Number(selected.customerId) !== Number(customerId)) return null
  return Number(selected.voucherId)
}

export function markVoucherAsUsed(customerId: number, voucherId?: number | null, order?: OrderResponse | null) {
  if (!voucherId) return
  const records = readVoucherRecords()
  const next = records.map((item) => {
    if (Number(item.customerId) !== Number(customerId) || Number(item.voucherId) !== Number(voucherId)) {
      return item
    }
    if (item.usedAt) return item
    return {
      ...item,
      usedAt: new Date().toISOString(),
      usedOrderId: order?.id == null ? null : Number(order.id),
      usedOrderCode: order?.code || null,
    }
  })
  writeVoucherRecords(next)
  clearSelectedVoucherForCustomer(customerId)
}
