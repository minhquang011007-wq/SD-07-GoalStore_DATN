<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { getPublicProducts, resolveImageUrl } from '@/shared/lib/shop.api'
import type { PublicProductSummary } from '@/shared/lib/shop.types'

const loading = ref(false)
const error = ref('')
const featuredProducts = ref<PublicProductSummary[]>([])
const newArrivalProducts = ref<PublicProductSummary[]>([])

const heroStyle = computed(() => ({
  backgroundImage: "url('/legacy/img/bong-da-san-co-sut-bong-banner.jpg')",
}))

async function loadHomeProducts() {
  loading.value = true
  error.value = ''

  try {
    const [featuredResponse, newArrivalResponse] = await Promise.all([
      getPublicProducts({ page: 0, size: 8, sort: 'best_selling' }),
      getPublicProducts({ page: 0, size: 8, sort: 'newest' }),
    ])

    featuredProducts.value = featuredResponse.content || []
    newArrivalProducts.value = newArrivalResponse.content || []
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Không tải được sản phẩm trang chủ.'
  } finally {
    loading.value = false
  }
}

function productCardStyle(url?: string) {
  const imageUrl = resolveImageUrl(url) || '/legacy/img/product/product-1.jpg'
  return {
    backgroundImage: `url('${imageUrl}')`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  }
}

function toNumber(value: unknown) {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

function hasSale(product: PublicProductSummary) {
  const price = toNumber(product.price || product.minPrice)
  const salePrice = toNumber(product.salePrice)
  return salePrice > 0 && salePrice < price
}

function formatCurrency(value?: number | string | null) {
  const amount = toNumber(value)
  if (!amount) return 'Liên hệ'

  return new Intl.NumberFormat('vi-VN').format(amount) + '₫'
}

function formatPrice(product: PublicProductSummary) {
  const salePrice = toNumber(product.salePrice)
  const price = toNumber(product.price || product.minPrice || product.maxPrice)
  return formatCurrency(salePrice || price)
}

function isInStock(product: PublicProductSummary) {
  return toNumber(product.totalStock) > 0
}

onMounted(() => {
  document.title = 'GoalStore | Bán giày bóng đá & phụ kiện thể thao'
  const preloader = document.getElementById('preloder')
  if (preloader) {
    preloader.style.display = 'none'
  }
  window.scrollTo({ top: 0 })
  loadHomeProducts()
})
</script>

<template>
  <div class="legacy-page">
    <div id="preloder">
      <div class="loader"></div>
    </div>

    <section class="hero">
      <div class="hero__slider">
        <div class="hero__items set-bg" :style="heroStyle">
          <div class="container">
            <div class="row">
              <div class="col-xl-6 col-lg-7 col-md-8">
                <div class="hero__text">
                  <h6>GoalStore Collection</h6>
                  <h2>Giày bóng đá & Phụ kiện thể thao</h2>
                  <p>
                    Chọn đúng đôi giày – nâng tầm trải nghiệm sân cỏ. Hàng chính hãng,
                    tư vấn nhanh, đổi trả dễ. 
                  </p>
                  <RouterLink to="/shop" class="primary-btn">
                    Mua ngay <span class="arrow_right"></span>
                  </RouterLink>
                  <div class="hero__social">
                    <a href="#"><i class="fa fa-facebook"></i></a>
                    <a href="#"><i class="fa fa-instagram"></i></a>
                    <a href="#"><i class="fa fa-youtube-play"></i></a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="banner spad home-banner-section">
      <div class="container">
        <div class="row home-banner-grid">
          <div class="col-lg-7">
            <div class="banner__item home-banner-card home-banner-card--large">
              <div class="banner__item__pic home-banner-card__pic">
                <img src="/legacy/img/soccer-ball-cleats.jpg" alt="Giày sân cỏ nhân tạo" />
              </div>
              <div class="banner__item__text home-banner-card__text home-banner-card__text--light">
                <span class="home-banner-card__eyebrow">Bộ sưu tập nổi bật</span>
                <h2>Quần áo đá bóng</h2>
                <RouterLink to="/shop">Xem sản phẩm</RouterLink>
              </div>
            </div>
          </div>

          <div class="col-lg-5">
            <div class="banner__item home-banner-card home-banner-card--small">
              <div class="banner__item__pic home-banner-card__pic">
                <img src="/legacy/img/football-composition-with-whiteboard.jpg" alt="Phụ kiện luyện tập" />
              </div>
              <div class="banner__item__text home-banner-card__text">
                <span class="home-banner-card__eyebrow">Dành cho tập luyện</span>
                <h2>Phụ kiện luyện tập</h2>
                <RouterLink to="/shop">Xem sản phẩm</RouterLink>
              </div>
            </div>
          </div>

        
        </div>
      </div>
    </section>

    <section class="product spad home-product-section">
      <div class="container">
        <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
        <div v-else-if="loading" class="text-center py-5">Đang tải sản phẩm...</div>

        <template v-else>
          <div class="row">
            <div class="col-lg-12">
              <div class="section-title">
                <span>Bán chạy nhất</span>
                <h2>Sản phẩm nổi bật</h2>
              </div>
            </div>
          </div>

          <div class="row product__filter">
            <div
              v-for="product in featuredProducts"
              :key="`featured-${product.id}`"
              class="col-lg-3 col-md-6 col-sm-6"
            >
              <div class="product__item product-card-home" :class="{ sale: hasSale(product) }">
                <div class="product__item__pic set-bg" :style="productCardStyle(product.imageUrl)">
                  <span v-if="hasSale(product)" class="label">Sale</span>
                  <span v-if="!isInStock(product)" class="home-stock-badge home-stock-badge--out">Hết hàng</span>
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
                  <RouterLink :to="`/shop-details/${product.id}`" class="add-cart">+ Xem chi tiết</RouterLink>
                  <div class="rating text-muted small">{{ product.brand || 'GoalStore' }}</div>
                  <h5>
                    <template v-if="hasSale(product)">
                      <span class="home-price-sale">{{ formatCurrency(product.salePrice) }}</span>
                      <span class="home-price-original">{{ formatCurrency(product.price || product.minPrice) }}</span>
                    </template>
                    <template v-else>
                      {{ formatPrice(product) }}
                    </template>
                  </h5>
                </div>
              </div>
            </div>

            <div v-if="!featuredProducts.length" class="col-12 text-center py-4">
              Chưa có sản phẩm nổi bật để hiển thị.
            </div>
          </div>

          <div class="row home-product-section__divider">
            <div class="col-lg-12">
              <div class="section-title">
                <span>Hàng mới cập nhật</span>
                <h2>Sản phẩm mới về</h2>
              </div>
            </div>
          </div>

          <div class="row product__filter">
            <div
              v-for="product in newArrivalProducts"
              :key="`new-${product.id}`"
              class="col-lg-3 col-md-6 col-sm-6"
            >
              <div class="product__item product-card-home" :class="{ sale: hasSale(product) }">
                <div class="product__item__pic set-bg" :style="productCardStyle(product.imageUrl)">
                  <span class="label label--new">Mới</span>
                  <span v-if="!isInStock(product)" class="home-stock-badge home-stock-badge--out">Hết hàng</span>
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
                  <RouterLink :to="`/shop-details/${product.id}`" class="add-cart">+ Xem chi tiết</RouterLink>
                  <div class="rating text-muted small">{{ product.brand || 'GoalStore' }}</div>
                  <h5>
                    <template v-if="hasSale(product)">
                      <span class="home-price-sale">{{ formatCurrency(product.salePrice) }}</span>
                      <span class="home-price-original">{{ formatCurrency(product.price || product.minPrice) }}</span>
                    </template>
                    <template v-else>
                      {{ formatPrice(product) }}
                    </template>
                  </h5>
                </div>
              </div>
            </div>

            <div v-if="!newArrivalProducts.length" class="col-12 text-center py-4">
              Chưa có sản phẩm mới về để hiển thị.
            </div>
          </div>

          <div class="row mt-4">
            <div class="col-lg-12 text-center">
              <RouterLink to="/shop" class="primary-btn">Xem tất cả sản phẩm</RouterLink>
            </div>
          </div>
        </template>
      </div>
    </section>

    <div class="search-model">
      <div class="h-100 d-flex align-items-center justify-content-center">
        <div class="search-close-switch">+</div>
        <form class="search-model-form">
          <input type="text" id="search-input" placeholder="Tìm kiếm sản phẩm..." />
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-product-section {
  padding-top: 0;
}

.home-product-section__divider {
  margin-top: 28px;
}

.label--new {
  background: #111111;
}

.product-card-home .product__item__pic {
  position: relative;
  background-repeat: no-repeat;
}

.home-stock-badge {
  position: absolute;
  left: 20px;
  bottom: 20px;
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  background: #2f2f2f;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.home-stock-badge--out {
  background: #999999;
}

.home-price-sale {
  color: #e53637;
  margin-right: 8px;
}

.home-price-original {
  color: #b7b7b7;
  text-decoration: line-through;
  font-size: 15px;
}

.home-banner-section {
  padding-top: 32px;
}

.home-banner-grid {
  row-gap: 24px;
}

.home-banner-card {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  margin-bottom: 0;
  background: #f6f6f6;
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.08);
}

.home-banner-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.55) 0%, rgba(0, 0, 0, 0.18) 42%, rgba(0, 0, 0, 0.05) 100%);
  pointer-events: none;
}

.home-banner-card--small::after {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.1) 35%, rgba(255, 255, 255, 0.88) 100%);
}

.home-banner-card__pic {
  height: 100%;
}

.home-banner-card--large {
  min-height: 420px;
}

.home-banner-card--small {
  min-height: 420px;
}

.home-banner-card--wide {
  min-height: 340px;
}

.home-banner-card__pic img {
  width: 100%;
  height: 100%;
  min-height: inherit;
  object-fit: cover;
  object-position: center;
  display: block;
}

.home-banner-card__text {
  position: absolute;
  left: 34px;
  bottom: 34px;
  z-index: 2;
  max-width: 320px;
  padding: 0;
}

.home-banner-card__text--light h2,
.home-banner-card__text--light a,
.home-banner-card__text--light .home-banner-card__eyebrow {
  color: #ffffff;
}

.home-banner-card__eyebrow {
  display: inline-block;
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: #111111;
}

.home-banner-card__text h2 {
  margin-bottom: 12px;
  font-size: 36px;
  line-height: 1.18;
}

.home-banner-card__text a {
  display: inline-block;
  padding-bottom: 6px;
  border-bottom: 2px solid currentColor;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

@media (max-width: 991.98px) {
  .home-banner-card--large,
  .home-banner-card--small,
  .home-banner-card--wide {
    min-height: 320px;
  }

  .home-banner-card__text {
    left: 24px;
    right: 24px;
    bottom: 24px;
    max-width: 280px;
  }

  .home-banner-card__text h2 {
    font-size: 30px;
  }
}

@media (max-width: 575.98px) {
  .home-banner-section {
    padding-top: 16px;
  }

  .home-banner-grid {
    row-gap: 16px;
  }

  .home-banner-card--large,
  .home-banner-card--small,
  .home-banner-card--wide {
    min-height: 260px;
  }

  .home-banner-card {
    border-radius: 14px;
  }

  .home-banner-card__text h2 {
    font-size: 24px;
  }

  .home-banner-card__eyebrow {
    font-size: 11px;
  }
}

</style>
