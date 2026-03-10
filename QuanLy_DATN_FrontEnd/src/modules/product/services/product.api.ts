import api from "@/shared/lib/api"
import type {
  CategoryRequest,
  CategoryResponse,
  PageResponse,
  ProductBatchUpdateRequest,
  ProductDetailResponse,
  ProductHistoryResponse,
  ProductImageResponse,
  ProductQuickUpdateRequest,
  ProductRequest,
  ProductSearchParams,
  ProductSummaryResponse,
  ProductVariantRequest,
  ProductVariantResponse,
  TagRequest,
  TagResponse,
} from "@/modules/product/types"

const toCsv = (values?: number[]) => (values && values.length ? values.join(",") : undefined)

export async function fetchCategories(keyword?: string) {
  const { data } = await api.get<CategoryResponse[]>("/api/categories", {
    params: keyword ? { keyword } : undefined,
  })
  return data
}

export async function fetchCategoryDetail(id: number) {
  const { data } = await api.get<CategoryResponse>(`/api/categories/${id}`)
  return data
}

export async function fetchCategoryProducts(id: number) {
  const { data } = await api.get<ProductSummaryResponse[]>(`/api/categories/${id}/products`)
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
      tagIds: toCsv(params.tagIds),
      displayStatus: params.displayStatus || undefined,
      brand: params.brand || undefined,
      material: params.material || undefined,
      inStock: params.inStock ?? undefined,
      stockStatus: params.stockStatus || undefined,
      hideOutOfStock: params.hideOutOfStock ?? undefined,
      createdWithinDays: params.createdWithinDays ?? undefined,
      sort: params.sort || "newest",
      productType: params.productType || undefined,
      targetGender: params.targetGender || undefined,
      releaseYear: params.releaseYear ?? undefined,
      minPrice: params.minPrice ?? undefined,
      maxPrice: params.maxPrice ?? undefined,
      categoryMatchAll: params.categoryMatchAll ?? false,
    },
  })
  return data
}

export async function fetchProductDetail(id: number) {
  const { data } = await api.get<ProductDetailResponse>(`/api/products/${id}`)
  return data
}

export async function fetchTopSellingProducts() {
  const { data } = await api.get<ProductSummaryResponse[]>("/api/products/top-selling")
  return data
}

export async function fetchNewestProducts(limit = 10) {
  const { data } = await api.get<ProductSummaryResponse[]>("/api/products/newest", {
    params: { limit },
  })
  return data
}

export async function fetchProductHistory(productId: number) {
  const { data } = await api.get<ProductHistoryResponse[]>(`/api/products/${productId}/history`)
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

export async function quickUpdateProduct(id: number, payload: ProductQuickUpdateRequest) {
  const { data } = await api.patch<ProductDetailResponse>(`/api/products/${id}/quick-update`, payload)
  return data
}

export async function batchUpdateProducts(payload: ProductBatchUpdateRequest) {
  const { data } = await api.patch<ProductDetailResponse[]>("/api/products/batch-update", payload)
  return data
}

export async function deleteProduct(id: number) {
  await api.delete(`/api/products/${id}`)
}

export async function hardDeleteProduct(id: number) {
  await api.delete(`/api/products/${id}/hard`)
}

export async function hideProductIfOutOfStock(id: number) {
  const { data } = await api.patch<ProductDetailResponse>(`/api/products/${id}/hide-if-out-of-stock`)
  return data
}

export async function fetchProductImages(productId: number) {
  const { data } = await api.get<ProductImageResponse[]>(`/api/products/${productId}/images`)
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

export async function fetchVariants(productId: number, keyword?: string, stockStatus?: string) {
  const { data } = await api.get<ProductVariantResponse[]>(`/api/products/${productId}/variants`, {
    params: {
      keyword: keyword || undefined,
      stockStatus: stockStatus || undefined,
    },
  })
  return data
}

export async function fetchVariantDetail(id: number) {
  const { data } = await api.get<ProductVariantResponse>(`/api/variants/${id}`)
  return data
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
