<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import {
  AlertCircle,
  Boxes,
  Eye,
  FolderTree,
  History,
  LoaderCircle,
  Pencil,
  Plus,
  RefreshCcw,
  Search,
  Tags,
  Trash2,
  Upload,
  X,
} from "lucide-vue-next"
import {
  batchUpdateProducts,
  createCategory,
  createProduct,
  createTag,
  createVariant,
  deleteCategory,
  deleteProduct,
  deleteProductImage,
  deleteTag,
  deleteVariant,
  fetchCategories,
  fetchCategoryProducts,
  fetchNewestProducts,
  fetchProductDetail,
  fetchProductHistory,
  fetchProductImages,
  fetchTags,
  fetchTopSellingProducts,
  hardDeleteProduct,
  hideProductIfOutOfStock,
  quickUpdateProduct,
  searchProducts,
  updateCategory,
  updateProduct,
  updateProductImage,
  updateTag,
  updateVariant,
  uploadCategoryImage,
  uploadProductImages,
} from "@/modules/product/services/product.api"
import type {
  CategoryRequest,
  CategoryResponse,
  ProductBatchUpdateItemRequest,
  ProductDetailResponse,
  ProductDisplayStatus,
  ProductHistoryResponse,
  ProductImageResponse,
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
  { label: "Unisex", value: "UNISEX" },
]

const DISPLAY_STATUS_OPTIONS: Array<{ label: string; value: ProductDisplayStatus }> = [
  { label: "Hiển thị", value: "HIENTHI" },
  { label: "Ẩn", value: "AN" },
  { label: "Ngừng bán", value: "NGUNG_BAN" },
]

const STOCK_STATUS_OPTIONS: Array<{ label: string; value: VariantStockStatus }> = [
  { label: "Còn hàng", value: "CON_HANG" },
  { label: "Hết hàng", value: "HET_HANG" },
  { label: "Pre-order", value: "PRE_ORDER" },
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
const uploadingProductImages = ref(false)
const selectedProductId = ref<number | null>(null)
const selectedProduct = ref<ProductDetailResponse | null>(null)
const selectedProductHistory = ref<ProductHistoryResponse[]>([])
const topSelling = ref<ProductSummaryResponse[]>([])
const newestProducts = ref<ProductSummaryResponse[]>([])
const categoryPreviewProducts = ref<ProductSummaryResponse[]>([])
const categoryPreviewId = ref<number | null>(null)
const message = ref("")
const errorMessage = ref("")

const productItems = ref<ProductSummaryResponse[]>([])
const categoryItems = ref<CategoryResponse[]>([])
const tagItems = ref<TagResponse[]>([])

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
})

const productModalOpen = ref(false)
const categoryModalOpen = ref(false)
const tagModalOpen = ref(false)
const variantModalOpen = ref(false)
const quickUpdateOpen = ref(false)
const batchModalOpen = ref(false)

const editingProductId = ref<number | null>(null)
const editingCategoryId = ref<number | null>(null)
const editingTagId = ref<number | null>(null)
const editingVariantId = ref<number | null>(null)
const categoryImageUploadId = ref<number | null>(null)

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

const tagForm = reactive<TagRequest>({
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

const quickUpdateForm = reactive({
  name: "",
  brand: "",
  season: "",
  material: "",
  displayStatus: "HIENTHI" as ProductDisplayStatus,
})

const batchUpdateRows = ref<ProductBatchUpdateItemRequest[]>([])

const selectedProductIds = ref<number[]>([])

const detailStats = computed(() => {
  if (!selectedProduct.value) return { imageCount: 0, variantCount: 0, totalStock: 0 }
  return {
    imageCount: selectedProduct.value.images.length,
    variantCount: selectedProduct.value.variants.length,
    totalStock: selectedProduct.value.variants.reduce((sum, item) => sum + (item.stockQuantity || 0), 0),
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
  const fallback = "Có lỗi xảy ra khi gọi API backend."
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
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(value || 0))
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
    PRE_ORDER: "Pre-order",
  }
  return labels[value || ""] || value || "-"
}

function toggleSelectedProduct(id: number) {
  selectedProductIds.value = selectedProductIds.value.includes(id)
    ? selectedProductIds.value.filter((item) => item !== id)
    : [...selectedProductIds.value, id]
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
  productForm.categoryIds = detail.categories.map((item) => item.id)
  productForm.tagIds = detail.tags.map((item) => item.id)
}

function resetProductForm() {
  editingProductId.value = null
  productForm.name = ""
  productForm.baseSku = ""
  productForm.brand = ""
  productForm.season = ""
  productForm.productType = "AO"
  productForm.targetGender = "NAM"
  productForm.material = ""
  productForm.description = ""
  productForm.releaseYear = new Date().getFullYear()
  productForm.displayStatus = "HIENTHI"
  productForm.categoryIds = []
  productForm.tagIds = []
}

function resetCategoryForm() {
  editingCategoryId.value = null
  categoryForm.name = ""
  categoryForm.description = ""
  categoryImageUploadId.value = null
}

function resetTagForm() {
  editingTagId.value = null
  tagForm.name = ""
  tagForm.description = ""
}

function resetVariantForm() {
  editingVariantId.value = null
  variantForm.sku = ""
  variantForm.size = ""
  variantForm.color = ""
  variantForm.price = null
  variantForm.salePrice = null
  variantForm.stockQuantity = 0
  variantForm.stockStatus = "CON_HANG"
}

onMounted(async () => {
  console.log("ProductHomeView mounted")
  try {
    await refreshAll()
    console.log("refreshAll done")
  } catch (e) {
    console.error("refreshAll error", e)
  }
})

async function loadCategories() {
  console.log("loadCategories called")
  loadingCategories.value = true
  try {
    categoryItems.value = await fetchCategories()
    console.log("categories ok", categoryItems.value)
  } catch (error) {
    console.error("loadCategories error", error)
    notifyError(error)
  } finally {
    loadingCategories.value = false
  }
}

async function loadTags() {
  console.log("loadTags called")
  loadingTags.value = true
  try {
    tagItems.value = await fetchTags()
    console.log("tags ok", tagItems.value)
  } catch (error) {
    console.error("loadTags error", error)
    notifyError(error)
  } finally {
    loadingTags.value = false
  }
}

async function loadProducts() {
  console.log("loadProducts called")
  loadingProducts.value = true
  try {
    const response = await searchProducts({
      page: page.value,
      size: size.value,
      keyword: productFilters.keyword || undefined,
      categoryIds: productFilters.categoryIds,
      tagIds: productFilters.tagIds,
      displayStatus: productFilters.displayStatus,
      productType: productFilters.productType,
      targetGender: productFilters.targetGender,
      stockStatus: productFilters.stockStatus,
      brand: productFilters.brand || undefined,
      material: productFilters.material || undefined,
      inStock: productFilters.inStock === "" ? null : productFilters.inStock === "true",
      hideOutOfStock: productFilters.hideOutOfStock,
      sort: productFilters.sort,
    })
    console.log("products ok", response)
    productItems.value = response.content
    totalElements.value = response.totalElements
    totalPages.value = response.totalPages
  } catch (error) {
    console.error("loadProducts error", error)
    notifyError(error)
  } finally {
    loadingProducts.value = false
  }
}

async function loadDashboardLists() {
  try {
    const [top, newest] = await Promise.all([fetchTopSellingProducts(), fetchNewestProducts(5)])
    topSelling.value = top
    newestProducts.value = newest
  } catch {
    // ignore extra panel errors
  }
}

async function openProductDetail(id: number) {
  selectedProductId.value = id
  loadingDetail.value = true
  try {
    const [detail, history, freshImages] = await Promise.all([
      fetchProductDetail(id),
      fetchProductHistory(id),
      fetchProductImages(id),
    ])
    selectedProduct.value = { ...detail, images: freshImages }
    selectedProductHistory.value = history
  } catch (error) {
    notifyError(error)
  } finally {
    loadingDetail.value = false
  }
}

async function refreshAll() {
  resetMessage()
  await Promise.all([loadCategories(), loadTags(), loadProducts(), loadDashboardLists()])
  if (selectedProductId.value) await openProductDetail(selectedProductId.value)
}

function openCreateProduct() {
  resetProductForm()
  productModalOpen.value = true
}

async function openEditProduct(id: number) {
  resetMessage()
  const detail = await fetchProductDetail(id)
  editingProductId.value = id
  setProductFormFromDetail(detail)
  productModalOpen.value = true
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

async function removeProduct(id: number, hard = false) {
  if (!window.confirm(hard ? "Xóa cứng sản phẩm này?" : "Xóa mềm sản phẩm này?")) return
  try {
    if (hard) await hardDeleteProduct(id)
    else await deleteProduct(id)
    notifySuccess(hard ? "Đã xóa cứng sản phẩm." : "Đã xóa sản phẩm.")
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

function openQuickUpdateModal(item: ProductSummaryResponse) {
  selectedProductId.value = item.id
  quickUpdateForm.name = item.name
  quickUpdateForm.brand = item.brand || ""
  quickUpdateForm.season = item.season || ""
  quickUpdateForm.material = item.material || ""
  quickUpdateForm.displayStatus = item.displayStatus
  quickUpdateOpen.value = true
}

async function submitQuickUpdate() {
  if (!selectedProductId.value) return
  try {
    await quickUpdateProduct(selectedProductId.value, { ...quickUpdateForm })
    quickUpdateOpen.value = false
    notifySuccess("Đã cập nhật nhanh sản phẩm.")
    await refreshAll()
  } catch (error) {
    notifyError(error)
  }
}

function openBatchUpdate() {
  batchUpdateRows.value = productItems.value
    .filter((item) => selectedProductIds.value.includes(item.id))
    .map((item) => ({
      id: item.id,
      displayStatus: item.displayStatus,
      brand: item.brand || "",
      season: item.season || "",
      material: item.material || "",
    }))
  batchModalOpen.value = batchUpdateRows.value.length > 0
}

async function submitBatchUpdate() {
  try {
    await batchUpdateProducts({ items: batchUpdateRows.value })
    batchModalOpen.value = false
    selectedProductIds.value = []
    notifySuccess("Đã batch update sản phẩm.")
    await refreshAll()
  } catch (error) {
    notifyError(error)
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
      notifySuccess("Đã cập nhật category.")
    } else {
      await createCategory({ ...categoryForm })
      notifySuccess("Đã tạo category mới.")
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
  if (!window.confirm("Xóa category này?")) return
  try {
    await deleteCategory(id)
    notifySuccess("Đã xóa category.")
    if (categoryPreviewId.value === id) {
      categoryPreviewId.value = null
      categoryPreviewProducts.value = []
    }
    await loadCategories()
  } catch (error) {
    notifyError(error)
  }
}

async function handleCategoryImageChange(event: Event) {
  const id = categoryImageUploadId.value
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!id || !file) return
  try {
    await uploadCategoryImage(id, file)
    notifySuccess("Đã upload ảnh category.")
    await loadCategories()
  } catch (error) {
    notifyError(error)
  } finally {
    input.value = ""
  }
}

async function previewCategoryProducts(categoryId: number) {
  categoryPreviewId.value = categoryId
  try {
    categoryPreviewProducts.value = await fetchCategoryProducts(categoryId)
  } catch (error) {
    notifyError(error)
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
      notifySuccess("Đã cập nhật tag.")
    } else {
      await createTag({ ...tagForm })
      notifySuccess("Đã tạo tag mới.")
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
  if (!window.confirm("Xóa tag này?")) return
  try {
    await deleteTag(id)
    notifySuccess("Đã xóa tag.")
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
  variantForm.sku = variant.sku
  variantForm.size = variant.size
  variantForm.color = variant.color
  variantForm.price = variant.price
  variantForm.salePrice = variant.salePrice || null
  variantForm.stockQuantity = variant.stockQuantity
  variantForm.stockStatus = variant.stockStatus
  variantModalOpen.value = true
}

async function submitVariant() {
  if (!selectedProductId.value) return
  try {
    if (editingVariantId.value) {
      await updateVariant(editingVariantId.value, { ...variantForm })
      notifySuccess("Đã cập nhật variant.")
    } else {
      await createVariant(selectedProductId.value, { ...variantForm })
      notifySuccess("Đã tạo variant mới.")
    }
    variantModalOpen.value = false
    await openProductDetail(selectedProductId.value)
    await loadProducts()
  } catch (error) {
    notifyError(error)
  }
}

async function removeVariant(id: number) {
  if (!selectedProductId.value || !window.confirm("Xóa variant này?")) return
  try {
    await deleteVariant(id)
    notifySuccess("Đã xóa variant.")
    await openProductDetail(selectedProductId.value)
    await loadProducts()
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
    notifySuccess("Đã upload ảnh sản phẩm.")
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
    await updateProductImage(image.id, { avatar: true, sortOrder: image.sortOrder })
    notifySuccess("Đã đặt ảnh đại diện.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  }
}

async function updateImageOrder(image: ProductImageResponse, value: number) {
  if (!selectedProductId.value) return
  try {
    await updateProductImage(image.id, { avatar: image.avatar, sortOrder: value })
    notifySuccess("Đã cập nhật thứ tự ảnh.")
    await openProductDetail(selectedProductId.value)
  } catch (error) {
    notifyError(error)
  }
}

function handleImageOrderChange(image: ProductImageResponse, event: Event) {
  const target = event.target as HTMLInputElement
  updateImageOrder(image, Number(target.value))
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
    <div class="flex flex-col gap-4 rounded-2xl border bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between">
      <div>
        <p class="text-sm text-slate-500">Inventory / Product & Category</p>
        <h1 class="mt-1 text-2xl font-bold text-slate-900">Quản lý Catalog Goal Store</h1>
        <p class="mt-2 max-w-3xl text-sm text-slate-600">
          Giao diện này đã nối thẳng với backend phần product, category, tag, image, variant và history.
        </p>
      </div>
      <div class="grid grid-cols-2 gap-3 lg:grid-cols-4">
        <div class="rounded-xl bg-slate-50 p-3">
          <div class="text-xs text-slate-500">Tổng sản phẩm</div>
          <div class="mt-1 text-xl font-semibold">{{ totalElements }}</div>
        </div>
        <div class="rounded-xl bg-slate-50 p-3">
          <div class="text-xs text-slate-500">Categories</div>
          <div class="mt-1 text-xl font-semibold">{{ categoryItems.length }}</div>
        </div>
        <div class="rounded-xl bg-slate-50 p-3">
          <div class="text-xs text-slate-500">Tags</div>
          <div class="mt-1 text-xl font-semibold">{{ tagItems.length }}</div>
        </div>
        <button
          @click="refreshAll"
          class="flex items-center justify-center gap-2 rounded-xl border px-4 py-3 text-sm font-medium hover:bg-slate-50"
        >
          <RefreshCcw :size="16" /> Làm mới
        </button>
      </div>
    </div>

    <div v-if="message" class="flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
      <Boxes :size="16" /> {{ message }}
    </div>
    <div v-if="errorMessage" class="flex items-center gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
      <AlertCircle :size="16" /> {{ errorMessage }}
    </div>

    <div class="grid gap-6 xl:grid-cols-[1.6fr_1fr]">
      <div class="space-y-6">
        <div class="rounded-2xl border bg-white p-4 shadow-sm">
          <div class="flex flex-wrap gap-2 border-b pb-4">
            <button
              @click="activeTab = 'products'"
              :class="activeTab === 'products' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'"
              class="rounded-lg px-4 py-2 text-sm font-medium"
            >
              Sản phẩm
            </button>
            <button
              @click="activeTab = 'categories'"
              :class="activeTab === 'categories' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'"
              class="rounded-lg px-4 py-2 text-sm font-medium"
            >
              Category
            </button>
            <button
              @click="activeTab = 'tags'"
              :class="activeTab === 'tags' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'"
              class="rounded-lg px-4 py-2 text-sm font-medium"
            >
              Tags
            </button>
          </div>

          <template v-if="activeTab === 'products'">
            <div class="mt-4 grid gap-3 md:grid-cols-2 xl:grid-cols-4">
              <label class="relative">
                <Search class="absolute left-3 top-3 text-slate-400" :size="16" />
                <input v-model="productFilters.keyword" class="w-full rounded-xl border px-9 py-2.5 text-sm" placeholder="Tên sản phẩm, SKU..." />
              </label>
              <select v-model="productFilters.displayStatus" class="rounded-xl border px-3 py-2.5 text-sm">
                <option value="">Trạng thái hiển thị</option>
                <option v-for="item in DISPLAY_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
              <select v-model="productFilters.productType" class="rounded-xl border px-3 py-2.5 text-sm">
                <option value="">Loại sản phẩm</option>
                <option v-for="item in PRODUCT_TYPES" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
              <select v-model="productFilters.targetGender" class="rounded-xl border px-3 py-2.5 text-sm">
                <option value="">Đối tượng</option>
                <option v-for="item in GENDERS" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
              <select v-model="productFilters.stockStatus" class="rounded-xl border px-3 py-2.5 text-sm">
                <option value="">Tồn kho</option>
                <option v-for="item in STOCK_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
              <select v-model="productFilters.inStock" class="rounded-xl border px-3 py-2.5 text-sm">
                <option value="">Có hàng / hết hàng</option>
                <option value="true">Chỉ còn hàng</option>
                <option value="false">Chỉ hết hàng</option>
              </select>
              <input v-model="productFilters.brand" class="rounded-xl border px-3 py-2.5 text-sm" placeholder="Brand" />
              <select v-model="productFilters.sort" class="rounded-xl border px-3 py-2.5 text-sm">
                <option v-for="item in SORT_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
            </div>

            <div class="mt-3 grid gap-3 md:grid-cols-2">
              <label class="space-y-2">
                <span class="text-xs font-medium text-slate-500">Filter category</span>
                <select v-model="productFilters.categoryIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2 text-sm">
                  <option v-for="category in categoryItems" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>
              </label>
              <label class="space-y-2">
                <span class="text-xs font-medium text-slate-500">Filter tag</span>
                <select v-model="productFilters.tagIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2 text-sm">
                  <option v-for="tag in tagItems" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
                </select>
              </label>
            </div>

            <div class="mt-4 flex flex-wrap items-center gap-3">
              <label class="flex items-center gap-2 rounded-xl border px-3 py-2 text-sm">
                <input v-model="productFilters.hideOutOfStock" type="checkbox" /> Ẩn sản phẩm hết hàng
              </label>
              <button @click="page = 0; loadProducts()" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Áp dụng filter</button>
              <button
                @click="productFilters.keyword=''; productFilters.categoryIds=[]; productFilters.tagIds=[]; productFilters.displayStatus=''; productFilters.productType=''; productFilters.targetGender=''; productFilters.stockStatus=''; productFilters.brand=''; productFilters.material=''; productFilters.inStock=''; productFilters.hideOutOfStock=false; productFilters.sort='newest'; page=0; loadProducts()"
                class="rounded-xl border px-4 py-2 text-sm font-medium"
              >
                Xóa filter
              </button>
              <button @click="openCreateProduct" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">
                <span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm sản phẩm</span>
              </button>
              <button @click="openBatchUpdate" :disabled="selectedProductIds.length === 0" class="rounded-xl border px-4 py-2 text-sm font-medium disabled:opacity-50">
                Batch update ({{ selectedProductIds.length }})
              </button>
            </div>

            <div class="mt-4 overflow-hidden rounded-2xl border">
              <div v-if="loadingProducts" class="flex items-center justify-center gap-2 p-10 text-sm text-slate-500">
                <LoaderCircle class="animate-spin" :size="18" /> Đang tải sản phẩm...
              </div>
              <table v-else class="min-w-full divide-y divide-slate-200 text-sm">
                <thead class="bg-slate-50 text-left text-slate-600">
                  <tr>
                    <th class="px-4 py-3"><input type="checkbox" :checked="selectedProductIds.length === productItems.length && productItems.length > 0" @change="selectedProductIds = selectedProductIds.length === productItems.length ? [] : productItems.map(item => item.id)" /></th>
                    <th class="px-4 py-3">Sản phẩm</th>
                    <th class="px-4 py-3">Category</th>
                    <th class="px-4 py-3">Giá</th>
                    <th class="px-4 py-3">Tồn kho</th>
                    <th class="px-4 py-3">Trạng thái</th>
                    <th class="px-4 py-3 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 bg-white">
                  <tr v-for="item in productItems" :key="item.id" class="hover:bg-slate-50">
                    <td class="px-4 py-3 align-top"><input type="checkbox" :checked="selectedProductIds.includes(item.id)" @change="toggleSelectedProduct(item.id)" /></td>
                    <td class="px-4 py-3 align-top">
                      <div class="flex gap-3">
                        <img v-if="item.thumbnailUrl" :src="item.thumbnailUrl" class="h-12 w-12 rounded-xl border object-cover" />
                        <div>
                          <div class="font-semibold text-slate-900">{{ item.name }}</div>
                          <div class="text-xs text-slate-500">{{ item.baseSku }}</div>
                          <div class="mt-1 text-xs text-slate-500">{{ item.brand || '-' }} • {{ statusLabel(item.productType) }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3 align-top">
                      <div class="flex flex-wrap gap-1">
                        <span v-for="cat in item.categoryNames" :key="cat" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ cat }}</span>
                      </div>
                    </td>
                    <td class="px-4 py-3 align-top">
                      <div>{{ formatCurrency(item.minPrice) }}</div>
                      <div v-if="item.maxPrice && item.maxPrice !== item.minPrice" class="text-xs text-slate-500">đến {{ formatCurrency(item.maxPrice) }}</div>
                    </td>
                    <td class="px-4 py-3 align-top">
                      <div>{{ item.totalStock ?? 0 }}</div>
                      <div class="text-xs text-slate-500">{{ statusLabel(item.stockStatus) }}</div>
                    </td>
                    <td class="px-4 py-3 align-top">
                      <span class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ statusLabel(item.displayStatus) }}</span>
                    </td>
                    <td class="px-4 py-3 align-top">
                      <div class="flex justify-end gap-2">
                        <button @click="openProductDetail(item.id)" class="rounded-lg border p-2 hover:bg-slate-50"><Eye :size="16" /></button>
                        <button @click="openEditProduct(item.id)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="16" /></button>
                        <button @click="openQuickUpdateModal(item)" class="rounded-lg border px-3 text-xs hover:bg-slate-50">Quick</button>
                        <button @click="runHideIfOutOfStock(item.id)" class="rounded-lg border px-3 text-xs hover:bg-slate-50">Hide OOS</button>
                        <button @click="removeProduct(item.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="16" /></button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="productItems.length === 0">
                    <td colspan="7" class="px-4 py-8 text-center text-sm text-slate-500">Chưa có dữ liệu sản phẩm.</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="mt-4 flex items-center justify-between text-sm text-slate-600">
              <div>Trang {{ page + 1 }} / {{ Math.max(totalPages, 1) }} — {{ totalElements }} sản phẩm</div>
              <div class="flex items-center gap-2">
                <button :disabled="page <= 0" @click="page -= 1; loadProducts()" class="rounded-lg border px-3 py-2 disabled:opacity-50">Trước</button>
                <button :disabled="page + 1 >= totalPages" @click="page += 1; loadProducts()" class="rounded-lg border px-3 py-2 disabled:opacity-50">Sau</button>
              </div>
            </div>
          </template>

          <template v-else-if="activeTab === 'categories'">
            <div class="mt-4 flex flex-wrap items-center gap-3">
              <button @click="openCreateCategory" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">
                <span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm category</span>
              </button>
              <button @click="loadCategories" class="rounded-xl border px-4 py-2 text-sm font-medium">Làm mới danh mục</button>
            </div>
            <div class="mt-4 grid gap-4 md:grid-cols-2">
              <div v-for="category in categoryItems" :key="category.id" class="rounded-2xl border p-4">
                <div class="flex gap-3">
                  <img v-if="category.imageUrl" :src="category.imageUrl" class="h-16 w-16 rounded-xl border object-cover" />
                  <div class="flex-1">
                    <div class="flex items-start justify-between gap-2">
                      <div>
                        <h3 class="font-semibold text-slate-900">{{ category.name }}</h3>
                        <p class="mt-1 text-sm text-slate-500">{{ category.description || 'Chưa có mô tả' }}</p>
                      </div>
                      <span class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ category.productCount }} SP</span>
                    </div>
                    <div class="mt-3 flex flex-wrap gap-2">
                      <button @click="openEditCategory(category)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa</button>
                      <label class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
                        Ảnh
                        <input type="file" class="hidden" accept="image/*" @change="categoryImageUploadId = category.id; handleCategoryImageChange($event)" />
                      </label>
                      <button @click="previewCategoryProducts(category.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Xem sản phẩm</button>
                      <button @click="removeCategory(category.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa</button>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="!loadingCategories && categoryItems.length === 0" class="rounded-2xl border p-10 text-center text-sm text-slate-500">Chưa có category.</div>
            </div>

            <div v-if="categoryPreviewId" class="mt-6 rounded-2xl border bg-slate-50 p-4">
              <h3 class="font-semibold text-slate-900">Sản phẩm thuộc category #{{ categoryPreviewId }}</h3>
              <div class="mt-3 grid gap-2 md:grid-cols-2">
                <div v-for="item in categoryPreviewProducts" :key="item.id" class="rounded-xl border bg-white p-3 text-sm">
                  <div class="font-medium">{{ item.name }}</div>
                  <div class="text-xs text-slate-500">{{ item.baseSku }}</div>
                </div>
                <div v-if="categoryPreviewProducts.length === 0" class="rounded-xl border bg-white p-3 text-sm text-slate-500">Category chưa có sản phẩm.</div>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="mt-4 flex flex-wrap items-center gap-3">
              <button @click="openCreateTag" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white">
                <span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm tag</span>
              </button>
            </div>
            <div class="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              <div v-for="tag in tagItems" :key="tag.id" class="rounded-2xl border p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <h3 class="font-semibold text-slate-900">{{ tag.name }}</h3>
                    <p class="mt-1 text-sm text-slate-500">{{ tag.description || 'Chưa có mô tả' }}</p>
                  </div>
                  <span class="rounded-full bg-slate-100 p-2"><Tags :size="14" /></span>
                </div>
                <div class="mt-3 flex gap-2">
                  <button @click="openEditTag(tag)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa</button>
                  <button @click="removeTag(tag.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa</button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>

      <div class="space-y-6">
        <div class="rounded-2xl border bg-white p-4 shadow-sm">
          <div class="flex items-center gap-2 border-b pb-3 text-lg font-semibold text-slate-900">
            <FolderTree :size="18" /> Chi tiết sản phẩm
          </div>

          <div v-if="loadingDetail" class="flex items-center justify-center gap-2 py-10 text-sm text-slate-500">
            <LoaderCircle class="animate-spin" :size="18" /> Đang tải chi tiết...
          </div>

          <div v-else-if="selectedProduct" class="space-y-5 pt-4">
            <div class="flex gap-3">
              <img v-if="selectedProduct.thumbnailUrl" :src="selectedProduct.thumbnailUrl" class="h-20 w-20 rounded-2xl border object-cover" />
              <div>
                <h3 class="text-lg font-semibold text-slate-900">{{ selectedProduct.name }}</h3>
                <p class="text-sm text-slate-500">{{ selectedProduct.baseSku }}</p>
                <p class="mt-1 text-sm text-slate-500">{{ selectedProduct.brand || '-' }} • {{ statusLabel(selectedProduct.displayStatus) }}</p>
              </div>
            </div>

            <div class="grid grid-cols-3 gap-3 text-center text-sm">
              <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Ảnh</div><div class="mt-1 font-semibold">{{ detailStats.imageCount }}</div></div>
              <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Variants</div><div class="mt-1 font-semibold">{{ detailStats.variantCount }}</div></div>
              <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Tổng tồn</div><div class="mt-1 font-semibold">{{ detailStats.totalStock }}</div></div>
            </div>

            <div>
              <div class="mb-2 flex items-center justify-between">
                <h4 class="font-semibold text-slate-900">Category & Tags</h4>
              </div>
              <div class="flex flex-wrap gap-2">
                <span v-for="cat in selectedProduct.categories" :key="cat.id" class="rounded-full bg-slate-100 px-2 py-1 text-xs">{{ cat.name }}</span>
                <span v-for="tag in selectedProduct.tags" :key="tag.id" class="rounded-full bg-amber-100 px-2 py-1 text-xs text-amber-800">#{{ tag.name }}</span>
              </div>
            </div>

            <div>
              <div class="mb-2 flex items-center justify-between">
                <h4 class="font-semibold text-slate-900">Variants</h4>
                <button @click="openCreateVariant" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Thêm variant</button>
              </div>
              <div class="space-y-2">
                <div v-for="variant in selectedProduct.variants" :key="variant.id" class="rounded-xl border p-3 text-sm">
                  <div class="flex items-start justify-between gap-3">
                    <div>
                      <div class="font-medium">{{ variant.sku }}</div>
                      <div class="text-slate-500">{{ variant.size }} • {{ variant.color }}</div>
                      <div class="mt-1 text-slate-500">{{ formatCurrency(variant.salePrice || variant.price) }} • {{ variant.stockQuantity }} • {{ statusLabel(variant.stockStatus) }}</div>
                    </div>
                    <div class="flex gap-2">
                      <button @click="openEditVariant(variant)" class="rounded-lg border p-2 hover:bg-slate-50"><Pencil :size="14" /></button>
                      <button @click="removeVariant(variant.id)" class="rounded-lg border p-2 text-rose-600 hover:bg-rose-50"><Trash2 :size="14" /></button>
                    </div>
                  </div>
                </div>
                <div v-if="selectedProduct.variants.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có variant.</div>
              </div>
            </div>

            <div>
              <div class="mb-2 flex items-center justify-between">
                <h4 class="font-semibold text-slate-900">Ảnh sản phẩm</h4>
                <label class="inline-flex cursor-pointer items-center gap-2 rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">
                  <Upload :size="14" />
                  {{ uploadingProductImages ? 'Đang upload...' : 'Upload ảnh' }}
                  <input type="file" class="hidden" accept="image/*" multiple @change="handleProductImagesChange" />
                </label>
              </div>
              <div class="grid gap-3 sm:grid-cols-2">
                <div v-for="image in selectedProduct.images" :key="image.id" class="rounded-xl border p-3">
                  <img :src="image.imageUrl" class="h-32 w-full rounded-xl border object-cover" />
                  <div class="mt-3 flex items-center justify-between gap-2 text-xs">
                    <span class="rounded-full px-2 py-1" :class="image.avatar ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'">
                      {{ image.avatar ? 'Ảnh đại diện' : 'Ảnh phụ' }}
                    </span>
                    <div class="flex gap-2">
                      <button @click="setAvatar(image)" class="rounded-lg border px-2 py-1 hover:bg-slate-50">Đặt avatar</button>
                      <button @click="removeImage(image.id)" class="rounded-lg border px-2 py-1 text-rose-600 hover:bg-rose-50">Xóa</button>
                    </div>
                  </div>
                  <label class="mt-2 block text-xs text-slate-500">
                    Sort order
                    <input :value="image.sortOrder" type="number" class="mt-1 w-full rounded-lg border px-2 py-1 text-sm" @change="handleImageOrderChange(image, $event)" />
                  </label>
                </div>
                <div v-if="selectedProduct.images.length === 0" class="rounded-xl border p-6 text-sm text-slate-500">Chưa có ảnh sản phẩm.</div>
              </div>
            </div>

            <div>
              <div class="mb-2 flex items-center gap-2 font-semibold text-slate-900"><History :size="16" /> Lịch sử thay đổi</div>
              <div class="space-y-2">
                <div v-for="history in selectedProductHistory" :key="history.id" class="rounded-xl border p-3 text-sm">
                  <div class="font-medium">{{ history.action }}</div>
                  <div class="text-slate-500">{{ history.note || 'Không có ghi chú' }}</div>
                  <div class="mt-1 text-xs text-slate-400">{{ formatDate(history.changedAt) }} • user {{ history.changedBy || '-' }}</div>
                </div>
                <div v-if="selectedProductHistory.length === 0" class="rounded-xl border p-3 text-sm text-slate-500">Chưa có history.</div>
              </div>
            </div>

            <div class="flex flex-wrap gap-2">
              <button @click="openEditProduct(selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Sửa sản phẩm</button>
              <button @click="runHideIfOutOfStock(selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Hide if OOS</button>
              <button @click="removeProduct(selectedProduct.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa mềm</button>
              <button @click="removeProduct(selectedProduct.id, true)" class="rounded-lg border px-3 py-2 text-sm text-rose-700 hover:bg-rose-50">Xóa cứng</button>
            </div>
          </div>

          <div v-else class="py-10 text-center text-sm text-slate-500">Chọn một sản phẩm ở bên trái để xem chi tiết.</div>
        </div>

        <div class="rounded-2xl border bg-white p-4 shadow-sm">
          <div class="flex items-center gap-2 text-lg font-semibold text-slate-900"><Boxes :size="18" /> Gợi ý nhanh</div>
          <div class="mt-4 space-y-4">
            <div>
              <h4 class="mb-2 text-sm font-semibold text-slate-700">Top bán chạy</h4>
              <div class="space-y-2">
                <button v-for="item in topSelling.slice(0, 5)" :key="item.id" @click="openProductDetail(item.id)" class="block w-full rounded-xl border p-3 text-left text-sm hover:bg-slate-50">
                  <div class="font-medium">{{ item.name }}</div>
                  <div class="text-xs text-slate-500">{{ item.baseSku }} • Đã bán {{ item.soldQuantity || 0 }}</div>
                </button>
              </div>
            </div>
            <div>
              <h4 class="mb-2 text-sm font-semibold text-slate-700">Sản phẩm mới</h4>
              <div class="space-y-2">
                <button v-for="item in newestProducts.slice(0, 5)" :key="item.id" @click="openProductDetail(item.id)" class="block w-full rounded-xl border p-3 text-left text-sm hover:bg-slate-50">
                  <div class="font-medium">{{ item.name }}</div>
                  <div class="text-xs text-slate-500">{{ formatDate(item.createdAt) }}</div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="productModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="max-h-[95vh] w-full max-w-3xl overflow-auto rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ editingProductId ? 'Sửa sản phẩm' : 'Thêm sản phẩm' }}</h3>
          <button @click="productModalOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 grid gap-4 md:grid-cols-2">
          <label class="space-y-2"><span class="text-sm">Tên sản phẩm</span><input v-model="productForm.name" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Base SKU</span><input v-model="productForm.baseSku" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Brand</span><input v-model="productForm.brand" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Season</span><input v-model="productForm.season" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Loại sản phẩm</span><select v-model="productForm.productType" class="w-full rounded-xl border px-3 py-2"><option v-for="item in PRODUCT_TYPES" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
          <label class="space-y-2"><span class="text-sm">Đối tượng</span><select v-model="productForm.targetGender" class="w-full rounded-xl border px-3 py-2"><option v-for="item in GENDERS" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
          <label class="space-y-2"><span class="text-sm">Chất liệu</span><input v-model="productForm.material" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Năm ra mắt</span><input v-model.number="productForm.releaseYear" type="number" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2 md:col-span-2"><span class="text-sm">Mô tả</span><textarea v-model="productForm.description" rows="3" class="w-full rounded-xl border px-3 py-2"></textarea></label>
          <label class="space-y-2"><span class="text-sm">Trạng thái hiển thị</span><select v-model="productForm.displayStatus" class="w-full rounded-xl border px-3 py-2"><option v-for="item in DISPLAY_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
          <label class="space-y-2"><span class="text-sm">Categories</span><select v-model="productForm.categoryIds" multiple class="min-h-32 w-full rounded-xl border px-3 py-2"><option v-for="item in categoryItems" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
          <label class="space-y-2 md:col-span-2"><span class="text-sm">Tags</span><select v-model="productForm.tagIds" multiple class="min-h-28 w-full rounded-xl border px-3 py-2"><option v-for="item in tagItems" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="productModalOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitProduct" :disabled="savingProduct" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">
            <span class="inline-flex items-center gap-2">
              <LoaderCircle v-if="savingProduct" class="animate-spin" :size="16" />
              {{ editingProductId ? 'Lưu thay đổi' : 'Tạo sản phẩm' }}
            </span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="categoryModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ editingCategoryId ? 'Sửa category' : 'Thêm category' }}</h3>
          <button @click="categoryModalOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 grid gap-4">
          <label class="space-y-2"><span class="text-sm">Tên category</span><input v-model="categoryForm.name" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Mô tả</span><textarea v-model="categoryForm.description" rows="4" class="w-full rounded-xl border px-3 py-2"></textarea></label>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="categoryModalOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitCategory" :disabled="savingCategory" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">{{ editingCategoryId ? 'Lưu thay đổi' : 'Tạo category' }}</button>
        </div>
      </div>
    </div>

    <div v-if="tagModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ editingTagId ? 'Sửa tag' : 'Thêm tag' }}</h3>
          <button @click="tagModalOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 grid gap-4">
          <label class="space-y-2"><span class="text-sm">Tên tag</span><input v-model="tagForm.name" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Mô tả</span><textarea v-model="tagForm.description" rows="4" class="w-full rounded-xl border px-3 py-2"></textarea></label>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="tagModalOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitTag" :disabled="savingTag" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50">{{ editingTagId ? 'Lưu thay đổi' : 'Tạo tag' }}</button>
        </div>
      </div>
    </div>

    <div v-if="variantModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ editingVariantId ? 'Sửa variant' : 'Thêm variant' }}</h3>
          <button @click="variantModalOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 grid gap-4 md:grid-cols-2">
          <label class="space-y-2"><span class="text-sm">SKU variant</span><input v-model="variantForm.sku" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Size</span><input v-model="variantForm.size" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Màu</span><input v-model="variantForm.color" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Giá bán</span><input v-model.number="variantForm.price" type="number" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Giá khuyến mãi</span><input v-model.number="variantForm.salePrice" type="number" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2"><span class="text-sm">Tồn kho</span><input v-model.number="variantForm.stockQuantity" type="number" class="w-full rounded-xl border px-3 py-2" /></label>
          <label class="space-y-2 md:col-span-2"><span class="text-sm">Trạng thái tồn kho</span><select v-model="variantForm.stockStatus" class="w-full rounded-xl border px-3 py-2"><option v-for="item in STOCK_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option></select></label>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="variantModalOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitVariant" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">{{ editingVariantId ? 'Lưu thay đổi' : 'Tạo variant' }}</button>
        </div>
      </div>
    </div>

    <div v-if="quickUpdateOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">Quick update sản phẩm</h3>
          <button @click="quickUpdateOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 grid gap-4">
          <input v-model="quickUpdateForm.name" class="rounded-xl border px-3 py-2" placeholder="Tên sản phẩm" />
          <input v-model="quickUpdateForm.brand" class="rounded-xl border px-3 py-2" placeholder="Brand" />
          <input v-model="quickUpdateForm.season" class="rounded-xl border px-3 py-2" placeholder="Season" />
          <input v-model="quickUpdateForm.material" class="rounded-xl border px-3 py-2" placeholder="Material" />
          <select v-model="quickUpdateForm.displayStatus" class="rounded-xl border px-3 py-2">
            <option v-for="item in DISPLAY_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="quickUpdateOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitQuickUpdate" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Lưu</button>
        </div>
      </div>
    </div>

    <div v-if="batchModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="max-h-[95vh] w-full max-w-4xl overflow-auto rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">Batch update sản phẩm</h3>
          <button @click="batchModalOpen = false" class="rounded-lg border p-2"><X :size="16" /></button>
        </div>
        <div class="mt-4 space-y-4">
          <div v-for="row in batchUpdateRows" :key="row.id" class="grid gap-3 rounded-xl border p-4 md:grid-cols-4">
            <div class="text-sm font-medium">ID #{{ row.id }}</div>
            <input v-model="row.brand" class="rounded-xl border px-3 py-2 text-sm" placeholder="Brand" />
            <input v-model="row.season" class="rounded-xl border px-3 py-2 text-sm" placeholder="Season" />
            <input v-model="row.material" class="rounded-xl border px-3 py-2 text-sm" placeholder="Material" />
            <select v-model="row.displayStatus" class="rounded-xl border px-3 py-2 text-sm md:col-span-4">
              <option v-for="item in DISPLAY_STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button @click="batchModalOpen = false" class="rounded-xl border px-4 py-2 text-sm">Hủy</button>
          <button @click="submitBatchUpdate" class="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Lưu batch update</button>
        </div>
      </div>
    </div>
  </section>
</template>
