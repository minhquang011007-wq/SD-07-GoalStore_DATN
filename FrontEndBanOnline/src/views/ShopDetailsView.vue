<template>
    <div class="legacy-page">
      <div id="preloder"><div class="loader"></div></div>

  
      <section class="breadcrumb-option">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="breadcrumb__text">
                <h4>Chi tiết sản phẩm</h4>
                <div class="breadcrumb__links">
                  <a href="/">Trang chủ</a>
                  <a href="/shop">Sản phẩm</a>
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
  
      <section class="shop-details" v-else-if="product">
        <div class="product__details__pic">
          <div class="container">
            <div class="row">
              <div class="col-lg-3 col-md-3">
                <ul class="nav nav-tabs" role="tablist">
                  <li v-for="(image, index) in galleryImages" :key="image.id ?? image.imageUrl ?? index" class="nav-item">
                    <a href="#" class="nav-link" :class="{ active: currentImage === image.imageUrl }" @click.prevent="currentImage = image.imageUrl || defaultImage">
                      <div class="product__thumb__pic set-bg" :style="imageStyle(image.imageUrl || defaultImage)"></div>
                    </a>
                  </li>
                </ul>
              </div>
              <div class="col-lg-6 col-md-9">
                <div class="product__details__pic__item">
                  <img class="product__details__pic__item--large" :src="currentImage || defaultImage" alt="product" />
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <div class="product__details__content">
          <div class="container">
            <div class="row d-flex justify-content-center">
              <div class="col-lg-8">
                <div class="product__details__text">
                  <h4>{{ product.name }}</h4>
                  <h3>{{ formatCurrency(selectedVariantPrice) }}</h3>
                  <p>{{ product.description || 'Sản phẩm chính hãng dành cho người yêu bóng đá.' }}</p>
  
                  <div class="product__details__option">
                    <div class="product__details__option__size">
                      <span>Size:</span>
                      <label
                        v-for="size in sizeOptions"
                        :key="size"
                        :class="{ active: selectedSize === size }"
                        :for="`size-${size}`"
                      >
                        {{ size }}
                        <input :id="`size-${size}`" v-model="selectedSize" type="radio" :value="size" />
                      </label>
                    </div>
  
                    <div class="product__details__option__color" v-if="colorOptions.length">
                      <span>Màu:</span>
                      <label
                        v-for="color in colorOptions"
                        :key="color"
                        :for="`color-${color}`"
                        :title="color"
                        :style="swatchStyle(color)"
                        :class="{ active: selectedColor === color }"
                      >
                        <input :id="`color-${color}`" v-model="selectedColor" type="radio" :value="color" />
                      </label>
                    </div>
                  </div>
  
                  <div class="product__details__cart__option">
                    <div class="quantity">
                      <div class="pro-qty">
                        <span class="qtybtn" @click="changeQuantity(-1)">-</span>
                        <input v-model="quantityInput" type="number" min="1" />
                        <span class="qtybtn" @click="changeQuantity(1)">+</span>
                      </div>
                    </div>
                    <a href="#" class="primary-btn" @click.prevent="handleAddToCart">Thêm vào giỏ hàng</a>
                  </div>
  
                  <div v-if="message" class="alert alert-success mt-4">{{ message }}</div>
                  <div v-if="actionError" class="alert alert-danger mt-4">{{ actionError }}</div>
  
                  <div class="product__details__last__option">
                    <h5><span>Cam kết GoalStore</span></h5>
                    <ul>
                      <li><span>SKU:</span> {{ selectedVariant?.sku || product.baseSku || 'Đang cập nhật' }}</li>
                      <li><span>Danh mục:</span> {{ product.categories?.map((item) => item.name).join(', ') || 'Đang cập nhật' }}</li>
                      <li><span>Thương hiệu:</span> {{ product.brand || 'GoalStore' }}</li>
                      <li><span>Tồn kho:</span> {{ selectedVariant?.stockQuantity ?? 0 }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
  
            <div class="row">
              <div class="col-lg-12">
                <div class="product__details__tab">
                  <ul class="nav nav-tabs" role="tablist">
                    <li class="nav-item"><a class="nav-link active" href="#">Mô tả</a></li>
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
                          <ul v-else>
                            <li v-for="attribute in product.attributes" :key="attribute.id">
                              {{ attribute.name }}: {{ attribute.value }}
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
                  <a :href="`/shop-details/${item.id}`" class="add-cart">+ Xem chi tiết</a>
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
  import { useRoute, useRouter } from 'vue-router'
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
  
  function imageStyle(url?: string | null) {
    const resolvedUrl = resolveImageUrl(url) || defaultImage
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
  
  const cartCount = computed(() => (cart.value.items || []).reduce((sum, item) => sum + toNumber(item.quantity), 0))
  const cartTotal = computed(() => toNumber(cart.value.totalAmount))
  
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
      currentImage.value = resolveImageUrl(detail.thumbnailUrl || detail.images?.[0]?.imageUrl) || defaultImage
  
      const firstVariant = (detail.variants || [])[0]
      selectedSize.value = firstVariant?.size || ''
      selectedColor.value = firstVariant?.color || ''
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
  
    if (toNumber(selectedVariant.value.stockQuantity) <= 0) {
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
  
  onMounted(async () => {
    const preloader = document.getElementById('preloder')
    if (preloader) preloader.style.display = 'none'
    window.scrollTo({ top: 0 })
    await Promise.all([loadProduct(), loadCartSummary()])
  })
  </script>
  
  <style scoped>
  .product__details__option__color label {
    margin-right: 10px;
  }
  
  .product__details__option__color label.active {
    outline: 2px solid #111;
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
  </style>
  