import { apiRequest } from '@/shared/lib/api'
import type {
  CartItem,
  CartResponse,
  PageResponse,
  PublicProductDetail,
  PublicProductSearchParams,
  PublicProductSummary,
  ProductCategory,
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


type LocalCartLine = {
  itemId: number
  variantId: number
  quantity: number
}

function delay<T>(value: T, ms = 80) {
  return new Promise<T>((resolve) => setTimeout(() => resolve(value), ms))
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

export async function getCart(_customerId?: number | string) {
  return delay(buildCartResponse(readCartLines()))
}

export async function addCartItem(payload: { customerId?: number | string; variantId: number; quantity: number }) {
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
  return delay(buildCartResponse(lines))
}

export async function updateCartItem(
  itemId: number,
  payload: { customerId?: number | string; variantId: number; quantity: number },
) {
  const lines = readCartLines()
  const target = lines.find((item) => item.itemId === itemId)
  const match = findVariantById(payload.variantId)

  if (!target || !match) throw new Error('Không tìm thấy sản phẩm cần cập nhật')

  target.quantity = Math.max(1, Math.min(toNumber(match.variant.stockQuantity), toNumber(payload.quantity)))
  writeCartLines(lines)
  return delay(buildCartResponse(lines))
}

export async function removeCartItem(itemId: number) {
  const lines = readCartLines().filter((item) => item.itemId !== itemId)
  writeCartLines(lines)
  return delay(buildCartResponse(lines))
}

export async function clearCart(_customerId?: number | string) {
  writeCartLines([])
  return delay(buildCartResponse([]))
}

export function getFeaturedProducts(limit = 4) {
  return mockProductSummaries.slice(0, limit)
}

export function getProductSummaryById(id: number | string) {
  const product = findProductById(id)
  return product ? getSummaryFromDetail(product) : null
}
