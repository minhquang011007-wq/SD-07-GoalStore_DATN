<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"
import { clearSession, getCustomerId, getDisplayName, isCustomerLoggedIn } from "@/shared/lib/auth"
import { getCart } from "@/shared/lib/shop.api"

const route = useRoute()
const router = useRouter()

const loggedIn = ref(false)
const displayName = ref("Khách hàng")
const cartCount = ref(0)
const cartTotal = ref(0)

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatCurrency(value: unknown) {
  return `${toNumber(value).toLocaleString("vi-VN")}₫`
}

async function refreshHeaderState() {
  loggedIn.value = isCustomerLoggedIn()
  displayName.value = getDisplayName()

  const customerId = getCustomerId()
  if (!loggedIn.value || customerId === null || customerId === undefined) {
    cartCount.value = 0
    cartTotal.value = 0
    return
  }

  try {
    const cart = await getCart(customerId)
    cartCount.value = (cart.items || []).reduce((sum, item) => sum + toNumber(item.quantity), 0)
    cartTotal.value = toNumber(cart.totalAmount)
  } catch {
    cartCount.value = 0
    cartTotal.value = 0
  }
}

function isActive(type: "home" | "shop" | "contact" | "cart" | "orders") {
  const path = route.path
  if (type === "home") return path === "/"
  if (type === "shop") return path === "/shop" || path.startsWith("/shop-details")
  if (type === "contact") return path === "/contact"
  if (type === "orders") return path === "/orders"
  return path === "/cart" || path === "/checkout"
}

async function logout() {
  clearSession()
  await router.push("/login")
}

function handleStorageChange() {
  refreshHeaderState()
}

function handleCartUpdated() {
  refreshHeaderState()
}

watch(() => route.fullPath, () => {
  refreshHeaderState()
}, { immediate: true })

onMounted(() => {
  window.addEventListener("storage", handleStorageChange)
  window.addEventListener("goalstore:cart-updated", handleCartUpdated as EventListener)
})

onUnmounted(() => {
  window.removeEventListener("storage", handleStorageChange)
  window.removeEventListener("goalstore:cart-updated", handleCartUpdated as EventListener)
})
</script>

<template>
    <div class="offcanvas-menu-overlay"></div>
    <div class="offcanvas-menu-wrapper">
      <div class="offcanvas__option">
        <div class="offcanvas__links">
          <template v-if="loggedIn">
            <a href="#">{{ displayName }}</a>
            <RouterLink to="/orders">Đơn hàng</RouterLink>
            <a href="#" @click.prevent="logout">Đăng xuất</a>
          </template>
          <template v-else>
            <RouterLink to="/login">Đăng nhập</RouterLink>
          </template>
          <RouterLink to="/contact">Hỗ trợ</RouterLink>
        </div>
        <div class="offcanvas__top__hover">
          <span>VNĐ <i class="arrow_carrot-down"></i></span>
          <ul>
            <li>VNĐ</li>
          </ul>
        </div>
      </div>
      <div class="offcanvas__nav__option">
        <RouterLink to="/shop"><img src="/legacy/img/icon/search.png" alt="search" /></RouterLink>
        <RouterLink to="/cart"><img src="/legacy/img/icon/cart.png" alt="cart" /> <span>{{ cartCount }}</span></RouterLink>
        <div class="price">{{ formatCurrency(cartTotal) }}</div>
      </div>
      <div id="mobile-menu-wrap"></div>
      <div class="offcanvas__text">
        <p>GoalStore - Giày bóng đá chính hãng | Đổi trả 7 ngày | Giao nhanh toàn quốc</p>
      </div>
    </div>

    <header class="header">
      <div class="header__top">
        <div class="container">
          <div class="row">
            <div class="col-lg-6 col-md-7">
              <div class="header__top__left">
                <p>GoalStore - Giày bóng đá chính hãng | Đổi trả 7 ngày | Giao nhanh toàn quốc</p>
              </div>
            </div>
            <div class="col-lg-6 col-md-5">
              <div class="header__top__right">
                <div class="header__top__links">
                  <template v-if="loggedIn">
                    <a href="#">{{ displayName }}</a>
                    <RouterLink to="/orders">Đơn hàng</RouterLink>
                    <a href="#" @click.prevent="logout">Đăng xuất</a>
                  </template>
                  <template v-else>
                    <RouterLink to="/login">Đăng nhập</RouterLink>
                  </template>
                  <RouterLink to="/contact">Hỗ trợ</RouterLink>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="container">
        <div class="row">
          <div class="col-lg-3 col-md-3">
            <div class="header__logo">
              <RouterLink to="/" class="gs-logo">GoalStore</RouterLink>
            </div>
          </div>

          <div class="col-lg-6 col-md-6">
            <nav class="header__menu mobile-menu">
              <ul>
                <li :class="{ active: isActive('home') }"><RouterLink to="/">Trang chủ</RouterLink></li>
                <li :class="{ active: isActive('shop') }"><RouterLink to="/shop">Sản phẩm</RouterLink></li>
                <li :class="{ active: isActive('contact') }"><RouterLink to="/contact">Liên hệ</RouterLink></li>
                <li :class="{ active: isActive('orders') }"><RouterLink to="/orders">Đơn hàng</RouterLink></li>
                <li :class="{ active: isActive('cart') }"><RouterLink to="/cart">Giỏ hàng</RouterLink></li>
              </ul>
            </nav>
          </div>

          <div class="col-lg-3 col-md-3">
            <div class="header__nav__option">
              <RouterLink to="/shop"><img src="/legacy/img/icon/search.png" alt="search" /></RouterLink>
              <RouterLink to="/cart"><img src="/legacy/img/icon/cart.png" alt="cart" /> <span>{{ cartCount }}</span></RouterLink>
              <!-- <div class="price">{{ formatCurrency(cartTotal) }}</div> -->
            </div>
          </div>
        </div>
        <div class="canvas__open"><i class="fa fa-bars"></i></div>
      </div>
    </header>
</template>
