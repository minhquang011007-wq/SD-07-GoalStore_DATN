<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { AlertCircle, Boxes, Plus, RefreshCcw, Tags, X } from "lucide-vue-next"
import ProductFilters from "@/modules/product/components/ProductFilters.vue"
import ProductTable from "@/modules/product/components/ProductTable.vue"
import ProductModal from "@/modules/product/components/ProductModal.vue"
import ProductDetail from "@/modules/product/components/ProductDetail.vue"
import TagManager from "@/modules/product/components/TagManager.vue"
import TagModal from "@/modules/product/components/TagModal.vue"
import {
  createCategory,
  createProduct,
  createVariant,
  deleteCategory,
  deleteProduct,
  deleteProductImage,
  deleteVariant,
  fetchCategories,
  fetchProductDetail,
  fetchTags,
  hideProductIfOutOfStock,
  searchProducts,
  updateCategory,
  updateProduct,
  updateProductImage,
  updateVariant,
  uploadCategoryImage,
  uploadProductImages,
} from "@/modules/product/services/product.api"
import type {
  CategoryRequest,
  CategoryResponse,
  ProductDetailResponse,
  ProductDisplayStatus,
  ProductImageResponse,
  ProductRequest,
  ProductSummaryResponse,
  ProductType,
  ProductVariantRequest,
  ProductVariantResponse,
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
]

const activeTab = ref<"products" | "categories" | "tags">("products")
const loadingProducts = ref(false)
const loadingCategories = ref(false)
const loadingTags = ref(false)
const loadingDetail = ref(false)
const savingProduct = ref(false)
const savingCategory = ref(false)
const uploadingProductImages = ref(false)
const selectedProductId = ref<number | null>(null)
const selectedProduct = ref<ProductDetailResponse | null>(null)
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
const variantModalOpen = ref(false)

const editingProductId = ref<number | null>(null)
const editingCategoryId = ref<number | null>(null)
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

const variantForm = reactive<ProductVariantRequest>({
  sku: "",
  size: "",
  color: "",
  price: null,
  salePrice: null,
  stockQuantity: 0,
  stockStatus: "CON_HANG",
})

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

function statusLabel(value?: string | null) {
  const labels: Record<string, string> = {
    HIENTHI: "Hiển thị",
    AN: "Ẩn",
    NGUNG_BAN: "Ngừng bán",
    CON_HANG: "Còn hàng",
    HET_HANG: "Hết hàng",
    PRE_ORDER: "Pre-order",
    AO: "Áo",
    QUAN: "Quần",
    VO: "Vớ",
    GANG_TAY: "Găng tay",
    KHAC: "Khác",
  }
  return labels[value || ""] || value || "-"
}

function toggleSelectedProduct(id: number) {
  selectedProductIds.value = selectedProductIds.value.includes(id)
    ? selectedProductIds.value.filter((item) => item !== id)
    : [...selectedProductIds.value, id]
}

function toggleAllProducts() {
  selectedProductIds.value = selectedProductIds.value.length === productItems.value.length ? [] : productItems.value.map((item) => item.id)
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

async function loadCategories() {
  loadingCategories.value = true
  try {
    categoryItems.value = await fetchCategories()
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
      displayStatus: productFilters.displayStatus || undefined,
      inStock: productFilters.inStock === "" ? null : productFilters.inStock === "true",
      sort: productFilters.sort,
    })
    productItems.value = response.content
    totalElements.value = response.totalElements
    totalPages.value = response.totalPages
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
    selectedProduct.value = await fetchProductDetail(id)
  } catch (error) {
    notifyError(error)
  } finally {
    loadingDetail.value = false
  }
}

async function refreshAll() {
  resetMessage()
  await Promise.all([loadCategories(), loadTags(), loadProducts()])
  if (selectedProductId.value) await openProductDetail(selectedProductId.value)
}

function applyFilters() {
  page.value = 0
  loadProducts()
}

function resetFilters() {
  productFilters.keyword = ""
  productFilters.categoryIds = []
  productFilters.tagIds = []
  productFilters.displayStatus = ""
  productFilters.productType = ""
  productFilters.targetGender = ""
  productFilters.stockStatus = ""
  productFilters.brand = ""
  productFilters.material = ""
  productFilters.inStock = ""
  productFilters.hideOutOfStock = false
  productFilters.sort = "newest"
  page.value = 0
  loadProducts()
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
  if (!window.confirm("Xóa sản phẩm này?")) return
  try {
    await deleteProduct(id)
    notifySuccess("Đã xóa sản phẩm.")
    if (selectedProductId.value === id) {
      selectedProductId.value = null
      selectedProduct.value = null
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

function handleImageOrderChange(image: ProductImageResponse, event: Event) {
  if (!selectedProductId.value) return
  const target = event.target as HTMLInputElement
  updateProductImage(image.id, { avatar: image.avatar, sortOrder: Number(target.value) })
    .then(() => {
      notifySuccess("Đã cập nhật thứ tự ảnh.")
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
    <div class="flex flex-col gap-4 rounded-2xl border bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between">
      <div>
        <p class="text-sm text-slate-500">Inventory / Product & Category</p>
        <h1 class="mt-1 text-2xl font-bold text-slate-900">Quản lý Catalog Goal Store</h1>
        <p class="mt-2 max-w-3xl text-sm text-slate-600">Đã tách module product thành nhiều component để nhóm dễ code Git hơn.</p>
      </div>
      <div class="grid grid-cols-2 gap-3 lg:grid-cols-4">
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Tổng sản phẩm</div><div class="mt-1 text-xl font-semibold">{{ totalElements }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Categories</div><div class="mt-1 text-xl font-semibold">{{ categoryItems.length }}</div></div>
        <div class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">Tags</div><div class="mt-1 text-xl font-semibold">{{ tagItems.length }}</div></div>
        <button @click="refreshAll" class="flex items-center justify-center gap-2 rounded-xl border px-4 py-3 text-sm font-medium hover:bg-slate-50"><RefreshCcw :size="16" /> Làm mới</button>
      </div>
    </div>

    <div v-if="message" class="flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700"><Boxes :size="16" /> {{ message }}</div>
    <div v-if="errorMessage" class="flex items-center gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700"><AlertCircle :size="16" /> {{ errorMessage }}</div>

    <div class="grid gap-6 xl:grid-cols-[1.6fr_1fr]">
      <div class="space-y-6">
        <div class="rounded-2xl border bg-white p-4 shadow-sm">
          <div class="flex flex-wrap gap-2 border-b pb-4">
            <button @click="activeTab = 'products'" :class="activeTab === 'products' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Sản phẩm</button>
            <button @click="activeTab = 'categories'" :class="activeTab === 'categories' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Category</button>
            <button @click="activeTab = 'tags'" :class="activeTab === 'tags' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Tags</button>
          </div>

          <template v-if="activeTab === 'products'">
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
              @hide-oos="runHideIfOutOfStock"
              @delete="removeProduct"
              @prev-page="page -= 1; loadProducts()"
              @next-page="page += 1; loadProducts()"
            />
          </template>

          <template v-else-if="activeTab === 'categories'">
            <div class="mt-4 flex flex-wrap items-center gap-3">
              <button @click="openCreateCategory" class="rounded-xl bg-emerald-600 px-4 py-2 text-sm font-medium text-white"><span class="inline-flex items-center gap-2"><Plus :size="16" /> Thêm category</span></button>
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
                      <label class="rounded-lg border px-3 py-2 text-sm hover:bg-slate-50">Ảnh<input type="file" class="hidden" accept="image/*" @change="categoryImageUploadId = category.id; handleCategoryImageChange($event)" /></label>
                      <button @click="removeCategory(category.id)" class="rounded-lg border px-3 py-2 text-sm text-rose-600 hover:bg-rose-50">Xóa</button>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="!loadingCategories && categoryItems.length === 0" class="rounded-2xl border p-10 text-center text-sm text-slate-500">Chưa có category.</div>
            </div>
          </template>

          <template v-else>
            <div class="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              <div v-for="tag in tagItems" :key="tag.id" class="rounded-2xl border p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <h3 class="font-semibold text-slate-900">{{ tag.name }}</h3>
                    <p class="mt-1 text-sm text-slate-500">{{ tag.description || 'Chưa có mô tả' }}</p>
                  </div>
                  <span class="rounded-full bg-slate-100 p-2"><Tags :size="14" /></span>
                </div>
              </div>
              <div v-if="!loadingTags && tagItems.length === 0" class="rounded-2xl border p-10 text-center text-sm text-slate-500">Chưa có tag.</div>
            </div>
          </template>
        </div>
      </div>

      <ProductDetail
        :selected-product="selectedProduct"
        :loading-detail="loadingDetail"
        :uploading-product-images="uploadingProductImages"
        :detail-stats="detailStats"
        :format-currency="formatCurrency"
        :status-label="statusLabel"
        @edit-product="openEditProduct"
        @hide-oos="runHideIfOutOfStock"
        @delete-soft="removeProduct"
        @create-variant="openCreateVariant"
        @edit-variant="openEditVariant"
        @remove-variant="removeVariant"
        @upload-images="handleProductImagesChange"
        @set-avatar="setAvatar"
        @remove-image="removeImage"
        @image-order-change="(payload) => handleImageOrderChange(payload.image, payload.event)"
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
  </section>
</template>
