<template>
    <div class="legacy-page shop-details-page">
      <div id="preloder"><div class="loader"></div></div>
  
      <section class="breadcrumb-option">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="breadcrumb__text">
                <h4>Chi tiết sản phẩm</h4>
                <div class="breadcrumb__links">
                  <RouterLink to="/">Trang chủ</RouterLink>
                  <RouterLink to="/shop">Sản phẩm</RouterLink>
                  <span>{{ product?.name || 'Chi tiết sản phẩm' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
  
      <section class="shop-details" v-if="loading">
        <div class="container py-5 text-center">Đang tải chi tiết sản phẩm...</div>
      </section>
  
      <section class="shop-details" v-else-if="error">
        <div class="container py-5">
          <div class="alert alert-danger">{{ error }}</div>
        </div>
      </section>
  
      <section class="shop-details spad" v-else-if="product">
        <div class="container">
          <div class="row product-detail-main align-items-start">
            <div class="col-lg-7 col-md-12">
              <div class="detail-gallery">
                <div class="detail-gallery__thumbs">
                  <button
                    v-for="(image, index) in galleryImages"
                    :key="image.id ?? image.imageUrl ?? index"
                    type="button"
                    class="detail-gallery__thumb"
                    :class="{ active: currentImage === resolveImage(image.imageUrl) }"
                    @click="currentImage = resolveImage(image.imageUrl)"
                  >
                    <img :src="resolveImage(image.imageUrl)" :alt="`${product.name} ${index + 1}`" />
                  </button>
                </div>
  
                <div class="detail-gallery__main">
                  <div class="detail-gallery__badge" v-if="hasSale">Sale</div>
                  <img class="detail-gallery__main-image" :src="currentImage || defaultImage" :alt="product.name" />
                </div>
              </div>
            </div>
  
            <div class="col-lg-5 col-md-12">
              <div class="detail-summary">
                <div class="detail-summary__brand">{{ product.brand || 'GoalStore' }}</div>
                <h2 class="detail-summary__title">{{ product.name }}</h2>
  
                <div class="detail-summary__meta">
                  <span class="detail-pill" :class="selectedVariantStock > 0 ? 'in-stock' : 'out-stock'">
                    {{ selectedVariantStock > 0 ? 'Còn hàng' : 'Hết hàng' }}
                  </span>
                  <span class="detail-summary__sku">SKU: {{ selectedVariant?.sku || product.baseSku || 'Đang cập nhật' }}</span>
                </div>
  
                <div class="detail-summary__price">
                  <span class="detail-summary__price-current">{{ formatCurrency(selectedVariantPrice) }}</span>
                  <span v-if="hasVariantSale" class="detail-summary__price-old">{{ formatCurrency(selectedVariantOriginalPrice) }}</span>
                </div>
  
                <p class="detail-summary__desc">
                  {{ product.description || 'Sản phẩm chính hãng dành cho người yêu bóng đá, thiết kế đẹp và dễ phối đồ khi đi tập hoặc thi đấu.' }}
                </p>
  
                <div class="detail-summary__group" v-if="sizeOptions.length">
                  <div class="detail-summary__label">Kích thước</div>
                  <div class="option-chips">
                    <label
                      v-for="size in sizeOptions"
                      :key="size"
                      class="option-chip"
                      :class="{ active: selectedSize === size }"
                      :for="`size-${size}`"
                    >
                      {{ size }}
                      <input :id="`size-${size}`" v-model="selectedSize" type="radio" :value="size" />
                    </label>
                  </div>
                </div>
  
                <div class="detail-summary__group" v-if="colorOptions.length">
                  <div class="detail-summary__label">Màu sắc</div>
                  <div class="color-options">
                    <label
                      v-for="color in colorOptions"
                      :key="color"
                      :for="`color-${color}`"
                      :title="color"
                      class="color-option"
                      :class="{ active: selectedColor === color }"
                    >
                      <input :id="`color-${color}`" v-model="selectedColor" type="radio" :value="color" />
                      <span class="color-option__swatch" :style="swatchStyle(color)"></span>
                      <span class="color-option__name">{{ color }}</span>
                    </label>
                  </div>
                </div>
  
                <div class="detail-summary__group">
                  <div class="detail-summary__label">Số lượng</div>
                  <div class="detail-actions">
                    <div class="detail-qty">
                      <button type="button" class="detail-qty__btn" @click="changeQuantity(-1)">−</button>
                      <input v-model="quantityInput" type="number" min="1" class="detail-qty__input" />
                      <button type="button" class="detail-qty__btn" @click="changeQuantity(1)">+</button>
                    </div>
                    <button type="button" class="primary-btn detail-add-cart" @click="handleAddToCart">
                      Thêm vào giỏ hàng
                    </button>
                  </div>
                </div>
  
                <div v-if="message" class="alert alert-success mt-4">{{ message }}</div>
                <div v-if="actionError" class="alert alert-danger mt-4">{{ actionError }}</div>
  
                <div class="detail-service-list">
                  <div class="detail-service-item">
                    <strong>Giao hàng:</strong>
                    <span>Toàn quốc, kiểm tra hàng trước khi thanh toán.</span>
                  </div>
                  <div class="detail-service-item">
                    <strong>Đổi trả:</strong>
                    <span>Hỗ trợ 7 ngày nếu sản phẩm lỗi hoặc không đúng mô tả.</span>
                  </div>
                  <div class="detail-service-item">
                    <strong>Thanh toán:</strong>
                    <span>COD hoặc thanh toán trực tuyến an toàn.</span>
                  </div>
                </div>
  
                <div class="detail-info-card">
                  <div class="detail-info-row">
                    <span>Danh mục</span>
                    <strong>{{ categoryText }}</strong>
                  </div>
                  <div class="detail-info-row">
                    <span>Thương hiệu</span>
                    <strong>{{ product.brand || 'GoalStore' }}</strong>
                  </div>
                  <div class="detail-info-row">
                    <span>Tồn kho</span>
                    <strong>{{ selectedVariantStock }}</strong>
                  </div>
                  <div class="detail-info-row" v-if="selectedColor">
                    <span>Màu đã chọn</span>
                    <strong>{{ selectedColor }}</strong>
                  </div>
                  <div class="detail-info-row" v-if="selectedSize">
                    <span>Size đã chọn</span>
                    <strong>{{ selectedSize }}</strong>
                  </div>
                </div>
              </div>
            </div>
          </div>
  
          <div class="row mt-5">
            <div class="col-lg-12">
              <div class="product__details__tab detail-tabs">
                <ul class="nav nav-tabs" role="tablist">
                  <li class="nav-item"><a class="nav-link active" href="#">Mô tả sản phẩm</a></li>
                  <li class="nav-item"><a class="nav-link" href="#">Thông tin thêm</a></li>
                </ul>
                <div class="tab-content">
                  <div class="tab-pane active">
                    <div class="product__details__tab__content">
                      <div class="product__details__tab__content__item">
                        <h5>Thông tin sản phẩm</h5>
                        <p>{{ product.description || 'Sản phẩm được đồng bộ từ backend public product của GoalStore.' }}</p>
                      </div>
                      <div class="product__details__tab__content__item">
                        <h5>Thuộc tính</h5>
                        <p v-if="!product.attributes?.length">Chưa có thuộc tính bổ sung.</p>
                        <ul v-else class="detail-attribute-list">
                          <li v-for="attribute in product.attributes" :key="attribute.id">
                            <strong>{{ attribute.name }}:</strong> {{ attribute.value }}
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
  
      <section class="related spad" v-if="relatedProducts.length">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <h3 class="related-title">Sản phẩm liên quan</h3>
            </div>
          </div>
          <div class="row">
            <div v-for="item in relatedProducts" :key="item.id" class="col-lg-3 col-md-6 col-sm-6">
              <div class="product__item">
                <div class="product__item__pic set-bg" :style="imageStyle(item.imageUrl || defaultImage)"></div>
                <div class="product__item__text">
                  <h6>{{ item.name }}</h6>
                  <RouterLink :to="`/shop-details/${item.id}`" class="add-cart">+ Xem chi tiết</RouterLink>
                  <h5>{{ formatSummaryPrice(item) }}</h5>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, onMounted, ref, watch } from 'vue'
  import { RouterLink, useRoute, useRouter } from 'vue-router'
  import { getCustomerId, isCustomerLoggedIn } from '@/shared/lib/auth'
  import { addCartItem, getCart, getPublicProductDetail, getPublicProducts, resolveImageUrl } from '@/shared/lib/shop.api'
  import type { CartResponse, ProductImage, ProductVariant, PublicProductDetail, PublicProductSummary } from '@/shared/lib/shop.types'
  
  const route = useRoute()
  const router = useRouter()
  const loading = ref(false)
  const error = ref('')
  const actionError = ref('')
  const message = ref('')
  const product = ref<PublicProductDetail | null>(null)
  const relatedProducts = ref<PublicProductSummary[]>([])
  const selectedSize = ref('')
  const selectedColor = ref('')
  const quantityInput = ref(1)
  const currentImage = ref('')
  const cart = ref<CartResponse>({ items: [], totalAmount: 0 })
  const defaultImage = '/legacy/img/product/product-1.jpg'
  
  function toNumber(value: unknown) {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : 0
  }
  
  function formatCurrency(value: unknown) {
    return `${toNumber(value).toLocaleString('vi-VN')}₫`
  }
  
  function resolveImage(url?: string | null) {
    return resolveImageUrl(url) || defaultImage
  }
  
  function imageStyle(url?: string | null) {
    const resolvedUrl = resolveImage(url)
    const safeUrl = resolvedUrl.replace(/"/g, '%22')
    return {
      backgroundImage: `url("${safeUrl}")`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
    }
  }
  
  function swatchStyle(color: string) {
    const map: Record<string, string> = {
      black: '#111111',
      trắng: '#ffffff',
      white: '#ffffff',
      đỏ: '#d62828',
      red: '#d62828',
      xanh: '#2563eb',
      blue: '#2563eb',
      vàng: '#f59e0b',
      yellow: '#f59e0b',
      xám: '#9ca3af',
      grey: '#9ca3af',
      gray: '#9ca3af',
      'xanh lá': '#16a34a',
      green: '#16a34a',
    }
    const normalized = color.trim().toLowerCase()
    return { background: map[normalized] || normalized, border: '1px solid #ddd' }
  }
  
  const galleryImages = computed<ProductImage[]>(() => {
    if (!product.value) return []
    const images = product.value.images || []
    if (images.length) return images
    return [{ id: 0, imageUrl: product.value.thumbnailUrl || defaultImage, avatar: true, sortOrder: 0 }]
  })
  
  const sizeOptions = computed(() => {
    const values = (product.value?.variants || []).map((item) => item.size).filter(Boolean) as string[]
    return Array.from(new Set(values))
  })
  
  const colorOptions = computed(() => {
    const values = (product.value?.variants || [])
      .filter((item) => !selectedSize.value || item.size === selectedSize.value)
      .map((item) => item.color)
      .filter(Boolean) as string[]
    return Array.from(new Set(values))
  })
  
  const selectedVariant = computed<ProductVariant | null>(() => {
    const variants = product.value?.variants || []
    return (
      variants.find(
        (item) =>
          (!selectedSize.value || item.size === selectedSize.value) &&
          (!selectedColor.value || item.color === selectedColor.value),
      ) || variants[0] || null
    )
  })
  
  const selectedVariantPrice = computed(() => {
    const variant = selectedVariant.value
    if (!variant) return 0
    return toNumber(variant.salePrice) || toNumber(variant.price)
  })
  
  const selectedVariantOriginalPrice = computed(() => {
    const variant = selectedVariant.value
    if (!variant) return 0
    return toNumber(variant.price)
  })
  
  const selectedVariantStock = computed(() => toNumber(selectedVariant.value?.stockQuantity))
  const hasVariantSale = computed(() => selectedVariantOriginalPrice.value > selectedVariantPrice.value)
  const hasSale = computed(() => hasVariantSale.value || toNumber(product.value?.salePrice) > 0)
  const categoryText = computed(() => product.value?.categories?.map((item) => item.name).join(', ') || 'Đang cập nhật')
  
  function formatSummaryPrice(item: PublicProductSummary) {
    return formatCurrency(item.salePrice || item.minPrice || item.price)
  }
  
  function changeQuantity(delta: number) {
    quantityInput.value = Math.max(1, toNumber(quantityInput.value) + delta)
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
  
  async function loadProduct() {
    loading.value = true
    error.value = ''
    message.value = ''
    actionError.value = ''
  
    const id = Number(route.params.id)
    if (!id) {
      error.value = 'Không tìm thấy sản phẩm cần xem.'
      loading.value = false
      return
    }
  
    try {
      const [detail, related] = await Promise.all([
        getPublicProductDetail(id),
        getPublicProducts({ page: 0, size: 4, sort: 'newest' }),
      ])
      product.value = detail
      relatedProducts.value = (related.content || []).filter((item) => item.id !== id).slice(0, 4)
      currentImage.value = resolveImage(detail.thumbnailUrl || detail.images?.[0]?.imageUrl)
  
      const firstVariant = (detail.variants || [])[0]
      selectedSize.value = firstVariant?.size || ''
      selectedColor.value = firstVariant?.color || ''
      quantityInput.value = 1
      document.title = `GoalStore | ${detail.name}`
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể tải chi tiết sản phẩm'
    } finally {
      loading.value = false
    }
  }
  
  async function handleAddToCart() {
    actionError.value = ''
    message.value = ''
  
    if (!isCustomerLoggedIn()) {
      router.push('/login')
      return
    }
  
    const customerId = getCustomerId()
    if (!customerId || !selectedVariant.value?.id) {
      actionError.value = 'Vui lòng chọn biến thể sản phẩm hợp lệ.'
      return
    }
  
    if (selectedVariantStock.value <= 0) {
      actionError.value = 'Biến thể này hiện đang hết hàng.'
      return
    }
  
    try {
      cart.value = await addCartItem({
        customerId,
        variantId: selectedVariant.value.id,
        quantity: Math.max(1, toNumber(quantityInput.value)),
      })
      message.value = 'Đã thêm sản phẩm vào giỏ hàng.'
    } catch (e: any) {
      actionError.value = e?.response?.data?.message || e?.message || 'Không thể thêm vào giỏ hàng'
    }
  }
  
  watch(selectedSize, () => {
    if (selectedColor.value && !colorOptions.value.includes(selectedColor.value)) {
      selectedColor.value = colorOptions.value[0] || ''
    }
  })
  
  watch(
    () => route.params.id,
    async () => {
      window.scrollTo({ top: 0, behavior: 'smooth' })
      await loadProduct()
    },
  )
  
  onMounted(async () => {
    const preloader = document.getElementById('preloder')
    if (preloader) preloader.style.display = 'none'
    window.scrollTo({ top: 0 })
    await Promise.all([loadProduct(), loadCartSummary()])
  })
  </script>
  
  <style scoped>
  .shop-details-page {
    background: #f7f5f2;
  }
  
  .product-detail-main {
    margin-bottom: 20px;
  }
  
  .detail-gallery {
    display: grid;
    grid-template-columns: 96px 1fr;
    gap: 20px;
    align-items: start;
  }
  
  .detail-gallery__thumbs {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }
  
  .detail-gallery__thumb {
    border: 1px solid #e5e5e5;
    background: #fff;
    padding: 0;
    width: 96px;
    height: 120px;
    overflow: hidden;
    transition: all 0.2s ease;
  }
  
  .detail-gallery__thumb.active,
  .detail-gallery__thumb:hover {
    border-color: #111;
  }
  
  .detail-gallery__thumb img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
  
  .detail-gallery__main {
    position: relative;
    background: #fff;
    min-height: 620px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
  }
  
  .detail-gallery__main-image {
    width: 100%;
    max-height: 560px;
    object-fit: contain;
  }
  
  .detail-gallery__badge {
    position: absolute;
    top: 20px;
    left: 20px;
    background: #111;
    color: #fff;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 2px;
    padding: 6px 10px;
  }
  
  .detail-summary {
    background: #fff;
    padding: 34px 32px;
    height: 100%;
  }
  
  .detail-summary__brand {
    font-size: 13px;
    text-transform: uppercase;
    letter-spacing: 2px;
    color: #888;
    margin-bottom: 8px;
  }
  
  .detail-summary__title {
    font-size: 34px;
    line-height: 1.3;
    margin-bottom: 16px;
    color: #111;
  }
  
  .detail-summary__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
    margin-bottom: 18px;
  }
  
  .detail-pill {
    display: inline-flex;
    align-items: center;
    padding: 6px 12px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 600;
  }
  
  .detail-pill.in-stock {
    background: #ecfdf3;
    color: #027a48;
  }
  
  .detail-pill.out-stock {
    background: #fef3f2;
    color: #b42318;
  }
  
  .detail-summary__sku {
    color: #666;
    font-size: 14px;
  }
  
  .detail-summary__price {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 18px;
  }
  
  .detail-summary__price-current {
    font-size: 36px;
    font-weight: 700;
    color: #111;
  }
  
  .detail-summary__price-old {
    font-size: 22px;
    color: #9b9b9b;
    text-decoration: line-through;
  }
  
  .detail-summary__desc {
    color: #555;
    line-height: 1.8;
    margin-bottom: 24px;
  }
  
  .detail-summary__group {
    margin-bottom: 24px;
  }
  
  .detail-summary__label {
    font-size: 14px;
    font-weight: 700;
    color: #111;
    margin-bottom: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
  
  .option-chips,
  .color-options {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .option-chip {
    min-width: 54px;
    border: 1px solid #ddd;
    padding: 10px 16px;
    text-align: center;
    cursor: pointer;
    background: #fff;
    transition: all 0.2s ease;
    margin-bottom: 0;
  }
  
  .option-chip input,
  .color-option input {
    display: none;
  }
  
  .option-chip.active {
    border-color: #111;
    background: #111;
    color: #fff;
  }
  
  .color-option {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #ddd;
    padding: 10px 12px;
    cursor: pointer;
    background: #fff;
    margin-bottom: 0;
  }
  
  .color-option.active {
    border-color: #111;
  }
  
  .color-option__swatch {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: inline-block;
  }
  
  .color-option__name {
    font-size: 14px;
    color: #222;
  }
  
  .detail-actions {
    display: flex;
    gap: 14px;
    align-items: center;
    flex-wrap: wrap;
  }
  
  .detail-qty {
    display: inline-flex;
    align-items: center;
    border: 1px solid #ddd;
    background: #fff;
  }
  
  .detail-qty__btn {
    width: 42px;
    height: 48px;
    border: 0;
    background: transparent;
    font-size: 24px;
    line-height: 1;
  }
  
  .detail-qty__input {
    width: 72px;
    height: 48px;
    border: 0;
    border-left: 1px solid #eee;
    border-right: 1px solid #eee;
    text-align: center;
  }
  
  .detail-add-cart {
    min-width: 220px;
    height: 48px;
    border: 0;
  }
  
  .detail-service-list {
    border-top: 1px solid #eee;
    border-bottom: 1px solid #eee;
    padding: 20px 0;
    margin: 28px 0;
    display: grid;
    gap: 12px;
  }
  
  .detail-service-item {
    display: flex;
    gap: 8px;
    color: #444;
    line-height: 1.7;
  }
  
  .detail-service-item strong {
    color: #111;
    min-width: 90px;
  }
  
  .detail-info-card {
    background: #fafafa;
    padding: 18px 20px;
  }
  
  .detail-info-row {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    padding: 10px 0;
    border-bottom: 1px solid #ececec;
    font-size: 14px;
  }
  
  .detail-info-row:last-child {
    border-bottom: 0;
  }
  
  .detail-info-row span {
    color: #666;
  }
  
  .detail-info-row strong {
    color: #111;
    text-align: right;
  }
  
  .detail-tabs {
    background: #fff;
    padding: 24px 30px 30px;
  }
  
  .detail-attribute-list {
    padding-left: 18px;
    color: #444;
  }
  
  .alert {
    padding: 14px 18px;
    border-radius: 8px;
  }
  
  .alert-danger {
    background: #fff5f5;
    color: #c53030;
  }
  
  .alert-success {
    background: #f0fff4;
    color: #2f855a;
  }
  
  @media (max-width: 991px) {
    .detail-gallery {
      grid-template-columns: 1fr;
    }
  
    .detail-gallery__thumbs {
      order: 2;
      flex-direction: row;
      overflow-x: auto;
    }
  
    .detail-gallery__thumb {
      width: 84px;
      height: 104px;
      flex: 0 0 auto;
    }
  
    .detail-gallery__main {
      min-height: 440px;
    }
  
    .detail-summary {
      margin-top: 24px;
    }
  }
  
  @media (max-width: 575px) {
    .detail-summary {
      padding: 24px 18px;
    }
  
    .detail-summary__title {
      font-size: 28px;
    }
  
    .detail-summary__price-current {
      font-size: 30px;
    }
  
    .detail-actions {
      flex-direction: column;
      align-items: stretch;
    }
  
    .detail-add-cart {
      width: 100%;
    }
  }
  </style>
  