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
                  <a href="/">Trang chủ</a>
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
                <div class="shop__sidebar__search">
                  <form @submit.prevent="applySearch">
                    <input v-model="keyword" type="text" placeholder="Tìm kiếm..." />
                    <button type="submit"><span class="icon_search"></span></button>
                  </form>
                </div>
  
                <div class="shop__sidebar__accordion">
                  <div class="accordion" id="accordionExample">
                    <div class="card">
                      <div class="card-heading">
                        <a data-toggle="collapse" data-target="#collapseOne">Danh mục</a>
                      </div>
                      <div id="collapseOne" class="collapse show" data-parent="#accordionExample">
                        <div class="card-body">
                          <div class="shop__sidebar__categories">
                            <label class="category-check category-check--all">
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
                              class="category-check"
                            >
                              <input
                                type="checkbox"
                                :checked="isCategorySelected(category.id)"
                                @change="toggleCategory(category.id)"
                              />
                              <span>{{ category.name }} ({{ category.productCount || 0 }})</span>
                            </label>
                          </div>
                        </div>
                      </div>
                    </div>
  
                    <div class="card">
                      <div class="card-heading">
                        <a data-toggle="collapse" data-target="#collapseTwo">Thương hiệu</a>
                      </div>
                      <div id="collapseTwo" class="collapse show" data-parent="#accordionExample">
                        <div class="card-body">
                          <div class="shop__sidebar__brand">
                            <ul>
                              <li><a href="#" @click.prevent="pickBrand('')">Tất cả</a></li>
                              <li v-for="brand in brandOptions" :key="brand.name">
                                <a href="#" @click.prevent="pickBrand(brand.name)">
                                  {{ brand.name }} ({{ brand.count }})
                                </a>
                              </li>
                            </ul>
                          </div>
                        </div>
                      </div>
                    </div>
  
                    <div class="card">
                      <div class="card-heading">
                        <a data-toggle="collapse" data-target="#collapseThree">Bộ lọc nhanh</a>
                      </div>
                      <div id="collapseThree" class="collapse show" data-parent="#accordionExample">
                        <div class="card-body">
                          <div class="shop__sidebar__price">
                            <ul>
                              <li><a href="#" @click.prevent="toggleInStock(false)">Hiển thị tất cả</a></li>
                              <li><a href="#" @click.prevent="toggleInStock(true)">Chỉ còn hàng</a></li>
                              <li v-if="selectedBrand"><a href="#" @click.prevent="pickBrand('')">Bỏ lọc thương hiệu</a></li>
                              <li v-if="selectedCategoryIds.length"><a href="#" @click.prevent="clearCategoryFilter">Bỏ lọc danh mục</a></li>
                            </ul>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
  
            <div class="col-lg-9">
              <div class="shop__product__option">
                <div class="row">
                  <div class="col-lg-6 col-md-6 col-sm-6">
                    <div class="shop__product__option__left">
                      <p>
                        Hiển thị {{ visibleRangeText }}
                        <span v-if="selectedBrand"> | Thương hiệu: {{ selectedBrand }}</span>
                        <span v-if="selectedCategoryNames.length"> | Danh mục: {{ selectedCategoryNames.join(', ') }}</span>
                      </p>
                    </div>
                  </div>
                  <div class="col-lg-6 col-md-6 col-sm-6">
                    <div class="shop__product__option__right">
                      <p>Sắp xếp:</p>
                      <select v-model="sort" @change="loadProducts(0)">
                        <option value="newest">Mới nhất</option>
                        <option value="price_asc">Giá thấp đến cao</option>
                        <option value="price_desc">Giá cao đến thấp</option>
                        <option value="best_selling">Bán chạy</option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
  
              <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
              <div v-if="loading" class="text-center py-5">Đang tải sản phẩm...</div>
  
              <div v-else class="row">
                <div v-for="product in displayedProducts" :key="product.id" class="col-lg-4 col-md-6 col-sm-6">
                  <div class="product__item">
                    <div class="product__item__pic set-bg" :style="productCardStyle(product.imageUrl)">
                      <span v-if="hasSale(product)" class="label">Sale</span>
                      <ul class="product__hover">
                        <li>
                          <RouterLink :to="`/shop-details/${product.id}`">
                            <img src="/legacy/img/icon/search.png" alt="Chi tiết" />
                          </RouterLink>
                        </li>
                      </ul>
                    </div>
                    <div class="product__item__text">
                      <h6>{{ product.name }}</h6>
                      <a :href="`/shop-details/${product.id}`" class="add-cart">+ Xem chi tiết</a>
                      <div class="rating text-muted small">{{ product.brand || 'GoalStore' }}</div>
                      <h5>{{ formatPriceRange(product) }}</h5>
                      <p class="mb-0 mt-2 text-muted small">
                        {{ product.categoryNames?.join(' • ') || 'Sản phẩm bóng đá chính hãng' }}
                      </p>
                    </div>
                  </div>
                </div>
  
                <div v-if="!displayedProducts.length" class="col-12">
                  <div class="text-center py-5 border rounded">
                    Không tìm thấy sản phẩm phù hợp.
                  </div>
                </div>
              </div>
  
              <div class="row mt-4">
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
  import { getCustomerId, isCustomerLoggedIn } from '@/shared/lib/auth'
  import { getCart, getPublicCategories, getPublicProducts, resolveImageUrl } from '@/shared/lib/shop.api'
  import type { CartResponse, PageResponse, ProductCategory, PublicProductSummary } from '@/shared/lib/shop.types'
  
  const keyword = ref('')
  const sort = ref('newest')
  const selectedBrand = ref('')
  const selectedCategoryIds = ref<number[]>([])
  const inStockOnly = ref(false)
  const loading = ref(false)
  const error = ref('')
  const currentPage = ref(0)
  const pageSize = 9
  
  const pageData = ref<PageResponse<PublicProductSummary>>({
    content: [],
    page: 0,
    size: pageSize,
    totalElements: 0,
    totalPages: 0,
    last: true,
  })
  
  const cart = ref<CartResponse>({ items: [], totalAmount: 0 })
  const categories = ref<ProductCategory[]>([])
  
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
  
  function formatPriceRange(product: PublicProductSummary) {
    const salePrice = toNumber(product.salePrice)
    const minPrice = toNumber(product.minPrice || product.price)
    const maxPrice = toNumber(product.maxPrice || product.price)
  
    if (salePrice > 0) return formatCurrency(salePrice)
    if (minPrice > 0 && maxPrice > 0 && minPrice !== maxPrice) return `${formatCurrency(minPrice)} - ${formatCurrency(maxPrice)}`
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
  
  const selectedCategoryNames = computed(() => {
    const nameMap = new Map((categories.value || []).map((item) => [Number(item.id), item.name]))
    return selectedCategoryIds.value
      .map((id) => nameMap.get(Number(id)) || '')
      .filter(Boolean)
  })
  
  const brandOptions = computed(() => {
    const map = new Map<string, number>()
    ;(pageData.value.content || []).forEach((product) => {
      const key = (product.brand || '').trim()
      if (!key) return
      map.set(key, (map.get(key) || 0) + 1)
    })
    return Array.from(map.entries()).map(([name, count]) => ({ name, count }))
  })
  
  const categoryOptions = computed(() => {
    return (categories.value || [])
      .filter((item) => toNumber(item.id) > 0 && String(item.name || '').trim())
      .sort((a, b) => String(a.name || '').localeCompare(String(b.name || ''), 'vi'))
  })
  
  const totalCategoryCount = computed(() => {
    const total = categoryOptions.value.reduce((sum, item) => sum + toNumber(item.productCount), 0)
    return total || toNumber(pageData.value.totalElements)
  })
  
  const cartCount = computed(() => (cart.value.items || []).reduce((sum, item) => sum + toNumber(item.quantity), 0))
  const cartTotal = computed(() => toNumber(cart.value.totalAmount))
  
  const visibleRangeText = computed(() => {
    const total = toNumber(pageData.value.totalElements)
    if (!total || !displayedProducts.value.length) return '0 sản phẩm'
    const from = currentPage.value * pageSize + 1
    const to = Math.min(currentPage.value * pageSize + displayedProducts.value.length, total)
    return `${from} - ${to} trong ${total} sản phẩm`
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
        inStock: inStockOnly.value || undefined,
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
  
  async function loadCartSummary() {
    if (!isCustomerLoggedIn()) {
      cart.value = { items: [], totalAmount: 0 }
      return
    }
  
    const customerId = getCustomerId()
    if (!customerId) return
  
    try {
      cart.value = await getCart(customerId)
    } catch {
      cart.value = { items: [], totalAmount: 0 }
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
  
  function toggleInStock(flag: boolean) {
    inStockOnly.value = flag
    loadProducts(0)
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
    await Promise.all([loadCategories(), loadProducts(0), loadCartSummary()])
  })
  </script>
  
  <style scoped>
  .product__pagination a.disabled {
    pointer-events: none;
    opacity: 0.4;
  }
  
  .alert {
    padding: 14px 18px;
    border-radius: 8px;
  }
  
  .alert-danger {
    background: #fff5f5;
    color: #c53030;
  }
  
  .shop__sidebar__categories {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .category-check {
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 0;
    cursor: pointer;
    font-size: 14px;
    color: #111111;
  }
  
  .category-check input {
    width: 16px;
    height: 16px;
    accent-color: #111111;
    cursor: pointer;
  }
  
  .category-check span {
    line-height: 1.4;
  }
  
  .category-check--all {
    padding-bottom: 10px;
    margin-bottom: 4px;
    border-bottom: 1px solid #f1f1f1;
  }
  </style>
  