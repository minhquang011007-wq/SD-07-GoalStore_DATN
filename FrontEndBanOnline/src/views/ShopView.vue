<template>
  <div class="legacy-page">
    <div id="preloder"><div class="loader"></div></div>

    <section class="breadcrumb-option">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="breadcrumb__text">
              <h4>Sản phẩm</h4>
              <div class="breadcrumb__links">
                <RouterLink to="/">Trang chủ</RouterLink>
                <span>Sản phẩm</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="shop spad">
      <div class="container">
        <div class="row">
          <div class="col-lg-3">
            <div class="shop__sidebar">
              <div class="shop__sidebar__actions">
                <button type="button" class="sidebar-action-btn" @click="clearAllFilters">
                  Xóa toàn bộ bộ lọc
                </button>
              </div>

              <div class="shop__sidebar__accordion custom-accordion">
                <div class="card">
                  <div class="card-heading">
                    <button type="button" class="accordion-toggle" @click="togglePanel('category')">
                      <span>Danh mục</span>
                      <span class="accordion-icon" :class="{ open: openPanels.category }">⌄</span>
                    </button>
                  </div>
                  <div v-show="openPanels.category" class="card-body card-body--panel">
                    <div class="shop__sidebar__categories">
                      <label class="filter-check filter-check--all">
                        <input
                          type="checkbox"
                          :checked="!selectedCategoryIds.length"
                          @change="clearCategoryFilter"
                        />
                        <span>Tất cả ({{ totalCategoryCount }})</span>
                      </label>

                      <label
                        v-for="category in categoryOptions"
                        :key="category.id || category.name"
                        class="filter-check"
                      >
                        <input
                          type="checkbox"
                          :checked="isCategorySelected(category.id)"
                          @change="toggleCategory(category.id)"
                        />
                        <span>{{ category.name }} ({{ category.productCount || 0 }})</span>
                      </label>

                      <p v-if="!categoryOptions.length" class="filter-empty">Chưa có danh mục.</p>
                    </div>
                  </div>
                </div>

                <div class="card">
                  <div class="card-heading">
                    <button type="button" class="accordion-toggle" @click="togglePanel('brand')">
                      <span>Thương hiệu</span>
                      <span class="accordion-icon" :class="{ open: openPanels.brand }">⌄</span>
                    </button>
                  </div>
                  <div v-show="openPanels.brand" class="card-body card-body--panel">
                    <div class="shop__sidebar__categories">
                      <label class="filter-check filter-check--all">
                        <input
                          type="checkbox"
                          :checked="!selectedBrand"
                          @change="pickBrand('')"
                        />
                        <span>Tất cả ({{ totalBrandCount }})</span>
                      </label>

                      <label
                        v-for="brand in brandOptions"
                        :key="brand.name"
                        class="filter-check"
                      >
                        <input
                          type="checkbox"
                          :checked="selectedBrand === brand.name"
                          @change="pickBrand(selectedBrand === brand.name ? '' : brand.name)"
                        />
                        <span>{{ brand.name }} ({{ brand.count }})</span>
                      </label>

                      <p v-if="!brandOptions.length" class="filter-empty">Chưa có thương hiệu.</p>
                    </div>
                  </div>
                </div>

                <div class="card">
                  <div class="card-heading">
                    <button type="button" class="accordion-toggle" @click="togglePanel('price')">
                      <span>Khoảng giá</span>
                      <span class="accordion-icon" :class="{ open: openPanels.price }">⌄</span>
                    </button>
                  </div>
                  <div v-show="openPanels.price" class="card-body card-body--panel">
                    <div class="shop__sidebar__categories">
                      <label class="filter-check filter-check--all">
                        <input type="checkbox" :checked="!selectedPriceRangeKey" @change="pickPriceRange('')" />
                        <span>Tất cả</span>
                      </label>

                      <label v-for="range in priceRanges" :key="range.key" class="filter-check">
                        <input
                          type="checkbox"
                          :checked="selectedPriceRangeKey === range.key"
                          @change="pickPriceRange(selectedPriceRangeKey === range.key ? '' : range.key)"
                        />
                        <span>{{ range.label }}</span>
                      </label>
                    </div>
                  </div>
                </div>

                <div class="card">
                  <div class="card-heading">
                    <button type="button" class="accordion-toggle" @click="togglePanel('quick')">
                      <span>Bộ lọc nhanh</span>
                      <span class="accordion-icon" :class="{ open: openPanels.quick }">⌄</span>
                    </button>
                  </div>
                  <div v-show="openPanels.quick" class="card-body card-body--panel">
                    <div class="shop__sidebar__categories">
                      <label class="filter-check">
                        <input type="checkbox" :checked="inStockOnly" @change="toggleInStock(!inStockOnly)" />
                        <span>Chỉ hiện sản phẩm còn hàng</span>
                      </label>

                      <label class="filter-check">
                        <input type="checkbox" :checked="saleOnly" @change="toggleSaleOnly(!saleOnly)" />
                        <span>Chỉ hiện sản phẩm đang giảm giá</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-9">
            <div class="shop-toolbar mb-4">
              <div class="shop-toolbar__top">
                <form class="shop-toolbar__search" @submit.prevent="applySearch">
                  <input
                    v-model="keyword"
                    type="text"
                    placeholder="Tìm kiếm theo tên sản phẩm, SKU hoặc thương hiệu"
                  />
                  <button type="submit">Tìm</button>
                </form>

                <div class="shop-toolbar__sort">
                  <label for="shop-sort">Sắp xếp</label>
                  <select id="shop-sort" v-model="sort" @change="loadProducts(0)">
                    <option value="newest">Mới nhất</option>
                    <option value="price_asc">Giá thấp đến cao</option>
                    <option value="price_desc">Giá cao đến thấp</option>
                    <option value="best_selling">Bán chạy</option>
                  </select>
                </div>
              </div>

              <div class="shop-toolbar__summary">
                <p>
                  Hiển thị {{ visibleRangeText }}
                </p>
                <div v-if="activeFilterLabels.length" class="active-filters">
                  <span v-for="label in activeFilterLabels" :key="label" class="active-filter-chip">
                    {{ label }}
                  </span>
                </div>
              </div>
            </div>

            <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
            <div v-if="loading" class="text-center py-5">Đang tải sản phẩm...</div>

            <div v-else class="row">
              <div v-for="product in displayedProducts" :key="product.id" class="col-lg-4 col-md-6 col-sm-6">
                <div class="product__item product-card">
                  <div class="product__item__pic set-bg" :style="productCardStyle(product.imageUrl)">
                    <span v-if="hasSale(product)" class="label">Sale</span>
                    <span v-if="!isInStock(product)" class="stock-badge stock-badge--out">Hết hàng</span>
                    <span v-else class="stock-badge">Còn hàng</span>
                    <ul class="product__hover">
                      <li>
                        <RouterLink :to="`/shop-details/${product.id}`">
                          <img src="/legacy/img/icon/search.png" alt="Chi tiết" />
                        </RouterLink>
                      </li>
                    </ul>
                  </div>
                  <div class="product__item__text">
                    <h6 class="product-name">{{ product.name }}</h6>
                    <RouterLink :to="`/shop-details/${product.id}`" class="add-cart">+ Xem chi tiết</RouterLink>
                    <div class="rating text-muted small">{{ product.brand || 'GoalStore' }}</div>
                    <h5>
                      <template v-if="hasSale(product)">
                        <span class="price-sale">{{ formatCurrency(product.salePrice) }}</span>
                        <span class="price-original">{{ formatCurrency(product.price || product.minPrice) }}</span>
                      </template>
                      <template v-else>
                        {{ formatPriceRange(product) }}
                      </template>
                    </h5>
                    <p class="mb-0 mt-2 text-muted small product-meta">
                      {{ product.categoryNames?.join(' • ') || 'Sản phẩm bóng đá chính hãng' }}
                    </p>
                  </div>
                </div>
              </div>

              <div v-if="!displayedProducts.length" class="col-12">
                <div class="empty-state">
                  <h5>Không tìm thấy sản phẩm phù hợp.</h5>
                  <p>Hãy thử đổi từ khóa hoặc bỏ bớt bộ lọc để xem thêm sản phẩm.</p>
                  <button type="button" class="sidebar-action-btn" @click="clearAllFilters">Xóa bộ lọc</button>
                </div>
              </div>
            </div>

            <div class="row mt-4" v-if="pageNumbers.length > 1">
              <div class="col-lg-12">
                <div class="product__pagination d-flex gap-2 align-items-center flex-wrap">
                  <a href="#" @click.prevent="prevPage" :class="{ disabled: currentPage === 0 }">&laquo;</a>
                  <a
                    v-for="pageNumber in pageNumbers"
                    :key="pageNumber"
                    href="#"
                    :class="{ active: currentPage === pageNumber - 1 }"
                    @click.prevent="loadProducts(pageNumber - 1)"
                  >
                    {{ pageNumber }}
                  </a>
                  <a href="#" @click.prevent="nextPage" :class="{ disabled: currentPage >= (pageData.totalPages || 1) - 1 }">&raquo;</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { getPublicCategories, getPublicProducts, resolveImageUrl } from '@/shared/lib/shop.api'
import type { PageResponse, ProductCategory, PublicProductSummary } from '@/shared/lib/shop.types'

type PriceRangeOption = {
  key: string
  label: string
  min?: number
  max?: number
}

const keyword = ref('')
const sort = ref('newest')
const selectedBrand = ref('')
const selectedCategoryIds = ref<number[]>([])
const selectedPriceRangeKey = ref('')
const inStockOnly = ref(false)
const saleOnly = ref(false)
const loading = ref(false)
const error = ref('')
const currentPage = ref(0)
const pageSize = 9

const openPanels = ref({
  category: true,
  brand: true,
  price: true,
  quick: true,
})

const priceRanges: PriceRangeOption[] = [
  { key: 'under-200', label: 'Dưới 200.000₫', max: 200000 },
  { key: '200-500', label: '200.000₫ - 500.000₫', min: 200000, max: 500000 },
  { key: '500-1000', label: '500.000₫ - 1.000.000₫', min: 500000, max: 1000000 },
  { key: 'over-1000', label: 'Trên 1.000.000₫', min: 1000000 },
]

const pageData = ref<PageResponse<PublicProductSummary>>({
  content: [],
  page: 0,
  size: pageSize,
  totalElements: 0,
  totalPages: 0,
  last: true,
})

const categories = ref<ProductCategory[]>([])
const filterProductsSource = ref<PublicProductSummary[]>([])

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatCurrency(value: unknown) {
  return `${toNumber(value).toLocaleString('vi-VN')}₫`
}

function hasSale(product: PublicProductSummary) {
  return toNumber(product.salePrice) > 0 && toNumber(product.salePrice) < toNumber(product.price || product.minPrice)
}

function isInStock(product: PublicProductSummary) {
  return toNumber(product.totalStock) > 0
}

function getFinalPrice(product: PublicProductSummary) {
  return toNumber(product.salePrice || product.minPrice || product.price)
}

function formatPriceRange(product: PublicProductSummary) {
  const minPrice = toNumber(product.minPrice || product.price)
  const maxPrice = toNumber(product.maxPrice || product.price)

  if (minPrice > 0 && maxPrice > 0 && minPrice !== maxPrice) {
    return `${formatCurrency(minPrice)} - ${formatCurrency(maxPrice)}`
  }

  return formatCurrency(minPrice)
}

function productCardStyle(imageUrl?: string) {
  const url = resolveImageUrl(imageUrl) || '/legacy/img/product/product-1.jpg'
  return {
    backgroundImage: `url("${url}")`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  }
}

const displayedProducts = computed(() => pageData.value.content || [])

const selectedPriceRange = computed(() => {
  return priceRanges.find((range) => range.key === selectedPriceRangeKey.value)
})

const selectedCategoryNames = computed(() => {
  const nameMap = new Map((categories.value || []).map((item) => [Number(item.id), item.name]))
  return selectedCategoryIds.value
    .map((id) => nameMap.get(Number(id)) || '')
    .filter(Boolean)
})

const brandOptions = computed(() => {
  const map = new Map<string, number>()
  ;(filterProductsSource.value || []).forEach((product) => {
    const key = (product.brand || '').trim()
    if (!key) return
    map.set(key, (map.get(key) || 0) + 1)
  })
  return Array.from(map.entries())
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => a.name.localeCompare(b.name, 'vi'))
})

const totalBrandCount = computed(() => brandOptions.value.reduce((sum, item) => sum + item.count, 0))

const categoryOptions = computed(() => {
  return (categories.value || [])
    .filter((item) => toNumber(item.id) > 0 && String(item.name || '').trim())
    .sort((a, b) => String(a.name || '').localeCompare(String(b.name || ''), 'vi'))
})

const totalCategoryCount = computed(() => {
  const total = categoryOptions.value.reduce((sum, item) => sum + toNumber(item.productCount), 0)
  return total || toNumber(pageData.value.totalElements)
})

const visibleRangeText = computed(() => {
  const total = toNumber(pageData.value.totalElements)
  if (!total || !displayedProducts.value.length) return '0 sản phẩm'
  const from = currentPage.value * pageSize + 1
  const to = Math.min(currentPage.value * pageSize + displayedProducts.value.length, total)
  return `${from} - ${to} trong ${total} sản phẩm`
})

const activeFilterLabels = computed(() => {
  const labels: string[] = []

  if (keyword.value.trim()) labels.push(`Từ khóa: ${keyword.value.trim()}`)
  if (selectedBrand.value) labels.push(`Thương hiệu: ${selectedBrand.value}`)
  if (selectedCategoryNames.value.length) labels.push(`Danh mục: ${selectedCategoryNames.value.join(', ')}`)
  if (selectedPriceRange.value) labels.push(`Giá: ${selectedPriceRange.value.label}`)
  if (inStockOnly.value) labels.push('Chỉ còn hàng')
  if (saleOnly.value) labels.push('Đang giảm giá')

  return labels
})

const pageNumbers = computed(() => {
  const totalPages = Math.max(pageData.value.totalPages || 1, 1)
  return Array.from({ length: totalPages }, (_, index) => index + 1)
})

async function loadProducts(page = currentPage.value) {
  loading.value = true
  error.value = ''
  try {
    const response = await getPublicProducts({
      page,
      size: pageSize,
      keyword: keyword.value.trim() || undefined,
      brand: selectedBrand.value || undefined,
      categoryIds: selectedCategoryIds.value,
      categoryNames: selectedCategoryNames.value,
      minPrice: selectedPriceRange.value?.min,
      maxPrice: selectedPriceRange.value?.max,
      inStock: inStockOnly.value || undefined,
      saleOnly: saleOnly.value || undefined,
      sort: sort.value as 'newest' | 'price_asc' | 'price_desc' | 'best_selling',
    })
    pageData.value = response
    currentPage.value = response.page || 0
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể tải danh sách sản phẩm'
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = await getPublicCategories()
  } catch {
    categories.value = []
  }
}

async function loadFilterSource() {
  try {
    const response = await getPublicProducts({ page: 0, size: 200, sort: 'newest' })
    filterProductsSource.value = response.content || []
  } catch {
    filterProductsSource.value = []
  }
}

function applySearch() {
  loadProducts(0)
}

function pickBrand(brand: string) {
  selectedBrand.value = brand
  loadProducts(0)
}

function isCategorySelected(categoryId?: number | string | null) {
  return selectedCategoryIds.value.includes(toNumber(categoryId))
}

function toggleCategory(categoryId?: number | string | null) {
  const normalizedId = toNumber(categoryId)
  if (!normalizedId) return

  if (selectedCategoryIds.value.includes(normalizedId)) {
    selectedCategoryIds.value = selectedCategoryIds.value.filter((id) => id !== normalizedId)
  } else {
    selectedCategoryIds.value = [...selectedCategoryIds.value, normalizedId]
  }

  loadProducts(0)
}

function clearCategoryFilter() {
  selectedCategoryIds.value = []
  loadProducts(0)
}

function pickPriceRange(key: string) {
  selectedPriceRangeKey.value = key
  loadProducts(0)
}

function toggleInStock(flag: boolean) {
  inStockOnly.value = flag
  loadProducts(0)
}

function toggleSaleOnly(flag: boolean) {
  saleOnly.value = flag
  loadProducts(0)
}

function clearAllFilters() {
  keyword.value = ''
  selectedBrand.value = ''
  selectedCategoryIds.value = []
  selectedPriceRangeKey.value = ''
  inStockOnly.value = false
  saleOnly.value = false
  sort.value = 'newest'
  loadProducts(0)
}

function togglePanel(panel: keyof typeof openPanels.value) {
  openPanels.value[panel] = !openPanels.value[panel]
}

function prevPage() {
  if (currentPage.value <= 0) return
  loadProducts(currentPage.value - 1)
}

function nextPage() {
  const totalPages = Math.max(pageData.value.totalPages || 1, 1)
  if (currentPage.value >= totalPages - 1) return
  loadProducts(currentPage.value + 1)
}

onMounted(async () => {
  document.title = 'GoalStore | Sản phẩm'
  const preloader = document.getElementById('preloder')
  if (preloader) preloader.style.display = 'none'
  window.scrollTo({ top: 0 })
  await Promise.all([loadCategories(), loadFilterSource(), loadProducts(0)])
})
</script>

<style scoped>
.alert {
  padding: 14px 18px;
  border-radius: 8px;
}

.alert-danger {
  background: #fff5f5;
  color: #c53030;
}

.custom-accordion .card {
  border: none;
  border-bottom: 1px solid #f2f2f2;
}

.accordion-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: transparent;
  border: none;
  padding: 0;
  font-size: 24px;
  font-weight: 700;
  color: #111111;
  text-align: left;
}

.accordion-icon {
  font-size: 18px;
  transition: transform 0.2s ease;
}

.accordion-icon.open {
  transform: rotate(180deg);
}

.card-body--panel {
  padding-top: 18px;
}

.shop__sidebar__actions {
  margin-bottom: 24px;
}

.sidebar-action-btn {
  width: 100%;
  border: 1px solid #111111;
  background: #111111;
  color: #ffffff;
  padding: 11px 14px;
  font-size: 13px;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  transition: 0.2s ease;
}

.sidebar-action-btn:hover {
  background: #ffffff;
  color: #111111;
}

.shop__sidebar__categories {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-check {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  cursor: pointer;
  font-size: 14px;
  color: #111111;
  line-height: 1.4;
}

.filter-check input {
  width: 16px;
  height: 16px;
  accent-color: #111111;
  cursor: pointer;
  flex: 0 0 auto;
}

.filter-check--all {
  padding-bottom: 10px;
  margin-bottom: 4px;
  border-bottom: 1px solid #f1f1f1;
}

.filter-empty {
  margin: 0;
  color: #666666;
  font-size: 14px;
}

.shop-toolbar {
  padding: 28px 24px;
  border: 1px solid #ebebeb;
  border-radius: 0;
  background: #ffffff;
  margin-bottom: 28px;
}

.shop-toolbar__top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 250px;
  gap: 18px;
  align-items: end;
  margin-bottom: 18px;
}

.shop-toolbar__search {
  display: flex;
  align-items: stretch;
  width: 100%;
}

.shop-toolbar__search input {
  flex: 1;
  height: 54px;
  border: 1px solid #d9d9d9;
  border-right: none;
  border-radius: 0;
  padding: 0 18px;
  font-size: 14px;
  color: #111111;
  background: #ffffff;
}

.shop-toolbar__search input:focus {
  outline: none;
  border-color: #111111;
}

.shop-toolbar__search button {
  min-width: 96px;
  height: 54px;
  border: 1px solid #111111;
  border-radius: 0;
  background: #111111;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  transition: 0.2s ease;
}

.shop-toolbar__search button:hover {
  background: #ffffff;
  color: #111111;
}

.shop-toolbar__summary p {
  margin-bottom: 10px;
}

.shop-toolbar__sort {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 10px;
}

.shop-toolbar__sort label {
  margin: 0;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 600;
  color: #111111;
}

.shop-toolbar__sort select {
  width: 100%;
  height: 54px;
  border: 1px solid #d9d9d9;
  border-radius: 0;
  padding: 0 16px;
  background: #ffffff;
  font-size: 14px;
  color: #111111;
}

.shop-toolbar__sort select:focus {
  outline: none;
  border-color: #111111;
}

.active-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.active-filter-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 0;
  background: #f3f3f3;
  color: #111111;
  font-size: 12px;
}

.product-card {
  height: 100%;
}

.product__item__pic {
  position: relative;
}

.stock-badge {
  position: absolute;
  left: 12px;
  bottom: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(17, 17, 17, 0.78);
  color: #ffffff;
  font-size: 11px;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.stock-badge--out {
  background: rgba(197, 48, 48, 0.9);
}

.product-name {
  min-height: 44px;
}

.product-meta {
  min-height: 40px;
}

.price-sale {
  color: #e53637;
  margin-right: 8px;
}

.price-original {
  text-decoration: line-through;
  color: #999999;
  font-size: 14px;
  font-weight: 400;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
  border: 1px solid #ebebeb;
  border-radius: 10px;
}

.empty-state h5 {
  margin-bottom: 10px;
}

.empty-state p {
  color: #666666;
  margin-bottom: 20px;
}

.product__pagination a.disabled {
  pointer-events: none;
  opacity: 0.4;
}

@media (max-width: 991px) {
  .shop-toolbar {
    padding: 22px 18px;
  }

  .shop-toolbar__top {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 575px) {
  .shop-toolbar__search {
    flex-direction: column;
  }

  .shop-toolbar__search input {
    border-right: 1px solid #d9d9d9;
    border-bottom: none;
  }

  .shop-toolbar__search button {
    width: 100%;
  }
}
</style>
