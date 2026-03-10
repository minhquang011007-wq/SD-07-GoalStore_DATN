import api from "@/shared/lib/api"
import type {
  CategoryRequest,
  CategoryResponse,
  PageResponse,
  ProductDetailResponse,
  ProductImageResponse,
  ProductRequest,
  ProductSummaryResponse,
  ProductVariantRequest,
  ProductVariantResponse,
  TagRequest,
  TagResponse,
} from "@/modules/product/types"

export interface ProductSearchParams {
  page?: number
  size?: number
  keyword?: string
  categoryIds?: number[]
  displayStatus?: string
  inStock?: boolean | null
  sort?: string
}

const toCsv = (values?: number[]) => (values && values.length ? values.join(",") : undefined)

export async function fetchCategories(keyword?: string) {
  const { data } = await api.get<CategoryResponse[]>("/api/categories", {
    params: keyword ? { keyword } : undefined,
  })
  return data
}

export async function createCategory(payload: CategoryRequest) {
  const { data } = await api.post<CategoryResponse>("/api/categories", payload)
  return data
}

export async function updateCategory(id: number, payload: CategoryRequest) {
  const { data } = await api.put<CategoryResponse>(`/api/categories/${id}`, payload)
  return data
}

export async function deleteCategory(id: number) {
  await api.delete(`/api/categories/${id}`)
}

export async function uploadCategoryImage(id: number, file: File) {
  const formData = new FormData()
  formData.append("file", file)
  const { data } = await api.post<CategoryResponse>(`/api/categories/${id}/image`, formData)
  return data
}

export async function fetchTags() {
  const { data } = await api.get<TagResponse[]>("/api/tags")
  return data
}

export async function createTag(payload: TagRequest) {
  const { data } = await api.post<TagResponse>("/api/tags", payload)
  return data
}

export async function updateTag(id: number, payload: TagRequest) {
  const { data } = await api.put<TagResponse>(`/api/tags/${id}`, payload)
  return data
}

export async function deleteTag(id: number) {
  await api.delete(`/api/tags/${id}`)
}

export async function searchProducts(params: ProductSearchParams) {
  const { data } = await api.get<PageResponse<ProductSummaryResponse>>("/api/products", {
    params: {
      page: params.page ?? 0,
      size: params.size ?? 20,
      keyword: params.keyword || undefined,
      categoryIds: toCsv(params.categoryIds),
      displayStatus: params.displayStatus || undefined,
      inStock: params.inStock ?? undefined,
      sort: params.sort || "newest",
    },
  })
  return data
}

export async function fetchProductDetail(id: number) {
  const { data } = await api.get<ProductDetailResponse>(`/api/products/${id}`)
  return data
}

export async function createProduct(payload: ProductRequest) {
  const { data } = await api.post<ProductDetailResponse>("/api/products", payload)
  return data
}

export async function updateProduct(id: number, payload: ProductRequest) {
  const { data } = await api.put<ProductDetailResponse>(`/api/products/${id}`, payload)
  return data
}

export async function deleteProduct(id: number) {
  await api.delete(`/api/products/${id}`)
}

export async function hideProductIfOutOfStock(id: number) {
  const { data } = await api.patch<ProductDetailResponse>(`/api/products/${id}/hide-if-out-of-stock`)
  return data
}

export async function uploadProductImages(productId: number, files: File[], avatarFirst = true) {
  const formData = new FormData()
  files.forEach((file) => formData.append("files", file))
  const { data } = await api.post<ProductImageResponse[]>(`/api/products/${productId}/images`, formData, {
    params: { avatarFirst },
  })
  return data
}

export async function updateProductImage(imageId: number, payload: { avatar?: boolean; sortOrder?: number }) {
  const { data } = await api.put<ProductImageResponse>(`/api/images/${imageId}`, payload)
  return data
}

export async function deleteProductImage(imageId: number) {
  await api.delete(`/api/images/${imageId}`)
}

export async function createVariant(productId: number, payload: ProductVariantRequest) {
  const { data } = await api.post<ProductVariantResponse>(`/api/products/${productId}/variants`, payload)
  return data
}

export async function updateVariant(id: number, payload: ProductVariantRequest) {
  const { data } = await api.put<ProductVariantResponse>(`/api/variants/${id}`, payload)
  return data
}

export async function deleteVariant(id: number) {
  await api.delete(`/api/variants/${id}`)
}
