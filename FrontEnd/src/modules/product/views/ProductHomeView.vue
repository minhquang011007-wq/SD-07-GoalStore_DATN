<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { AlertCircle, Boxes, RefreshCcw, X } from "lucide-vue-next"
import ProductFilters from "@/modules/product/components/ProductFilters.vue"
import ProductTable from "@/modules/product/components/ProductTable.vue"
import ProductModal from "@/modules/product/components/ProductModal.vue"
import ProductDetail from "@/modules/product/components/ProductDetail.vue"
import CategoryManager from "@/modules/product/components/CategoryManager.vue"
import CategoryModal from "@/modules/product/components/CategoryModal.vue"
import TagManager from "@/modules/product/components/TagManager.vue"
import TagModal from "@/modules/product/components/TagModal.vue"
import AttributeModal from "@/modules/product/components/AttributeModal.vue"
import ProductQuickEditModal from "@/modules/product/components/ProductQuickEditModal.vue"
import ProductBatchUpdateBar from "@/modules/product/components/ProductBatchUpdateBar.vue"
import {
  batchUpdateProducts,
  createCategory,
  createProduct,
  createProductAttribute,
  createTag,
  createVariant,
  deleteCategory,
  deleteProduct,
  deleteProductAttribute,
  deleteProductImage,
  deleteTag,
  deleteVariant,
  fetchCategories,
  fetchProductAttributes,
  fetchProductDetail,
  fetchProductHistory,
  fetchProductImages,
  fetchTags,
  fetchVariants,
  hideProductIfOutOfStock,
  quickUpdateProduct,
  searchProducts,
  updateCategory,
  updateProduct,
  updateProductAttribute,
  updateProductImage,
  updateTag,
  updateVariant,
  uploadCategoryImage,
  uploadProductImages,
} from "@/modules/product/services/product.api"
import type {
  CategoryRequest,
  CategoryResponse,
  ProductAttributeRequest,
  ProductAttributeResponse,
  ProductBatchUpdateItemRequest,
  ProductDetailResponse,
  ProductDisplayStatus,
  ProductHistoryResponse,
  ProductImageResponse,
  ProductQuickUpdateRequest,
  ProductRequest,
  ProductSummaryResponse,
  ProductType,
  ProductVariantRequest,
  ProductVariantResponse,
  TagRequest,
  TagResponse,
  TargetGender,
  VariantStockStatus,
} from "@/modules/product/types"

const PRODUCT_TYPES: Array<{ label: string; value: ProductType }> = [
  { label: "Áo", value: "AO" },
  { label: "Quần", value: "QUAN" },
  { label: "Vớ", value: "VO" },
  { label: "Găng tay", value: "GANG_TAY" },
  { label: "Khác", value: "KHAC" },
]

const GENDERS: Array<{ label: string; value: TargetGender }> = [
  { label: "Nam", value: "NAM" },
  { label: "Nữ", value: "NU" },
  { label: "Trẻ em", value: "TRE_EM" },
  { label: "Dùng chung", value: "UNISEX" },
]

const DISPLAY_STATUS_OPTIONS: Array<{ label: string; value: ProductDisplayStatus }> = [
  { label: "Hiển thị", value: "HIENTHI" },
  { label: "Ẩn", value: "AN" },
  { label: "Ngừng bán", value: "NGUNG_BAN" },
]

const STOCK_STATUS_OPTIONS: Array<{ label: string; value: VariantStockStatus }> = [
  { label: "Còn hàng", value: "CON_HANG" },
  { label: "Hết hàng", value: "HET_HANG" },
  { label: "Đặt trước", value: "PRE_ORDER" },
]

const SORT_OPTIONS = [
  { label: "Mới nhất", value: "newest" },
  { label: "Cũ nhất", value: "oldest" },
  { label: "Tên A → Z", value: "name_asc" },
  { label: "Tên Z → A", value: "name_desc" },
  { label: "Giá tăng dần", value: "price_asc" },
  { label: "Giá giảm dần", value: "price_desc" },
  { label: "Bán chạy", value: "best_selling" },
]

const activeTab = ref<"products" | "categories" | "tags">("products")

const loadingProducts = ref(false)
const loadingCategories = ref(false)
const loadingTags = ref(false)
const loadingDetail = ref(false)
const savingProduct = ref(false)
const savingCategory = ref(false)
const savingTag = ref(false)
const savingAttribute = ref(false)
const savingQuickUpdate = ref(false)
const savingBatchUpdate = ref(false)
const uploadingProductImages = ref(false)

const selectedProductId = ref<number | null>(null)
const selectedProduct = ref<ProductDetailResponse | null>(null)
const selectedProductHistory = ref<ProductHistoryResponse[]>([])
const message = ref("")
const errorMessage = ref("")

const productItems = ref<ProductSummaryResponse[]>([])
const categoryItems = ref<CategoryResponse[]>([])
const tagItems = ref<TagResponse[]>([])
const categoryKeyword = ref("")
const variantFilters = reactive({
  keyword: "",
  stockStatus: "" as VariantStockStatus | "",
})

const page = ref(0)
const size = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

const productFilters = reactive({
  keyword: "",
  categoryIds: [] as number[],
  tagIds: [] as number[],
  displayStatus: "" as ProductDisplayStatus | "",
  productType: "" as ProductType | "",
  targetGender: "" as TargetGender | "",
  stockStatus: "" as VariantStockStatus | "",
  brand: "",
  material: "",
  inStock: "" as "" | "true" | "false",
  hideOutOfStock: false,
  sort: "newest",
  releaseYear: null as number | null,
  minPrice: null as number | null,
  maxPrice: null as number | null,
  createdWithinDays: null as number | null,
  categoryMatchAll: false,
})

const productModalOpen = ref(false)
const categoryModalOpen = ref(false)
const variantModalOpen = ref(false)
const tagModalOpen = ref(false)
const attributeModalOpen = ref(false)
const quickEditOpen = ref(false)

const editingProductId = ref<number | null>(null)
const editingCategoryId = ref<number | null>(null)
const editingVariantId = ref<number | null>(null)
const editingTagId = ref<number | null>(null)
const editingAttributeId = ref<number | null>(null)
const quickEditProductId = ref<number | null>(null)
const quickEditProductName = ref("")

const productForm = reactive<ProductRequest>({
  name: "",
  baseSku: "",
  brand: "",
  season: "",
  productType: "AO",
  targetGender: "NAM",
  material: "",
  description: "",
  releaseYear: new Date().getFullYear(),
  displayStatus: "HIENTHI",
  categoryIds: [],
  tagIds: [],
})

const categoryForm = reactive<CategoryRequest>({
  name: "",
  description: "",
})

const variantForm = reactive<ProductVariantRequest>({
  sku: "",
  size: "",
  color: "",
  price: null,
  salePrice: null,
  stockQuantity: 0,
  stockStatus: "CON_HANG",
})

const tagForm = reactive<TagRequest>({
  name: "",
  description: "",
})

const attributeForm = reactive<ProductAttributeRequest>({
  name: "",
  value: "",
  sortOrder: 0,
})

const quickEditForm = reactive<ProductQuickUpdateRequest>({
  name: "",
  brand: "",
  material: "",
  season: "",
  displayStatus: undefined,
})

const batchUpdateForm = reactive<Omit<ProductBatchUpdateItemRequest, "id">>({
  brand: "",
  season: "",
  material: "",
  displayStatus: undefined,
})

const selectedProductIds = ref<number[]>([])

const detailStats = computed(() => {
  if (!selectedProduct.value) {
    return {
      imageCount: 0,
      variantCount: 0,
      totalStock: 0,
    }
  }

  return {
    imageCount: selectedProduct.value.images?.length || 0,
    variantCount: selectedProduct.value.variants?.length || 0,
    totalStock: (selectedProduct.value.variants || []).reduce(
      (sum, item) => sum + (item.stockQuantity || 0),
      0,
    ),
  }
})

const productOverview = computed(() => {
  const items = productItems.value || []

  return {
    total: items.length,
    inStock: items.filter((item) => (item.totalStock ?? item.stockQuantity ?? 0) > 0).length,
    outOfStock: items.filter((item) => (item.totalStock ?? item.stockQuantity ?? 0) <= 0).length,
  }
})

function resetMessage() {
  message.value = ""
  errorMessage.value = ""
}

function notifySuccess(text: string) {
  message.value = text
  errorMessage.value = ""
}

function notifyError(error: unknown) {
  const fallback = "Có lỗi xảy ra khi gọi hệ thống."

  if (typeof error === "object" && error !== null && "response" in error) {
    const anyError = error as { response?: { data?: { message?: string } | string } }
    const data = anyError.response?.data

    if (typeof data === "string") {
      errorMessage.value = data
      return
    }

    if (typeof data === "object" && data?.message) {
      errorMessage.value = data.message
      return
    }
  }

  errorMessage.value = fallback
}

function formatCurrency(value?: number | null) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0))
}

function formatDate(value?: string | null) {
  if (!value) return "-"
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value))
}

function statusLabel(value?: string | null) {
  const labels: Record<string, string> = {
    HIENTHI: "Hiển thị",
    AN: "Ẩn",
    NGUNG_BAN: "Ngừng bán",
    CON_HANG: "Còn hàng",
    HET_HANG: "Hết hàng",
    PRE_ORDER: "Đặt trước",
    AO: "Áo",
    QUAN: "Quần",
    VO: "Vớ",
    GANG_TAY: "Găng tay",
    KHAC: "Khác",
    NAM: "Nam",
    NU: "Nữ",
    TRE_EM: "Trẻ em",
    UNISEX: "Dùng chung",
  }

  return labels[value || ""] || value || "-"
}

function toggleSelectedProduct(id: number) {
  selectedProductIds.value = selectedProductIds.value.includes(id)
    ? selectedProductIds.value.filter((item) => item !== id)
    : [...selectedProductIds.value, id]
}

function toggleAllProducts() {
  selectedProductIds.value =
    selectedProductIds.value.length === productItems.value.length
      ? []
      : productItems.value.map((item) => item.id)
}

function clearBatchSelection() {
  selectedProductIds.value = []
  Object.assign(batchUpdateForm, {
    brand: "",
    season: "",
    material: "",
    displayStatus: undefined,
  })
}

function setProductFormFromDetail(detail: ProductDetailResponse) {
  productForm.name = detail.name
  productForm.baseSku = detail.baseSku
  productForm.brand = detail.brand || ""
  productForm.season = detail.season || ""
  productForm.productType = detail.productType
  productForm.targetGender = detail.targetGender
  productForm.material = detail.material || ""
  productForm.description = detail.description || ""
  productForm.releaseYear = detail.releaseYear || new Date().getFullYear()
  productForm.displayStatus = detail.displayStatus
  productForm.categoryIds = (detail.categories || []).map((item) => item.id)
  productForm.tagIds = (detail.tags || []).map((item) => item.id)
}

function resetProductForm() {
  editingProductId.value = null
  Object.assign(productForm, {
    name: "",
    baseSku: "",
    brand: "",
    season: "",
    productType: "AO",
    targetGender: "NAM",
    material: "",
    description: "",
    releaseYear: new Date().getFullYear(),
    displayStatus: "HIENTHI",
    categoryIds: [],
    tagIds: [],
  })
}

function resetCategoryForm() {
  editingCategoryId.value = null
  Object.assign(categoryForm, { name: "", description: "" })
}

function resetVariantForm() {
  editingVariantId.value = null
  Object.assign(variantForm, {
    sku: "",
    size: "",
    color: "",
    price: null,
    salePrice: null,
    stockQuantity: 0,
    stockStatus: "CON_HANG",
  })
}

function resetTagForm() {
  editingTagId.value = null
  Object.assign(tagForm, { name: "", description: "" })
}

function resetAttributeForm() {
  editingAttributeId.value = null
  Object.assign(attributeForm, { name: "", value: "", sortOrder: 0 })
}

function resetQuickEditForm() {
  Object.assign(quickEditForm, {
    name: "",
    brand: "",
    material: "",
    season: "",
    displayStatus: undefined,
  })
}

async function loadCategories(keyword = categoryKeyword.value) {
  loadingCategories.value = true
  categoryKeyword.value = keyword

  try {
    categoryItems.value = await fetchCategories(keyword || undefined)
  } catch (error) {
    notifyError(error)
  } finally {
    loadingCategories.value = false
  }
}

async function loadTags() {
  loadingTags.value = true

  try {
    tagItems.value = await fetchTags()
  } catch (error) {
    notifyError(error)
  } finally {
    loadingTags.value = false
  }
}

async function loadProducts() {
  loadingProducts.value = true

  try {
    const response = await searchProducts({
      page: page.value,
      size: size.value,
      keyword: productFilters.keyword || undefined,
      categoryIds: productFilters.categoryIds,
      tagIds: productFilters.tagIds,
      displayStatus: productFilters.displayStatus || undefined,
      productType: productFilters.productType || undefined,
      targetGender: productFilters.targetGender || undefined,
      stockStatus: productFilters.stockStatus || undefined,
      brand: productFilters.brand || undefined,
      material: productFilters.material || undefined,
      inStock: productFilters.inStock === "" ? null : productFilters.inStock === "true",
      hideOutOfStock: productFilters.hideOutOfStock,
      sort: productFilters.sort,
      releaseYear: productFilters.releaseYear,
      minPrice: productFilters.minPrice,
      maxPrice: productFilters.maxPrice,
      createdWithinDays: productFilters.createdWithinDays,
      categoryMatchAll: productFilters.categoryMatchAll,
    })

    productItems.value = response.content || []
    totalElements.value = response.totalElements || 0
    totalPages.value = response.totalPages || 0
    selectedProductIds.value = selectedProductIds.value.filter((id) =>
      productItems.value.some((item) => item.id === id),
    )
  } catch (error) {
    notifyError(error)
  } finally {
    loadingProducts.value = false
  }
}

async function openProductDetail(id: number) {
  selectedProductId.value = id
  loadingDetail.value = true

  try {
    const [detail, images, attributes, history, variants] = await Promise.all([
      fetchProductDetail(id),
      fetchProductImages(id).catch(() => null),
      fetchProductAttributes(id).catch(() => null),
      fetchProductHistory(id).catch(() => null),
      fetchVariants(
        id,
        variantFilters.keyword || undefined,
        variantFilters.stockStatus || undefined,
      ).catch(() => null),
    ])

    selectedProduct.value = {
      ...detail,
      images: images ?? detail.images ?? [],
      variants: variants ?? detail.variants ?? [],
      categories: detail.categories || [],
      tags: detail.tags || [],
      attributes: attributes ?? detail.attributes ?? [],
    }

    selectedProductHistory.value = history ?? []
  } catch (error) {
    notifyError(error)
  } finally {
    loadingDetail.value = false
  }
}

async function refreshAll() {
  resetMessage()
  await Promise.all([loadCategories(), loadTags(), loadProducts()])

  if (selectedProductId.value) {
    await openProductDetail(selectedProductId.value)
  }
}

function applyFilters() {
  page.value = 0
  loadProducts()
}

function resetFilters() {
  Object.assign(productFilters, {
    keyword: "",
    categoryIds: [],
    tagIds: [],
    displayStatus: "",
    productType: "",
    targetGender: "",
    stockStatus: "",
    brand: "",
    material: "",
    inStock: "",
    hideOutOfStock: false,
    sort: "newest",
    releaseYear: null,
    minPrice: null,
    maxPrice: null,
    createdWithinDays: null,
    categoryMatchAll: false,
  })
  page.value = 0
  loadProducts()
}

function prevPage() {
  if (page.value <= 0) return
  page.value -= 1
  loadProducts()
}

function nextPage() {
  if (page.value + 1 >= totalPages.value) return
  page.value += 1
  loadProducts()
}

function searchCategories(keyword: string) {
  loadCategories(keyword)
}

function applyVariantFilters() {
  if (!selectedProductId.value) return
  openProductDetail(selectedProductId.value)
}

function resetVariantFilters() {
  Object.assign(variantFilters, { keyword: "", stockStatus: "" })
  if (!selectedProductId.value) return
  openProductDetail(selectedProductId.value)
}

function openCreateProduct() {
  resetProductForm()
  productModalOpen.value = true
}

async function openEditProduct(id: number) {
  try {
    const detail = await fetchProductDetail(id)
    editingProductId.value = id
    setProductFormFromDetail(detail)
    productModalOpen.value = true
  } catch (error) {
    notifyError(error)
  }
}

async function submitProduct() {
  savingProduct.value = true

  try {
    if (editingProductId.value) {
      await updateProduct(editingProductId.value, { ...productForm })
      notifySuccess("Đã cập nhật sản phẩm.")
    } else {
      await createProduct({ ...productForm })
      notifySuccess("Đã tạo sản phẩm mới.")
    }

    productModalOpen.value = false
    await refreshAll()
  } catch (error) {
    notifyError(error)
  } finally {
    savingProduct.value = false
  }
}

async function removeProduct(id: number) {
  if (!window.confirm("Xóa mềm sản phẩm này?")) return

  try {
    await deleteProduct(id)
    notifySuccess("Đã xóa sản phẩm.")

    if (selectedProductId.value === id) {
      selectedProductId.value = null
      selectedProduct.value = null
      selectedProductHistory.value = []
    }

    await refreshAll()
  } catch (error) {
    notifyError(error)
  }
}

async function runHideIfOutOfStock(id: number) {
  try {
    await hideProductIfOutOfStock(id)
    notifySuccess("Đã cập nhật trạng thái ẩn nếu hết hàng.")
    await refreshAll()
  } catch (error) {
    notifyError(error)
  }
}

async function openQuickEdit(id: number) {
  try {
    const detail = await fetchProductDetail(id)
    quickEditProductId.value = id
    quickEditProductName.value = detail.name

    Object.assign(quickEditForm, {
      name: detail.name,
      brand: detail.brand || "",
      material: detail.material || "",
      season: detail.season || "",
      displayStatus: detail.displayStatus,
    })

    quickEditOpen.value = true
  } catch (error) {
    notifyError(error)
  }
}

async function submitQuickEdit() {
  if (!quickEditProductId.value) return
  savingQuickUpdate.value = true

  try {
    await quickUpdateProduct(quickEditProductId.value, { ...quickEditForm })
    notifySuccess("Đã cập nhật nhanh sản phẩm.")
    quickEditOpen.value = false
    resetQuickEditForm()
    await refreshAll()
  } catch (error) {
    notifyError(error)
  } finally {
    savingQuickUpdate.value = false
  }
}

async function submitBatchUpdate() {
  if (!selectedProductIds.value.length) return

  const payloadItems = selectedProductIds.value.map((id) => ({
    id,
    brand: batchUpdateForm.brand?.trim() || undefined,
    season: batchUpdateForm.season?.trim() || undefined,
    material: batchUpdateForm.material?.trim() || undefined,
    displayStatus: batchUpdateForm.displayStatus,
  }))

  const hasAnyField = payloadItems.some(
    (item) => item.brand || item.season || item.material || item.displayStatus,
  )

  if (!hasAnyField) {
    errorMessage.value = "Cập nhật hàng loạt cần ít nhất 1 trường để cập nhật."
    return
  }

  savingBatchUpdate.value = true

  try {
    await batchUpdateProducts({ items: payloadItems })
    notifySuccess(`Đã cập nhật hàng loạt ${selectedProductIds.value.length} sản phẩm.`)
    clearBatchSelection()
    await refreshAll()
  } catch (error) {
    notifyError(error)
  } finally {
    savingBatchUpdate.value = false
  }
}

function openCreateCategory() {
  resetCategoryForm()
  categoryModalOpen.value = true
}

function openEditCategory(category: CategoryResponse) {
  editingCategoryId.value = category.id
  categoryForm.name = category.name
  categoryForm.description = category.description || ""
  categoryModalOpen.value = true
}

async function submitCategory() {
  savingCategory.value = true

  try {
    if (editingCategoryId.value) {
      await updateCategory(editingCategoryId.value, { ...categoryForm })
      notifySuccess("Đã cập nhật danh mục.")
    } else {
      await createCategory({ ...categoryForm })
      notifySuccess("Đã tạo danh mục mới.")
    }

    categoryModalOpen.value = false
    await loadCategories()
  } catch (error) {
    notifyError(error)
  } finally {
    savingCategory.value = false
  }
}

async function removeCategory(id: number) {
  if (!window.confirm("Xóa danh mục này?")) return

  try {
    await deleteCategory(id)
    notifySuccess("Đã xóa danh mục.")
    await loadCategories()
  } catch (error) {
    notifyError(error)
  }
}

async function handleCategoryImageChange(payload: { id: number; event: Event }) {
  const input = payload.event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  try {
    await uploadCategoryImage(payload.id, file)
    notifySuccess("Đã tải ảnh danh mục lên.")
    await loadCategories()
  } catch (error) {
    notifyError(error)
  } finally {
    input.value = ""
  }
}

function openCreateTag() {
  resetTagForm()
  tagModalOpen.value = true
}

function openEditTag(tag: TagResponse) {
  editingTagId.value = tag.id
  tagForm.name = tag.name
  tagForm.description = tag.description || ""
  tagModalOpen.value = true
}

async function submitTag() {
  savingTag.value = true

  try {
    if (editingTagId.value) {
      await updateTag(editingTagId.value, { ...tagForm })
      notifySuccess("Đã cập nhật thẻ.")
    } else {
      await createTag({ ...tagForm })
      notifySuccess("Đã tạo thẻ mới.")
    }

    tagModalOpen.value = false
    await loadTags()
  } catch (error) {
    notifyError(error)
  } finally {
    savingTag.value = false
  }
}

async function removeTag(id: number) {
  if (!window.confirm("Xóa thẻ này?")) return

  try {
    await deleteTag(id)
    notifySuccess("Đã xóa thẻ.")
    await loadTags()
  } catch (error) {
    notifyError(error)
  }
}

function openCreateVariant() {
  resetVariantForm()
  variantModalOpen.value = true
}

function openEditVariant(variant: ProductVariantResponse) {
  editingVariantId.value = variant.id
  Object.assign(variantForm, {
    sku: variant.sku,
    size: variant.size,
    color: variant.color,
    price: variant.price,
    salePrice: variant.salePrice || null,
    stockQuantity: variant.stockQuantity,
    stockStatus: variant.stockStatus,
  })
  variantModalOpen.value = true
}

async function submitVariant() {
  if (!selectedProductId.value) return

  try {
    if (editingVariantId.value) {
      await updateVariant(editingVariantId.value, { ...variantForm })
      notifySuccess("Đã cập nhật biến thể.")
    } else {
      await createVariant(selectedProductId.value, { ...variantForm })
      notifySuccess("Đã tạo biến thể mới.")
    }

    variantModalOpen.value = false
    await openProductDetail(selectedProductId.value)
    await loadProducts()
  } catch (error) {
    notifyError(error)
  }
}

async function removeVariant(id: number) {
  if (!selectedProductId.value || !window.confirm("Xóa biến thể này?")) return

  try {
    await deleteVariant(id)
    notifySuccess("Đã xóa biến thể.")
    await openProductDetail(selectedProductId.value)
    await loadProducts()
  } catch (error) {
    notifyError(error)
  }
}

function openCreateAttribute() {
  resetAttributeForm()
  attributeModalOpen.value = true
}

function openEditAttribute(attribute: ProductAttributeResponse) {
  editingAttributeId.value = attribute.id
  Object.assign(attributeForm, {
    name: attribute.name,
    value: attribute.value,
    sortOrder: attribute.sortOrder,
  })
  attributeModalOpen.value = true
}

async function submitAttribute() {
  if (!selectedProductId.value) return
  savingAttribute.value = true

  try {
    if (editingAttributeId.value) {
      await updateProductAttribute(editingAttributeId.value, { ...attributeForm })
      notifySuccess("Đã cập nhật thuộc tính.")
    } else {
      await createProductAttribute(selectedProductId.value, { ...attributeForm })
      notifySuccess("Đã tạo thuộc tính mới.")
    }

    attributeModalOpen.value = false
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  } finally {
    savingAttribute.value = false
  }
}

async function removeAttribute(id: number) {
  if (!selectedProductId.value || !window.confirm("Xóa thuộc tính này?")) return

  try {
    await deleteProductAttribute(id)
    notifySuccess("Đã xóa thuộc tính.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  }
}

async function handleProductImagesChange(event: Event) {
  const input = event.target as HTMLInputElement
  const files = input.files ? Array.from(input.files) : []
  if (!selectedProductId.value || !files.length) return

  uploadingProductImages.value = true

  try {
    await uploadProductImages(selectedProductId.value, files, true)
    notifySuccess("Đã tải ảnh sản phẩm lên.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  } finally {
    uploadingProductImages.value = false
    input.value = ""
  }
}

async function setAvatar(image: ProductImageResponse) {
  if (!selectedProductId.value) return

  try {
    await updateProductImage(image.id, {
      avatar: true,
      sortOrder: image.sortOrder,
    })
    notifySuccess("Đã đặt ảnh đại diện.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  }
}

function handleImageOrderChange(image: ProductImageResponse, event: Event) {
  if (!selectedProductId.value) return

  const target = event.target as HTMLInputElement
  const nextSortOrder = Number(target.value)

  if (!Number.isInteger(nextSortOrder) || nextSortOrder < 0) {
    errorMessage.value = "Thứ tự ảnh phải là số nguyên không âm."
    return
  }

  updateProductImage(image.id, {
    avatar: image.avatar,
    sortOrder: nextSortOrder,
  })
    .then(() => {
      notifySuccess("Đã cập nhật thứ tự sắp xếp ảnh.")
      return openProductDetail(selectedProductId.value as number)
    })
    .catch(notifyError)
}

async function removeImage(imageId: number) {
  if (!selectedProductId.value || !window.confirm("Xóa ảnh này?")) return

  try {
    await deleteProductImage(imageId)
    notifySuccess("Đã xóa ảnh.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  }
}

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <section class="space-y-6">
    <div
      class="flex flex-col gap-5 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between"
    >
      <div>
        <div
          class="inline-flex items-center rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600"
        >
          Quản lý sản phẩm
        </div>
        <h1 class="mt-3 text-2xl font-bold tracking-tight text-slate-900">
          Bảng điều khiển sản phẩm
        </h1>
        <p class="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
          Quản lý sản phẩm, danh mục, thẻ và biến thể trong cùng một màn hình.
        </p>
      </div>

      <div class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
          <div class="text-xs text-slate-500">Sản phẩm trong trang</div>
          <div class="mt-1 text-2xl font-bold text-slate-900">{{ productOverview.total }}</div>
        </div>

        <div class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3">
          <div class="text-xs text-emerald-700">Còn hàng</div>
          <div class="mt-1 text-2xl font-bold text-emerald-700">
            {{ productOverview.inStock }}
          </div>
        </div>

        <div class="rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3">
          <div class="text-xs text-amber-700">Hết hàng</div>
          <div class="mt-1 text-2xl font-bold text-amber-700">
            {{ productOverview.outOfStock }}
          </div>
        </div>

        <button
          @click="refreshAll"
          class="flex items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
        >
          <RefreshCcw :size="16" />
          Làm mới dữ liệu
        </button>
      </div>
    </div>

    <div
      v-if="message"
      class="flex items-center gap-2 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700 shadow-sm"
    >
      <Boxes :size="16" />
      {{ message }}
    </div>

    <div
      v-if="errorMessage"
      class="flex items-center gap-2 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700 shadow-sm"
    >
      <AlertCircle :size="16" />
      {{ errorMessage }}
    </div>

    <div class="grid items-start gap-6 xl:grid-cols-[1.6fr_1fr]">
      <div class="space-y-6">
        <div class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
          <div class="mb-5 flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 pb-4">
            <div class="inline-flex rounded-2xl border border-slate-200 bg-white p-1 shadow-sm">
              <button
                @click="activeTab = 'products'"
                :class="
                  activeTab === 'products'
                    ? 'bg-slate-900 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-100'
                "
                class="rounded-xl px-4 py-2 text-sm font-medium transition"
              >
                Sản phẩm
              </button>

              <button
                @click="activeTab = 'categories'"
                :class="
                  activeTab === 'categories'
                    ? 'bg-slate-900 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-100'
                "
                class="rounded-xl px-4 py-2 text-sm font-medium transition"
              >
                Danh mục
              </button>

              <button
                @click="activeTab = 'tags'"
                :class="
                  activeTab === 'tags'
                    ? 'bg-slate-900 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-100'
                "
                class="rounded-xl px-4 py-2 text-sm font-medium transition"
              >
                Thẻ
              </button>
            </div>

            <div class="grid grid-cols-2 gap-3 md:grid-cols-2">
              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <div class="text-xs text-slate-500">Danh mục</div>
                <div class="mt-1 text-xl font-semibold text-slate-900">
                  {{ categoryItems.length }}
                </div>
              </div>

              <div class="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
                <div class="text-xs text-slate-500">Thẻ</div>
                <div class="mt-1 text-xl font-semibold text-slate-900">
                  {{ tagItems.length }}
                </div>
              </div>
            </div>
          </div>

          <template v-if="activeTab === 'products'">
            <div class="space-y-4">
              <ProductFilters
                :filters="productFilters"
                :category-items="categoryItems"
                :tag-items="tagItems"
                :display-status-options="DISPLAY_STATUS_OPTIONS"
                :product-types="PRODUCT_TYPES"
                :genders="GENDERS"
                :stock-status-options="STOCK_STATUS_OPTIONS"
                :sort-options="SORT_OPTIONS"
                @apply="applyFilters"
                @reset="resetFilters"
                @create-product="openCreateProduct"
              />

              <ProductBatchUpdateBar
                :selected-count="selectedProductIds.length"
                :saving="savingBatchUpdate"
                :display-status-options="DISPLAY_STATUS_OPTIONS"
                :form="batchUpdateForm"
                @apply="submitBatchUpdate"
                @clear="clearBatchSelection"
              />

              <ProductTable
                :items="productItems"
                :loading="loadingProducts"
                :selected-product-ids="selectedProductIds"
                :page="page"
                :total-pages="totalPages"
                :total-elements="totalElements"
                :format-currency="formatCurrency"
                :status-label="statusLabel"
                @toggle-all="toggleAllProducts"
                @toggle-select="toggleSelectedProduct"
                @view="openProductDetail"
                @edit="openEditProduct"
                @quick-edit="openQuickEdit"
                @hide-oos="runHideIfOutOfStock"
                @delete="removeProduct"
                @prev-page="prevPage"
                @next-page="nextPage"
              />
            </div>
          </template>

          <template v-else-if="activeTab === 'categories'">
            <CategoryManager
              :loading="loadingCategories"
              :items="categoryItems"
              :keyword="categoryKeyword"
              @create="openCreateCategory"
              @edit="openEditCategory"
              @delete="removeCategory"
              @upload-image="handleCategoryImageChange"
              @refresh="loadCategories('')"
              @search="searchCategories"
            />
          </template>

          <template v-else>
            <TagManager
              :loading="loadingTags"
              :items="tagItems"
              @create="openCreateTag"
              @edit="openEditTag"
              @delete="removeTag"
            />
          </template>
        </div>
      </div>

      <ProductDetail
        :selected-product="selectedProduct"
        :loading-detail="loadingDetail"
        :uploading-product-images="uploadingProductImages"
        :detail-stats="detailStats"
        :history-items="selectedProductHistory"
        :format-currency="formatCurrency"
        :format-date="formatDate"
        :status-label="statusLabel"
        :variant-filters="variantFilters"
        :stock-status-options="STOCK_STATUS_OPTIONS"
        @edit-product="openEditProduct"
        @quick-edit="openQuickEdit"
        @hide-oos="runHideIfOutOfStock"
        @delete-soft="removeProduct"
        @create-variant="openCreateVariant"
        @edit-variant="openEditVariant"
        @remove-variant="removeVariant"
        @variant-filter-change="applyVariantFilters"
        @variant-filter-reset="resetVariantFilters"
        @upload-images="handleProductImagesChange"
        @set-avatar="setAvatar"
        @remove-image="removeImage"
        @image-order-change="(payload) => handleImageOrderChange(payload.image, payload.event)"
        @create-attribute="openCreateAttribute"
        @edit-attribute="openEditAttribute"
        @remove-attribute="removeAttribute"
      />
    </div>

    <ProductModal
      :open="productModalOpen"
      :editing-product-id="editingProductId"
      :form="productForm"
      :category-items="categoryItems"
      :tag-items="tagItems"
      :saving-product="savingProduct"
      :product-types="PRODUCT_TYPES"
      :genders="GENDERS"
      :display-status-options="DISPLAY_STATUS_OPTIONS"
      @close="productModalOpen = false"
      @submit="submitProduct"
    />

    <ProductQuickEditModal
      :open="quickEditOpen"
      :product-name="quickEditProductName"
      :saving="savingQuickUpdate"
      :display-status-options="DISPLAY_STATUS_OPTIONS"
      :form="quickEditForm"
      @close="quickEditOpen = false"
      @submit="submitQuickEdit"
    />

    <CategoryModal
      :open="categoryModalOpen"
      :saving="savingCategory"
      :editing-category-id="editingCategoryId"
      :form="categoryForm"
      @close="categoryModalOpen = false"
      @submit="submitCategory"
    />

    <div
      v-if="variantModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
    >
      <div class="w-full max-w-2xl rounded-3xl bg-white shadow-2xl">
        <div class="flex items-center justify-between border-b border-slate-100 px-6 py-5">
          <h3 class="text-lg font-semibold text-slate-900">
            {{ editingVariantId ? "Sửa biến thể" : "Thêm biến thể" }}
          </h3>

          <button
            @click="variantModalOpen = false"
            class="rounded-xl border border-slate-200 p-2 text-slate-600 transition hover:bg-slate-50"
          >
            <X :size="16" />
          </button>
        </div>

        <div class="grid gap-4 px-6 py-5 md:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">SKU biến thể</span>
            <input
              v-model="variantForm.sku"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">Kích thước</span>
            <input
              v-model="variantForm.size"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">Màu</span>
            <input
              v-model="variantForm.color"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">Giá bán</span>
            <input
              v-model.number="variantForm.price"
              type="number"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">Giá khuyến mãi</span>
            <input
              v-model.number="variantForm.salePrice"
              type="number"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2">
            <span class="text-sm font-medium text-slate-700">Tồn kho</span>
            <input
              v-model.number="variantForm.stockQuantity"
              type="number"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            />
          </label>

          <label class="space-y-2 md:col-span-2">
            <span class="text-sm font-medium text-slate-700">Trạng thái tồn kho</span>
            <select
              v-model="variantForm.stockStatus"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2.5 outline-none transition focus:border-slate-400 focus:bg-white"
            >
              <option
                v-for="item in STOCK_STATUS_OPTIONS"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </option>
            </select>
          </label>
        </div>

        <div class="flex justify-end gap-3 border-t border-slate-100 px-6 py-5">
          <button
            @click="variantModalOpen = false"
            class="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
          >
            Hủy
          </button>

          <button
            @click="submitVariant"
            class="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
          >
            {{ editingVariantId ? "Lưu thay đổi" : "Tạo biến thể" }}
          </button>
        </div>
      </div>
    </div>

    <TagModal
      :open="tagModalOpen"
      :saving="savingTag"
      :editing-tag-id="editingTagId"
      :form="tagForm"
      @close="tagModalOpen = false"
      @submit="submitTag"
    />

    <AttributeModal
      :open="attributeModalOpen"
      :saving="savingAttribute"
      :editing-attribute-id="editingAttributeId"
      :form="attributeForm"
      @close="attributeModalOpen = false"
      @submit="submitAttribute"
    />
  </section>
</template>