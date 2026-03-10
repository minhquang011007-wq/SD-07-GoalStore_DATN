export type ProductDisplayStatus = "HIENTHI" | "AN" | "NGUNG_BAN"
export type ProductType = "AO" | "QUAN" | "VO" | "GANG_TAY" | "KHAC"
export type TargetGender = "NAM" | "NU" | "TRE_EM" | "UNISEX"
export type VariantStockStatus = "CON_HANG" | "HET_HANG" | "PRE_ORDER"

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface CategoryResponse {
  id: number
  name: string
  description?: string | null
  imageUrl?: string | null
  productCount: number
}

export interface TagResponse {
  id: number
  name: string
  description?: string | null
}

export interface CategoryRequest {
  name: string
  description: string
}

export interface TagRequest {
  name: string
  description: string
}

export interface ProductImageResponse {
  id: number
  imageUrl: string
  avatar: boolean
  sortOrder: number
}

export interface ProductVariantResponse {
  id: number
  sku: string
  size: string
  color: string
  price: number
  salePrice?: number | null
  stockQuantity: number
  stockStatus: VariantStockStatus
}

export interface ProductSummaryResponse {
  id: number
  name: string
  baseSku: string
  brand?: string | null
  season?: string | null
  productType: ProductType
  targetGender: TargetGender
  material?: string | null
  displayStatus: ProductDisplayStatus
  thumbnailUrl?: string | null
  categoryNames: string[]
  minPrice?: number | null
  maxPrice?: number | null
  totalStock?: number | null
  stockStatus?: VariantStockStatus | null
  soldQuantity?: number | null
  createdAt?: string | null
}

export interface ProductDetailResponse {
  id: number
  name: string
  baseSku: string
  brand?: string | null
  season?: string | null
  productType: ProductType
  targetGender: TargetGender
  material?: string | null
  description?: string | null
  releaseYear?: number | null
  displayStatus: ProductDisplayStatus
  thumbnailUrl?: string | null
  createdAt?: string | null
  categories: CategoryResponse[]
  tags: TagResponse[]
  images: ProductImageResponse[]
  variants: ProductVariantResponse[]
}

export interface ProductRequest {
  name: string
  baseSku: string
  brand: string
  season: string
  productType: ProductType
  targetGender: TargetGender
  material: string
  description: string
  releaseYear?: number | null
  displayStatus: ProductDisplayStatus
  categoryIds: number[]
  tagIds: number[]
}

export interface ProductVariantRequest {
  sku: string
  size: string
  color: string
  price: number | null
  salePrice: number | null
  stockQuantity: number | null
  stockStatus: VariantStockStatus
}
