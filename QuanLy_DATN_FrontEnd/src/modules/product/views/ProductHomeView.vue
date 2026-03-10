<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue"
import { AlertCircle, Boxes, FolderTree, RefreshCcw } from "lucide-vue-next"
import CategoryManager from "@/modules/product/components/CategoryManager.vue"
import CategoryModal from "@/modules/product/components/CategoryModal.vue"
import ProductDetail from "@/modules/product/components/ProductDetail.vue"
import ProductFilters from "@/modules/product/components/ProductFilters.vue"
import ProductModal from "@/modules/product/components/ProductModal.vue"
import ProductTable from "@/modules/product/components/ProductTable.vue"
import TagManager from "@/modules/product/components/TagManager.vue"
import TagModal from "@/modules/product/components/TagModal.vue"
import {
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
  fetchProductDetail,
  fetchTags,
  hideProductIfOutOfStock,
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
  ProductDetailResponse,
  ProductDisplayStatus,
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
const sortOptions = [
  { label: "Mới nhất", value: "newest" },
  { label: "Cũ nhất", value: "oldest" },
  { label: "Tên A-Z", value: "name_asc" },
  { label: "Tên Z-A", value: "name_desc" },
  { label: "Giá tăng dần", value: "price_asc" },
  { label: "Giá giảm dần", value: "price_desc" },
  { label: "Bán chạy", value: "best_selling" },
]

const activeTab = ref<"products" | "categories" | "tags">("products")
const loadingProducts = ref(false)
const loadingCategories = ref(false)
const loadingDetail = ref(false)
const savingProduct = ref(false)
const savingCategory = ref(false)
const savingTag = ref(false)
const uploadingProductImages = ref(false)
const message = ref("")
const errorMessage = ref("")

const productPage = ref(0)
const productSize = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)
const productItems = ref<ProductSummaryResponse[]>([])
const categoryItems = ref<CategoryResponse[]>([])
const tagItems = ref<TagResponse[]>([])
const selectedProduct = ref<ProductDetailResponse | null>(null)
const selectedProductId = ref<number | null>(null)

const productFilters = reactive({ keyword: "", categoryId: "", displayStatus: "", inStock: "", sort: "newest" })
const productFormVisible = ref(false)
const categoryFormVisible = ref(false)
const tagFormVisible = ref(false)
const editingProductId = ref<number | null>(null)
const editingCategoryId = ref<number | null>(null)
const editingTagId = ref<number | null>(null)
const editingVariantId = ref<number | null>(null)

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
const categoryForm = reactive<CategoryRequest>({ name: "", description: "" })
const tagForm = reactive<TagRequest>({ name: "", description: "" })
const variantForm = reactive<ProductVariantRequest>({ sku: "", size: "", color: "", price: null, salePrice: null, stockQuantity: 0, stockStatus: "CON_HANG" })

const stats = computed(() => [
  { label: "Tổng sản phẩm", value: totalElements.value },
  { label: "Categories", value: categoryItems.value.length },
  { label: "Tags", value: tagItems.value.length },
])

function setMessage(text = "") { message.value = text; if (text) errorMessage.value = "" }
function setError(error: unknown) {
  const res = (error as { response?: { data?: { message?: string; error?: string } } })?.response?.data
  errorMessage.value = res?.message || res?.error || "Có lỗi xảy ra, kiểm tra backend rồi thử lại"
  message.value = ""
}
function normalizeAsset(url?: string | null) {
  if (!url) return ""
  if (url.startsWith("http://") || url.startsWith("https://")) return url
  const baseUrl = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/$/, "")
  return `${baseUrl}${url.startsWith("/") ? "" : "/"}${url}`
}
function formatCurrency(value?: number | null) {
  if (value === null || value === undefined) return "-"
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value)
}
function displayStatusLabel(status?: string | null) { return DISPLAY_STATUS_OPTIONS.find((i) => i.value === status)?.label || status || "-" }
function stockStatusLabel(status?: string | null) { return STOCK_STATUS_OPTIONS.find((i) => i.value === status)?.label || status || "-" }

function resetProductForm() {
  Object.assign(productForm, { name: "", baseSku: "", brand: "", season: "", productType: "AO", targetGender: "NAM", material: "", description: "", releaseYear: new Date().getFullYear(), displayStatus: "HIENTHI", categoryIds: [], tagIds: [] })
  editingProductId.value = null
}
function resetCategoryForm() { Object.assign(categoryForm, { name: "", description: "" }); editingCategoryId.value = null }
function resetTagForm() { Object.assign(tagForm, { name: "", description: "" }); editingTagId.value = null }
function resetVariantForm() { Object.assign(variantForm, { sku: "", size: "", color: "", price: null, salePrice: null, stockQuantity: 0, stockStatus: "CON_HANG" }); editingVariantId.value = null }

async function loadCategories() { loadingCategories.value = true; try { categoryItems.value = await fetchCategories() } catch (e) { setError(e) } finally { loadingCategories.value = false } }
async function loadTags() { try { tagItems.value = await fetchTags() } catch { tagItems.value = [] } }
async function loadProducts(page = productPage.value) {
  loadingProducts.value = true
  try {
    const result = await searchProducts({
      page,
      size: productSize.value,
      keyword: productFilters.keyword.trim() || undefined,
      categoryIds: productFilters.categoryId ? [Number(productFilters.categoryId)] : undefined,
      displayStatus: productFilters.displayStatus || undefined,
      inStock: productFilters.inStock === "" ? null : productFilters.inStock === "true",
      sort: productFilters.sort,
    })
    productItems.value = result.content
    productPage.value = result.page
    totalPages.value = result.totalPages
    totalElements.value = result.totalElements
  } catch (e) { setError(e) } finally { loadingProducts.value = false }
}
async function openProductDetail(id: number) { selectedProductId.value = id; loadingDetail.value = true; try { selectedProduct.value = await fetchProductDetail(id); resetVariantForm() } catch (e) { setError(e) } finally { loadingDetail.value = false } }
async function refreshAll() { await Promise.all([loadCategories(), loadTags(), loadProducts()]); if (selectedProductId.value) await openProductDetail(selectedProductId.value) }

function openCreateProduct() { resetProductForm(); productFormVisible.value = true }
function openEditProduct(item: ProductSummaryResponse) {
  const categoryIds = categoryItems.value.filter((c) => item.categoryNames.includes(c.name)).map((c) => c.id)
  Object.assign(productForm, { name: item.name, baseSku: item.baseSku, brand: item.brand || "", season: item.season || "", productType: item.productType, targetGender: item.targetGender, material: item.material || "", description: selectedProduct.value?.id === item.id ? selectedProduct.value.description || "" : "", releaseYear: selectedProduct.value?.id === item.id ? selectedProduct.value.releaseYear || new Date().getFullYear() : new Date().getFullYear(), displayStatus: item.displayStatus, categoryIds, tagIds: selectedProduct.value?.id === item.id ? selectedProduct.value.tags.map((tag) => tag.id) : [] })
  editingProductId.value = item.id
  productFormVisible.value = true
}
async function submitProduct() {
  savingProduct.value = true
  try {
    const payload = { ...productForm, releaseYear: productForm.releaseYear ? Number(productForm.releaseYear) : null, categoryIds: [...productForm.categoryIds], tagIds: [...productForm.tagIds] }
    if (editingProductId.value) { await updateProduct(editingProductId.value, payload); setMessage("Cập nhật sản phẩm thành công") }
    else { await createProduct(payload); setMessage("Tạo sản phẩm thành công") }
    productFormVisible.value = false
    await refreshAll()
  } catch (e) { setError(e) } finally { savingProduct.value = false }
}
async function handleDeleteProduct(id: number) { if (!window.confirm("Xóa mềm sản phẩm này?")) return; try { await deleteProduct(id); if (selectedProductId.value === id) selectedProduct.value = null; setMessage("Đã xóa sản phẩm"); await loadProducts() } catch (e) { setError(e) } }
async function handleHideOOS(id: number) { try { await hideProductIfOutOfStock(id); setMessage("Đã cập nhật trạng thái sản phẩm"); await loadProducts(); if (selectedProductId.value === id) await openProductDetail(id) } catch (e) { setError(e) } }

function openCreateCategory() { resetCategoryForm(); categoryFormVisible.value = true }
function openEditCategory(item: CategoryResponse) { Object.assign(categoryForm, { name: item.name, description: item.description || "" }); editingCategoryId.value = item.id; categoryFormVisible.value = true }
async function submitCategory() { savingCategory.value = true; try { if (editingCategoryId.value) { await updateCategory(editingCategoryId.value, categoryForm); setMessage("Đã cập nhật category") } else { await createCategory(categoryForm); setMessage("Đã tạo category") } categoryFormVisible.value = false; await loadCategories() } catch (e) { setError(e) } finally { savingCategory.value = false } }
async function handleDeleteCategory(id: number) { if (!window.confirm("Xóa category này?")) return; try { await deleteCategory(id); setMessage("Đã xóa category"); await loadCategories() } catch (e) { setError(e) } }
async function onCategoryImageSelected(payload: { event: Event; id: number }) { const input = payload.event.target as HTMLInputElement; const file = input.files?.[0]; if (!file) return; try { await uploadCategoryImage(payload.id, file); setMessage("Đã upload ảnh category"); await loadCategories() } catch (e) { setError(e) } finally { input.value = "" } }

function openCreateTag() { resetTagForm(); tagFormVisible.value = true }
function openEditTag(item: TagResponse) { Object.assign(tagForm, { name: item.name, description: item.description || "" }); editingTagId.value = item.id; tagFormVisible.value = true }
async function submitTag() { savingTag.value = true; try { if (editingTagId.value) { await updateTag(editingTagId.value, tagForm); setMessage("Đã cập nhật tag") } else { await createTag(tagForm); setMessage("Đã tạo tag") } tagFormVisible.value = false; await loadTags() } catch (e) { setError(e) } finally { savingTag.value = false } }
async function handleDeleteTag(id: number) { if (!window.confirm("Xóa tag này?")) return; try { await deleteTag(id); setMessage("Đã xóa tag"); await loadTags() } catch (e) { setError(e) } }

function editVariant(variant: ProductVariantResponse) { Object.assign(variantForm, { sku: variant.sku, size: variant.size, color: variant.color, price: variant.price, salePrice: variant.salePrice || null, stockQuantity: variant.stockQuantity, stockStatus: variant.stockStatus }); editingVariantId.value = variant.id }
async function submitVariant() { if (!selectedProductId.value) return; try { if (editingVariantId.value) { await updateVariant(editingVariantId.value, variantForm); setMessage("Đã cập nhật variant") } else { await createVariant(selectedProductId.value, variantForm); setMessage("Đã thêm variant") } await openProductDetail(selectedProductId.value); await loadProducts(); resetVariantForm() } catch (e) { setError(e) } }
async function handleDeleteVariant(id: number) { if (!selectedProductId.value || !window.confirm("Xóa variant này?")) return; try { await deleteVariant(id); setMessage("Đã xóa variant"); await openProductDetail(selectedProductId.value); await loadProducts() } catch (e) { setError(e) } }
async function onProductImagesSelected(event: Event) { const input = event.target as HTMLInputElement; const files = input.files ? [...input.files] : []; if (!selectedProductId.value || !files.length) return; uploadingProductImages.value = true; try { await uploadProductImages(selectedProductId.value, files, true); setMessage("Đã upload ảnh sản phẩm"); await openProductDetail(selectedProductId.value) } catch (e) { setError(e) } finally { uploadingProductImages.value = false; input.value = "" } }
async function handleSetAvatar(imageId: number) { if (!selectedProductId.value || !selectedProduct.value) return; const image = selectedProduct.value.images.find((i) => i.id === imageId); if (!image) return; try { await updateProductImage(imageId, { avatar: true, sortOrder: image.sortOrder }); setMessage("Đã đổi ảnh đại diện"); await openProductDetail(selectedProductId.value) } catch (e) { setError(e) } }
async function handleDeleteImage(imageId: number) { if (!selectedProductId.value || !window.confirm("Xóa ảnh này?")) return; try { await deleteProductImage(imageId); setMessage("Đã xóa ảnh sản phẩm"); await openProductDetail(selectedProductId.value) } catch (e) { setError(e) } }

function resetFilters() { Object.assign(productFilters, { keyword: "", categoryId: "", displayStatus: "", inStock: "", sort: "newest" }); loadProducts(0) }

onMounted(refreshAll)
</script>

<template>
  <section class="space-y-6">
    <div class="flex flex-col gap-4 rounded-2xl border bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between">
      <div>
        <p class="text-sm text-slate-500">Inventory / Product & Category</p>
        <h1 class="mt-1 text-2xl font-bold text-slate-900">Quản lý Catalog Goal Store</h1>
        <p class="mt-2 text-sm text-slate-600">Tách gọn module product, giữ đủ chức năng backend: product, category, tag, variant, image.</p>
      </div>
      <div class="grid grid-cols-2 gap-3 lg:grid-cols-4">
        <div v-for="item in stats" :key="item.label" class="rounded-xl bg-slate-50 p-3"><div class="text-xs text-slate-500">{{ item.label }}</div><div class="mt-1 text-xl font-semibold">{{ item.value }}</div></div>
        <button @click="refreshAll" class="flex items-center justify-center gap-2 rounded-xl border px-4 py-3 text-sm font-medium hover:bg-slate-50"><RefreshCcw :size="16" /> Làm mới</button>
      </div>
    </div>

    <div v-if="message" class="flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700"><Boxes :size="16" /> {{ message }}</div>
    <div v-if="errorMessage" class="flex items-center gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700"><AlertCircle :size="16" /> {{ errorMessage }}</div>

    <div class="flex flex-wrap gap-2">
      <button @click="activeTab = 'products'" :class="activeTab === 'products' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Sản phẩm</button>
      <button @click="activeTab = 'categories'" :class="activeTab === 'categories' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Category</button>
      <button @click="activeTab = 'tags'" :class="activeTab === 'tags' ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700'" class="rounded-lg px-4 py-2 text-sm font-medium">Tag</button>
    </div>

    <template v-if="activeTab === 'products'">
      <ProductFilters :filters="productFilters" :category-items="categoryItems" :display-status-options="DISPLAY_STATUS_OPTIONS" :sort-options="sortOptions" @apply="loadProducts(0)" @reset="resetFilters" @create="openCreateProduct" @go-categories="activeTab = 'categories'" @go-tags="activeTab = 'tags'" />
      <div class="grid gap-6 xl:grid-cols-[1.25fr_0.95fr]">
        <ProductTable :loading="loadingProducts" :items="productItems" :page="productPage" :total-pages="totalPages" :total-elements="totalElements" :normalize-asset="normalizeAsset" :format-currency="formatCurrency" :display-status-label="displayStatusLabel" :stock-status-label="stockStatusLabel" @detail="openProductDetail" @edit="openEditProduct" @delete="handleDeleteProduct" @hide-oos="handleHideOOS" @page="loadProducts" />
        <div>
          <div class="mb-3 flex items-center gap-2 text-lg font-semibold text-slate-900"><FolderTree :size="18" /> Chi tiết sản phẩm</div>
          <ProductDetail :loading="loadingDetail" :selected-product="selectedProduct" :uploading-product-images="uploadingProductImages" :variant-form="variantForm" :editing-variant-id="editingVariantId" :normalize-asset="normalizeAsset" :format-currency="formatCurrency" :display-status-label="displayStatusLabel" :stock-status-label="stockStatusLabel" :stock-status-options="STOCK_STATUS_OPTIONS" @edit-product="(id) => openEditProduct(productItems.find((item) => item.id === id)!)" @hide-oos="handleHideOOS" @delete-product="handleDeleteProduct" @upload-images="onProductImagesSelected" @set-avatar="handleSetAvatar" @delete-image="handleDeleteImage" @edit-variant="editVariant" @delete-variant="handleDeleteVariant" @submit-variant="submitVariant" @reset-variant="resetVariantForm" />
        </div>
      </div>
    </template>

    <CategoryManager v-else-if="activeTab === 'categories'" :loading="loadingCategories" :items="categoryItems" :normalize-asset="normalizeAsset" @create="openCreateCategory" @edit="openEditCategory" @delete="handleDeleteCategory" @upload="onCategoryImageSelected" />
    <TagManager v-else :items="tagItems" @create="openCreateTag" @edit="openEditTag" @delete="handleDeleteTag" />

    <ProductModal :open="productFormVisible" :saving="savingProduct" :editing-product-id="editingProductId" :form="productForm" :categories="categoryItems" :tags="tagItems" :product-types="PRODUCT_TYPES" :genders="GENDERS" :display-status-options="DISPLAY_STATUS_OPTIONS" @close="productFormVisible = false" @submit="submitProduct" />
    <CategoryModal :open="categoryFormVisible" :saving="savingCategory" :editing-id="editingCategoryId" :form="categoryForm" @close="categoryFormVisible = false" @submit="submitCategory" />
    <TagModal :open="tagFormVisible" :saving="savingTag" :editing-id="editingTagId" :form="tagForm" @close="tagFormVisible = false" @submit="submitTag" />
  </section>
</template>
