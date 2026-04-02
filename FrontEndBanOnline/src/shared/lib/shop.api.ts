import { apiRequest } from '@/shared/lib/api'
import type {
  CartItem,
  CartResponse,
  CheckoutOrderRequest,
  CustomerAddress,
  CustomerAddressPayload,
  OrderResponse,
  PageResponse,
  PublicProductDetail,
  PublicProductSearchParams,
  PublicProductSummary,
  ProductCategory,
  ReturnRequest,
  ReturnResponse,
} from '@/shared/lib/shop.types'
import {
  findProductById,
  findVariantById,
  getSummaryFromDetail,
  getVariantFinalPrice,
  mockProductSummaries,
  toNumber,
} from '@/shared/mock/shop.mock'

const API_URL = (import.meta.env.VITE_API_URL as string | undefined) || 'http://localhost:8080'
const CART_STORAGE_KEY = 'goalstore_cart'
const ADDRESS_STORAGE_KEY = 'goalstore_customer_addresses'
const ORDER_STORAGE_KEY = 'goalstore_customer_orders'
const RETURN_STORAGE_KEY = 'goalstore_order_returns'
const LAST_ORDER_STORAGE_KEY = 'goalstore_last_order'
const CART_SELECTED_STORAGE_KEY = 'goalstore_cart_selected_items'

type LocalCartLine = {
  itemId: number
  variantId: number
  quantity: number
}

function delay<T>(value: T, ms = 80) {
  return new Promise<T>((resolve) => setTimeout(() => resolve(value), ms))
}

function isNetworkError(error: unknown) {
  const message = error instanceof Error ? error.message.toLowerCase() : ''
  return message.includes('failed to fetch') || message.includes('networkerror') || message.includes('load failed')
}

function getStoredToken() {
  if (typeof window === 'undefined') return ''
  return window.localStorage.getItem('token') || ''
}

function hasMockSession() {
  const token = getStoredToken()
  if (!token) return false
  return !token.includes('.') || token.startsWith('mock-') || token.startsWith('customer-') || token.startsWith('staff-')
}

function shouldUseLocalFallback(error?: unknown) {
  return hasMockSession() || isNetworkError(error)
}

function dispatchCartUpdated(cart: CartResponse) {
  if (typeof window === 'undefined') return
  window.dispatchEvent(new CustomEvent('goalstore:cart-updated', { detail: cart }))
}

function readCartLines(): LocalCartLine[] {
  if (typeof window === 'undefined') return []
  try {
    const raw = window.localStorage.getItem(CART_STORAGE_KEY)
    if (!raw) return []
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function writeCartLines(lines: LocalCartLine[]) {
  if (typeof window === 'undefined') return
  window.localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(lines))
}


function readSelectedCartItemIdsRaw() {
  if (typeof window === 'undefined') return null as number[] | null
  try {
    const raw = window.localStorage.getItem(CART_SELECTED_STORAGE_KEY)
    if (raw == null) return null
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed.map((value) => Number(value)).filter((value) => Number.isFinite(value)) : []
  } catch {
    return []
  }
}

function writeSelectedCartItemIds(ids: number[]) {
  if (typeof window === 'undefined') return
  const normalized = Array.from(new Set((ids || []).map((value) => Number(value)).filter((value) => Number.isFinite(value))))
  window.localStorage.setItem(CART_SELECTED_STORAGE_KEY, JSON.stringify(normalized))
}

function syncSelectedCartItemIds(items?: Array<{ itemId?: number | null }> | null, autoSelectAllWhenUnset = false) {
  const availableIds = (items || [])
    .map((item) => Number(item?.itemId))
    .filter((value) => Number.isFinite(value))

  const stored = readSelectedCartItemIdsRaw()
  let next: number[]

  if (stored === null) {
    next = autoSelectAllWhenUnset ? [...availableIds] : []
  } else {
    next = stored.filter((value) => availableIds.includes(value))
  }

  writeSelectedCartItemIds(next)
  return next
}

export function getSelectedCartItemIds() {
  return readSelectedCartItemIdsRaw() || []
}

export function setSelectedCartItemIds(ids: number[]) {
  writeSelectedCartItemIds(ids)
}

function readJsonStorage<T>(key: string, fallback: T): T {
  if (typeof window === 'undefined') return fallback
  try {
    const raw = window.localStorage.getItem(key)
    if (!raw) return fallback
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

function writeJsonStorage<T>(key: string, value: T) {
  if (typeof window === 'undefined') return
  window.localStorage.setItem(key, JSON.stringify(value))
}

function normalizeSort(sort?: string) {
  return sort || 'newest'
}

function sortProducts(products: PublicProductSummary[], sort?: string) {
  const normalized = normalizeSort(sort)
  const copy = [...products]

  if (normalized === 'price_asc' || normalized === 'price-asc') {
    return copy.sort((a, b) => toNumber(a.salePrice || a.minPrice || a.price) - toNumber(b.salePrice || b.minPrice || b.price))
  }

  if (normalized === 'price_desc' || normalized === 'price-desc') {
    return copy.sort((a, b) => toNumber(b.salePrice || b.minPrice || b.price) - toNumber(a.salePrice || a.minPrice || a.price))
  }

  if (normalized === 'best_selling') {
    return copy.sort((a, b) => (a.id % 7) - (b.id % 7)).reverse()
  }

  return copy.sort((a, b) => Number(b.id) - Number(a.id))
}

function filterProducts(products: PublicProductSummary[], params: PublicProductSearchParams = {}) {
  return products.filter((item) => {
    const keyword = (params.keyword || '').trim().toLowerCase()
    const brand = (params.brand || '').trim().toLowerCase()
    const category = (params.category || '').trim().toLowerCase()
    const selectedCategoryNames = new Set((params.categoryNames || []).map((value) => String(value).trim().toLowerCase()).filter(Boolean))
    const inStock = Boolean(params.inStock)
    const finalPrice = toNumber(item.salePrice || item.minPrice || item.price)

    const matchesKeyword =
      !keyword ||
      [item.name, item.brand, item.baseSku, ...(item.categoryNames || [])]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(keyword))

    const matchesBrand = !brand || String(item.brand || '').toLowerCase() === brand
    const matchesSingleCategory = !category || (item.categoryNames || []).some((value) => value.toLowerCase() === category)
    const matchesMultiCategory = !selectedCategoryNames.size || (item.categoryNames || []).some((value) => selectedCategoryNames.has(value.toLowerCase()))
    const matchesStock = !inStock || toNumber(item.totalStock) > 0
    const matchesSale = !params.saleOnly || (toNumber(item.salePrice) > 0 && toNumber(item.salePrice) < toNumber(item.price || item.minPrice))
    const matchesMin = params.minPrice === undefined || finalPrice >= params.minPrice
    const matchesMax = params.maxPrice === undefined || finalPrice <= params.maxPrice

    return matchesKeyword && matchesBrand && matchesSingleCategory && matchesMultiCategory && matchesStock && matchesSale && matchesMin && matchesMax
  })
}

function paginateProducts(items: PublicProductSummary[], page = 0, size = 12): PageResponse<PublicProductSummary> {
  const safePage = Math.max(0, page)
  const safeSize = Math.max(1, size)
  const totalElements = items.length
  const totalPages = Math.max(1, Math.ceil(totalElements / safeSize))
  const start = safePage * safeSize
  const content = items.slice(start, start + safeSize)

  return {
    content,
    page: safePage,
    size: safeSize,
    totalElements,
    totalPages,
    last: safePage >= totalPages - 1,
  }
}

function buildCartResponse(lines: LocalCartLine[]): CartResponse {
  const items: CartItem[] = lines
    .map((line) => {
      const match = findVariantById(line.variantId)
      if (!match) return null

      const finalPrice = getVariantFinalPrice(match.variant)
      return {
        itemId: line.itemId,
        productId: match.product.id,
        variantId: line.variantId,
        sku: match.variant.sku,
        productName: match.product.name,
        imageUrl: match.product.thumbnailUrl || match.product.images?.[0]?.imageUrl || null,
        size: match.variant.size,
        color: match.variant.color,
        quantity: line.quantity,
        unitPrice: finalPrice,
        lineTotal: finalPrice * line.quantity,
      } as CartItem
    })
    .filter(Boolean) as CartItem[]

  return {
    cartId: 1,
    customerId: 0,
    totalAmount: items.reduce((sum, item) => sum + toNumber(item.lineTotal), 0),
    items,
  }
}

function readAddressBook() {
  return readJsonStorage<CustomerAddress[]>(ADDRESS_STORAGE_KEY, [])
}

function writeAddressBook(addresses: CustomerAddress[]) {
  writeJsonStorage(ADDRESS_STORAGE_KEY, addresses)
}

function getCustomerAddressesLocal(customerId: number) {
  return readAddressBook().filter((item) => Number(item.customerId) === Number(customerId))
}

function saveCustomerAddressLocal(payload: CustomerAddressPayload, id?: number) {
  const addresses = readAddressBook()
  const existingIndex = id ? addresses.findIndex((item) => item.id === id) : -1
  const nextId = id || (addresses.length ? Math.max(...addresses.map((item) => item.id)) + 1 : 1)

  const address: CustomerAddress = {
    id: nextId,
    customerId: payload.customerId,
    receiverName: payload.receiverName.trim(),
    receiverPhone: payload.receiverPhone.trim(),
    province: payload.province.trim(),
    district: payload.district.trim(),
    ward: payload.ward.trim(),
    detailAddress: payload.detailAddress.trim(),
    isDefault: payload.isDefault ?? true,
  }

  const normalized = addresses.filter((item) => item.customerId !== payload.customerId || item.id === address.id)
    .map((item) => ({ ...item, isDefault: address.isDefault ? false : item.isDefault }))

  if (existingIndex >= 0) {
    const others = normalized.filter((item) => item.id !== address.id)
    writeAddressBook([...others, address])
  } else {
    writeAddressBook([...normalized, address])
  }

  return address
}

function formatAddress(address?: CustomerAddress | null) {
  if (!address) return ''
  return [address.detailAddress, address.ward, address.district, address.province].filter(Boolean).join(', ')
}

function readOrders() {
  return readJsonStorage<OrderResponse[]>(ORDER_STORAGE_KEY, [])
}

function writeOrders(orders: OrderResponse[]) {
  writeJsonStorage(ORDER_STORAGE_KEY, orders)
}

function readReturns() {
  return readJsonStorage<ReturnResponse[]>(RETURN_STORAGE_KEY, [])
}

function writeReturns(returns: ReturnResponse[]) {
  writeJsonStorage(RETURN_STORAGE_KEY, returns)
}

function saveLastOrder(order: OrderResponse) {
  writeJsonStorage(LAST_ORDER_STORAGE_KEY, order)
}

function checkoutCartLocal(payload: CheckoutOrderRequest) {
  const cart = buildCartResponse(readCartLines())
  const allItems = cart.items || []
  const selectedIds = (payload.selectedItemIds || [])
    .map((value) => Number(value))
    .filter((value) => Number.isFinite(value))
  const items = selectedIds.length
    ? allItems.filter((item) => selectedIds.includes(Number(item.itemId)))
    : allItems

  if (!items.length) {
    throw new Error(selectedIds.length ? 'Bạn chưa chọn sản phẩm nào để thanh toán' : 'Không thể checkout vì giỏ hàng đang trống')
  }

  const customerId = Number(payload.customerId)
  const addresses = getCustomerAddressesLocal(customerId)
  const address = payload.addressId
    ? addresses.find((item) => item.id === Number(payload.addressId))
    : addresses.find((item) => item.isDefault) || addresses[0]

  if (!address) {
    throw new Error('Bạn cần thêm địa chỉ nhận hàng trước khi đặt đơn')
  }

  const subtotal = items.reduce((sum, item) => sum + toNumber(item.lineTotal), 0)
  const shippingFee = toNumber(payload.shippingFee)
  const discountAmount = toNumber(payload.discountAmount)
  const total = subtotal + shippingFee - discountAmount

  const orders = readOrders()
  const nextId = orders.length ? Math.max(...orders.map((item) => Number(item.id) || 0)) + 1 : 1
  const requestedPaymentMethod = String(payload.paymentMethod || 'COD').toUpperCase()

  const order: OrderResponse = {
    id: nextId,
    code: `ORD${Date.now().toString().slice(-8)}`,
    customerId,
    customerName: typeof window !== 'undefined' ? window.localStorage.getItem('displayName') || 'Khách hàng' : 'Khách hàng',
    status: 'MOI',
    paymentMethod: requestedPaymentMethod === 'QR' ? 'QR' : requestedPaymentMethod,
    paymentStatus: requestedPaymentMethod === 'QR' ? 'PENDING' : 'UNPAID',
    channel: 'ONLINE',
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    shippingAddress: formatAddress(address),
    note: payload.note || '',
    subtotal,
    shippingFee,
    discountAmount,
    total,
    orderDate: new Date().toISOString(),
    items: items.map((item) => ({
      itemId: item.itemId,
      productId: item.productId,
      variantId: item.variantId,
      sku: item.sku,
      productName: item.productName,
      imageUrl: item.imageUrl,
      size: item.size,
      color: item.color,
      quantity: item.quantity,
      unitPrice: item.unitPrice,
      lineTotal: item.lineTotal,
    })),
  }

  const remainingLines = readCartLines().filter((line) => !selectedIds.includes(Number(line.itemId)))

  writeOrders([order, ...orders])
  writeCartLines(remainingLines)
  const remainingCart = buildCartResponse(remainingLines)
  syncSelectedCartItemIds(remainingCart.items, false)
  dispatchCartUpdated(remainingCart)
  saveLastOrder(order)
  return delay(order)
}

export function resolveImageUrl(url?: string | null) {
  if (!url) return ''

  const trimmed = String(url).trim()

  if (!trimmed) return ''

  if (
    /^(https?:)?\/\//i.test(trimmed) ||
    trimmed.startsWith('data:') ||
    trimmed.startsWith('blob:')
  ) {
    return trimmed
  }

  if (trimmed.startsWith('/uploads/')) {
    return `${API_URL}${trimmed}`
  }

  if (trimmed.startsWith('uploads/')) {
    return `${API_URL}/${trimmed}`
  }

  return trimmed
}

export async function getPublicCategories() {
  try {
    return await apiRequest<ProductCategory[]>('/api/categories', { skipAuth: true })
  } catch {
    const map = new Map<string, ProductCategory>()

    mockProductSummaries.forEach((product) => {
      ;(product.categoryNames || []).forEach((name) => {
        const key = String(name || '').trim()
        if (!key) return

        if (!map.has(key)) {
          map.set(key, {
            id: map.size + 1,
            name: key,
            productCount: 0,
          })
        }

        const category = map.get(key)!
        category.productCount = Number(category.productCount || 0) + 1
      })
    })

    return delay(Array.from(map.values()))
  }
}

export async function getPublicProducts(params: PublicProductSearchParams = {}) {
  const query = new URLSearchParams()

  if (params.page !== undefined) query.set('page', String(params.page))
  if (params.size !== undefined) query.set('size', String(params.size))
  if (params.keyword) query.set('keyword', params.keyword)
  if (params.brand) query.set('brand', params.brand)
  if (params.category) query.set('category', params.category)
  if (params.categoryIds?.length) query.set('categoryIds', params.categoryIds.join(','))
  if (params.minPrice !== undefined) query.set('minPrice', String(params.minPrice))
  if (params.maxPrice !== undefined) query.set('maxPrice', String(params.maxPrice))
  if (params.inStock !== undefined) query.set('inStock', String(params.inStock))
  if (params.saleOnly !== undefined) query.set('saleOnly', String(params.saleOnly))
  if (params.sort) query.set('sort', params.sort)

  try {
    return await apiRequest<PageResponse<PublicProductSummary>>(
      `/api/public/products?${query.toString()}`,
      { skipAuth: true }
    )
  } catch {
    const filtered = filterProducts(mockProductSummaries, params)
    const sorted = sortProducts(filtered, params.sort)
    return delay(paginateProducts(sorted, params.page ?? 0, params.size ?? 12))
  }
}

export async function getPublicProductDetail(id: number | string) {
  try {
    return await apiRequest<PublicProductDetail>(`/api/public/products/${id}`, {
      skipAuth: true,
    })
  } catch {
    const product = findProductById(id)
    if (!product) throw new Error('Không tìm thấy sản phẩm')
    return delay<PublicProductDetail>(JSON.parse(JSON.stringify(product)))
  }
}

export async function getCart(customerId?: number | string) {
  if (customerId === undefined || customerId === null || customerId === '') {
    const cart = buildCartResponse(readCartLines())
    syncSelectedCartItemIds(cart.items, true)
    dispatchCartUpdated(cart)
    return delay(cart)
  }

  try {
    const cart = await apiRequest<CartResponse>(`/api/carts?customerId=${customerId}`)
    syncSelectedCartItemIds(cart.items, true)
    dispatchCartUpdated(cart)
    return cart
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const cart = buildCartResponse(readCartLines())
  syncSelectedCartItemIds(cart.items, true)
  dispatchCartUpdated(cart)
  return delay(cart)
}

export async function addCartItem(payload: { customerId?: number | string; variantId: number; quantity: number }) {
  try {
    if (payload.customerId !== undefined && payload.customerId !== null && !hasMockSession()) {
      const cart = await apiRequest<CartResponse>('/api/carts/items', {
        method: 'POST',
        body: {
          customerId: Number(payload.customerId),
          variantId: payload.variantId,
          quantity: payload.quantity,
        },
      })
      syncSelectedCartItemIds(cart.items, true)
      dispatchCartUpdated(cart)
      return cart
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const match = findVariantById(payload.variantId)
  if (!match) throw new Error('Biến thể sản phẩm không tồn tại')

  const stock = toNumber(match.variant.stockQuantity)
  if (stock <= 0) throw new Error('Sản phẩm này hiện đang hết hàng')

  const lines = readCartLines()
  const found = lines.find((item) => item.variantId === payload.variantId)
  const nextQuantity = Math.max(1, toNumber(payload.quantity))

  if (found) {
    found.quantity = Math.min(stock, found.quantity + nextQuantity)
  } else {
    lines.push({
      itemId: lines.length ? Math.max(...lines.map((item) => item.itemId)) + 1 : 1,
      variantId: payload.variantId,
      quantity: Math.min(stock, nextQuantity),
    })
  }

  writeCartLines(lines)
  const cart = buildCartResponse(lines)
  syncSelectedCartItemIds(cart.items, true)
  dispatchCartUpdated(cart)
  return delay(cart)
}

export async function updateCartItem(
  itemId: number,
  payload: { customerId?: number | string; variantId: number; quantity: number },
) {
  try {
    if (payload.customerId !== undefined && payload.customerId !== null && !hasMockSession()) {
      const cart = await apiRequest<CartResponse>(`/api/carts/items/${itemId}`, {
        method: 'PUT',
        body: {
          customerId: Number(payload.customerId),
          variantId: payload.variantId,
          quantity: payload.quantity,
        },
      })
      syncSelectedCartItemIds(cart.items, false)
      dispatchCartUpdated(cart)
      return cart
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const lines = readCartLines()
  const target = lines.find((item) => item.itemId === itemId)
  const match = findVariantById(payload.variantId)

  if (!target || !match) throw new Error('Không tìm thấy sản phẩm cần cập nhật')

  target.quantity = Math.max(1, Math.min(toNumber(match.variant.stockQuantity), toNumber(payload.quantity)))
  writeCartLines(lines)
  const cart = buildCartResponse(lines)
  syncSelectedCartItemIds(cart.items, false)
  dispatchCartUpdated(cart)
  return delay(cart)
}

export async function removeCartItem(itemId: number) {
  try {
    if (!hasMockSession()) {
      const cart = await apiRequest<CartResponse>(`/api/carts/items/${itemId}`, {
        method: 'DELETE',
      })
      syncSelectedCartItemIds(cart.items, false)
      dispatchCartUpdated(cart)
      return cart
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const lines = readCartLines().filter((item) => item.itemId !== itemId)
  writeCartLines(lines)
  const cart = buildCartResponse(lines)
  syncSelectedCartItemIds(cart.items, false)
  dispatchCartUpdated(cart)
  return delay(cart)
}

export async function clearCart(customerId?: number | string) {
  try {
    if (customerId !== undefined && customerId !== null && !hasMockSession()) {
      const cart = await apiRequest<CartResponse>(`/api/carts/clear?customerId=${customerId}`, {
        method: 'DELETE',
      })
      syncSelectedCartItemIds(cart.items, false)
      dispatchCartUpdated(cart)
      return cart
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  writeCartLines([])
  const cart = buildCartResponse([])
  syncSelectedCartItemIds(cart.items, false)
  dispatchCartUpdated(cart)
  return delay(cart)
}

export async function getCustomerAddresses(customerId: number) {
  try {
    if (!hasMockSession()) {
      return await apiRequest<CustomerAddress[]>(`/api/customer-addresses?customerId=${customerId}`)
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return delay(getCustomerAddressesLocal(customerId).sort((a, b) => Number(Boolean(b.isDefault)) - Number(Boolean(a.isDefault)) || b.id - a.id))
}

export async function createCustomerAddress(payload: CustomerAddressPayload) {
  try {
    if (!hasMockSession()) {
      return await apiRequest<CustomerAddress>('/api/customer-addresses', {
        method: 'POST',
        body: payload,
      })
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return delay(saveCustomerAddressLocal(payload))
}

export async function updateCustomerAddress(id: number, payload: CustomerAddressPayload) {
  try {
    if (!hasMockSession()) {
      return await apiRequest<CustomerAddress>(`/api/customer-addresses/${id}`, {
        method: 'PUT',
        body: payload,
      })
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return delay(saveCustomerAddressLocal(payload, id))
}

export async function checkoutCart(payload: CheckoutOrderRequest) {
  try {
    if (!hasMockSession()) {
      const customerId = Number(payload.customerId)

      if (!customerId) {
        throw new Error('Không xác định được khách hàng để checkout')
      }

      const cart = await apiRequest<CartResponse>(`/api/carts?customerId=${customerId}`)
      const allItems = cart.items || []
      const selectedIds = (payload.selectedItemIds || [])
        .map((value) => Number(value))
        .filter((value) => Number.isFinite(value))
      const items = selectedIds.length
        ? allItems.filter((item) => selectedIds.includes(Number(item.itemId)))
        : allItems

      if (!items.length) {
        throw new Error(selectedIds.length ? 'Bạn chưa chọn sản phẩm nào để thanh toán' : 'Không thể checkout vì giỏ hàng đang trống')
      }

      let addresses = getCustomerAddressesLocal(customerId)

      if (!addresses.length) {
        try {
          addresses = await apiRequest<CustomerAddress[]>(`/api/customer-addresses?customerId=${customerId}`)
        } catch {
          addresses = []
        }
      }

      const address = payload.addressId
        ? addresses.find((item) => item.id === Number(payload.addressId))
        : addresses.find((item) => item.isDefault) || addresses[0]

      if (!address) {
        throw new Error('Bạn cần thêm địa chỉ nhận hàng trước khi đặt đơn')
      }

      const subtotal = items.reduce((sum, item) => sum + toNumber(item.lineTotal), 0)
      const shippingFee = toNumber(payload.shippingFee)
      const discountAmount = toNumber(payload.discountAmount)
      const total = subtotal + shippingFee - discountAmount

      const requestedPaymentMethod = String(payload.paymentMethod || 'COD').toUpperCase()
      const backendPaymentMethod = requestedPaymentMethod === 'QR' ? 'BANKING' : requestedPaymentMethod

      const backendPayload = {
        customerId,
        paymentMethod: backendPaymentMethod,
        channel: 'ONLINE',
        note: payload.note || '',
        receiverName: address.receiverName,
        receiverPhone: address.receiverPhone,
        shippingAddress: formatAddress(address),
        shippingFee,
        discountAmount,
        items: items.map((item) => ({
          variantId: Number(item.variantId),
          quantity: Number(item.quantity),
        })),
      }

      const backendOrder = await apiRequest<any>('/api/orders', {
        method: 'POST',
        body: backendPayload,
      })

      const normalizedOrder: OrderResponse = {
        id: backendOrder?.id ?? null,
        code: backendOrder?.code ?? (backendOrder?.id ? `ORD${String(backendOrder.id).padStart(6, '0')}` : null),
        customerId,
        customerName:
          backendOrder?.customerName ||
          (typeof window !== 'undefined' ? window.localStorage.getItem('displayName') || 'Khách hàng' : 'Khách hàng'),
        status: backendOrder?.status || 'MOI',
        paymentMethod: requestedPaymentMethod === 'QR' ? 'QR' : (backendOrder?.paymentMethod || requestedPaymentMethod || 'COD'),
        paymentStatus: backendOrder?.paymentStatus || (requestedPaymentMethod === 'QR' ? 'PENDING' : 'UNPAID'),
        channel: backendOrder?.channel || 'ONLINE',
        receiverName: backendOrder?.receiverName || address.receiverName,
        receiverPhone: backendOrder?.receiverPhone || address.receiverPhone,
        shippingAddress: backendOrder?.shippingAddress || formatAddress(address),
        note: backendOrder?.note || payload.note || '',
        subtotal: backendOrder?.subtotal ?? subtotal,
        shippingFee: backendOrder?.shippingFee ?? shippingFee,
        discountAmount: backendOrder?.discountAmount ?? discountAmount,
        total: backendOrder?.total ?? total,
        orderDate: backendOrder?.orderDate || new Date().toISOString(),
        items: (backendOrder?.items || items).map((item: any, index: number) => ({
          itemId: item?.id ?? item?.itemId ?? index + 1,
          productId: item?.productId ?? null,
          variantId: item?.variantId ?? null,
          sku: item?.variantSku ?? item?.sku ?? null,
          productName: item?.productName ?? null,
          imageUrl: item?.imageUrl ?? null,
          size: item?.size ?? null,
          color: item?.color ?? null,
          quantity: Number(item?.quantity ?? 0),
          unitPrice: toNumber(item?.unitPrice),
          lineTotal: item?.lineTotal ?? toNumber(item?.unitPrice) * Number(item?.quantity || 0),
        })),
      }

      saveLastOrder(normalizedOrder)

      try {
        const refreshedCart = await apiRequest<CartResponse>(`/api/carts?customerId=${customerId}`)
        syncSelectedCartItemIds(refreshedCart.items, false)
        dispatchCartUpdated(refreshedCart)
      } catch {
        const remainingItems = allItems.filter((item) => !selectedIds.includes(Number(item.itemId)))
        const fallbackCart: CartResponse = {
          cartId: cart.cartId ?? 1,
          customerId,
          totalAmount: remainingItems.reduce((sum, item) => sum + toNumber(item.lineTotal), 0),
          items: remainingItems,
        }
        syncSelectedCartItemIds(fallbackCart.items, false)
        dispatchCartUpdated(fallbackCart)
      }

      return normalizedOrder
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return checkoutCartLocal(payload)
}

export async function getCustomerOrders(customerId: number) {
  try {
    if (!hasMockSession()) {
      const orders = await apiRequest<OrderResponse[]>(`/api/orders?customerId=${customerId}`)
      return [...orders].sort((a, b) => new Date(String(b.orderDate || 0)).getTime() - new Date(String(a.orderDate || 0)).getTime())
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  return delay(
    readOrders()
      .filter((item) => Number(item.customerId) === Number(customerId))
      .sort((a, b) => new Date(String(b.orderDate || 0)).getTime() - new Date(String(a.orderDate || 0)).getTime()),
  )
}

export async function getOrderDetail(orderId: number) {
  try {
    if (!hasMockSession()) {
      return await apiRequest<OrderResponse>(`/api/orders/${orderId}`)
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const order = readOrders().find((item) => Number(item.id) === Number(orderId))
  if (!order) {
    throw new Error('Không tìm thấy đơn hàng')
  }
  return delay(order)
}

export async function cancelCustomerOrder(orderId: number) {
  try {
    if (!hasMockSession()) {
      return await apiRequest<OrderResponse>(`/api/orders/${orderId}/cancel`, { method: 'PUT' })
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const orders = readOrders()
  const index = orders.findIndex((item) => Number(item.id) === Number(orderId))
  if (index < 0) throw new Error('Không tìm thấy đơn hàng')

  const current = orders[index]
  if (current.status === 'HUY') throw new Error('Đơn hàng đã hủy trước đó')
  if (current.status === 'DANG_GIAO' || current.status === 'HOAN_TAT' || current.status === 'TRA_HANG') {
    throw new Error('Không thể hủy đơn ở trạng thái hiện tại')
  }

  orders[index] = { ...current, status: 'HUY' }
  writeOrders(orders)
  return delay(orders[index])
}

export async function createOrderReturn(orderId: number, payload: ReturnRequest) {
  try {
    if (!hasMockSession()) {
      const created = await apiRequest<ReturnResponse>(`/api/returns/order/${orderId}`, {
        method: 'POST',
        body: payload,
      })
      return created
    }
  } catch (error) {
    if (!shouldUseLocalFallback(error)) {
      throw error
    }
  }

  const orders = readOrders()
  const index = orders.findIndex((item) => Number(item.id) === Number(orderId))
  if (index < 0) throw new Error('Không tìm thấy đơn hàng')

  const current = orders[index]
  if (current.status === 'HUY') throw new Error('Đơn đã hủy, không thể trả hàng')
  if (current.status === 'TRA_HANG') throw new Error('Đơn hàng đã được yêu cầu trả hàng trước đó')
  if (current.status !== 'HOAN_TAT') throw new Error('Chỉ hỗ trợ trả hàng khi đơn đã hoàn tất')

  const refundTotal = toNumber(current.total)
  const returns = readReturns()
  const nextId = returns.length ? Math.max(...returns.map((item) => Number(item.id) || 0)) + 1 : 1
  const created: ReturnResponse = {
    id: nextId,
    orderId,
    reason: payload.reason.trim(),
    note: payload.note?.trim() || '',
    refundTotal,
    returnDate: new Date().toISOString(),
  }

  writeReturns([created, ...returns])
  orders[index] = { ...current, status: 'TRA_HANG' }
  writeOrders(orders)
  return delay(created)
}

export function getLastOrder() {
  return readJsonStorage<OrderResponse | null>(LAST_ORDER_STORAGE_KEY, null)
}

export function getFeaturedProducts(limit = 4) {
  return mockProductSummaries.slice(0, limit)
}

export function getProductSummaryById(id: number | string) {
  const product = findProductById(id)
  return product ? getSummaryFromDetail(product) : null
}
