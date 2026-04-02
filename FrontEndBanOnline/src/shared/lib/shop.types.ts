export type PageResponse<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export type PublicProductSummary = {
  id: number
  name: string
  baseSku?: string
  brand?: string
  imageUrl?: string
  price?: number | string | null
  salePrice?: number | string | null
  minPrice?: number | string | null
  maxPrice?: number | string | null
  totalStock?: number | null
  stockStatus?: string | null
  productType?: string | null
  targetGender?: string | null
  categoryNames?: string[]
}

export type ProductImage = {
  id: number
  imageUrl?: string | null
  avatar?: boolean
  sortOrder?: number | null
}

export type ProductCategory = {
  id: number
  name: string
  description?: string | null
  imageUrl?: string | null
  productCount?: number
}

export type ProductTag = {
  id: number
  name: string
  colorCode?: string | null
}

export type ProductAttribute = {
  id: number
  name: string
  value: string
}

export type ProductVariant = {
  id: number
  sku?: string
  size?: string | null
  color?: string | null
  price?: number | string | null
  salePrice?: number | string | null
  stockQuantity?: number | null
  stockStatus?: string | null
}

export type PublicProductDetail = {
  id: number
  name: string
  baseSku?: string
  brand?: string
  season?: string | null
  productType?: string | null
  targetGender?: string | null
  material?: string | null
  description?: string | null
  releaseYear?: number | null
  displayStatus?: string | null
  thumbnailUrl?: string | null
  createdAt?: string | null
  categories?: ProductCategory[]
  tags?: ProductTag[]
  images?: ProductImage[]
  variants?: ProductVariant[]
  attributes?: ProductAttribute[]
}

export type CartItem = {
  itemId: number
  productId?: number | null
  variantId: number
  sku?: string | null
  productName?: string | null
  imageUrl?: string | null
  size?: string | null
  color?: string | null
  quantity: number
  unitPrice?: number | string | null
  lineTotal?: number | string | null
}

export type CartResponse = {
  cartId?: number | null
  customerId?: number | null
  totalAmount?: number | string | null
  items?: CartItem[]
}

export type CustomerAddress = {
  id: number
  customerId: number
  receiverName: string
  receiverPhone: string
  province: string
  district: string
  ward: string
  detailAddress: string
  isDefault?: boolean | null
}

export type CustomerAddressPayload = {
  customerId: number
  receiverName: string
  receiverPhone: string
  province: string
  district: string
  ward: string
  detailAddress: string
  isDefault?: boolean
}

export type CheckoutOrderRequest = {
  customerId: number
  addressId?: number | null
  paymentMethod?: 'COD' | 'QR' | 'BANKING' | 'MOMO' | 'VNPAY' | string
  note?: string
  shippingFee?: number
  discountAmount?: number
  selectedItemIds?: number[]
}

export type OrderItemSummary = {
  itemId?: number | null
  productId?: number | null
  variantId?: number | null
  sku?: string | null
  productName?: string | null
  imageUrl?: string | null
  size?: string | null
  color?: string | null
  quantity?: number | null
  unitPrice?: number | string | null
  lineTotal?: number | string | null
}

export type OrderResponse = {
  id?: number | null
  code?: string | null
  customerId?: number | null
  customerName?: string | null
  staffId?: number | null
  staffUsername?: string | null
  status?: string | null
  paymentMethod?: string | null
  paymentStatus?: string | null
  channel?: string | null
  receiverName?: string | null
  receiverPhone?: string | null
  shippingAddress?: string | null
  note?: string | null
  subtotal?: number | string | null
  shippingFee?: number | string | null
  discountAmount?: number | string | null
  total?: number | string | null
  orderDate?: string | null
  items?: OrderItemSummary[]
}

export type ReturnRequest = {
  reason: string
  note?: string
}

export type ReturnResponse = {
  id?: number | null
  orderId?: number | null
  reason?: string | null
  note?: string | null
  refundTotal?: number | string | null
  returnDate?: string | null
}

export type PublicProductSearchParams = {
  page?: number
  size?: number
  keyword?: string
  brand?: string
  category?: string
  categoryIds?: Array<number | string>
  categoryNames?: string[]
  minPrice?: number
  maxPrice?: number
  inStock?: boolean
  saleOnly?: boolean
  sort?: 'newest' | 'price-asc' | 'price-desc' | 'price_asc' | 'price_desc' | 'best_selling'
}
