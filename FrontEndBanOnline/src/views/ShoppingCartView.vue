<template>
    <div class="legacy-page">
      <div id="preloder"><div class="loader"></div></div>

  
      <section class="breadcrumb-option">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="breadcrumb__text">
                <h4>Giỏ hàng</h4>
                <div class="breadcrumb__links">
                  <a href="/">Trang chủ</a>
                  <a href="/shop">Sản phẩm</a>
                  <span>Giỏ hàng</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
  
      <section class="shopping-cart spad">
        <div class="container">
          <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
          <div v-if="message" class="alert alert-success mb-4">{{ message }}</div>
  
          <div v-if="!loggedIn" class="text-center py-5 border rounded">
            Bạn cần đăng nhập tài khoản khách hàng để xem giỏ hàng.
            <div class="mt-3"><a href="/login" class="primary-btn">Đăng nhập ngay</a></div>
          </div>
  
          <div v-else class="row">
            <div class="col-lg-8">
              <div class="shopping__cart__table">
                <table>
                  <thead>
                    <tr>
                      <th>Sản phẩm</th>
                      <th>Số lượng</th>
                      <th>Tổng</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in cart.items || []" :key="item.itemId">
                      <td class="product__cart__item">
                        <div class="product__cart__item__pic">
                          <img :src="resolveImageUrl(item.imageUrl) || '/legacy/img/shopping-cart/cart-1.jpg'" alt="cart-item" />
                        </div>
                        <div class="product__cart__item__text">
                          <h6>{{ item.productName || 'Sản phẩm' }}</h6>
                          <p class="mb-1 text-muted">{{ variantText(item) }}</p>
                          <h5>{{ formatCurrency(item.unitPrice) }}</h5>
                        </div>
                      </td>
                      <td class="quantity__item">
                        <div class="quantity">
                          <div class="pro-qty-2 cart-qty-inline">
                            <button type="button" class="qtybtn" @click="changeItemQuantity(item, -1)">-</button>
                            <input :value="item.quantity" type="text" readonly />
                            <button type="button" class="qtybtn" @click="changeItemQuantity(item, 1)">+</button>
                          </div>
                        </div>
                      </td>
                      <td class="cart__price">{{ formatCurrency(item.lineTotal) }}</td>
                      <td class="cart__close"><i class="fa fa-close" @click="removeItemFromCart(item.itemId)"></i></td>
                    </tr>
  
                    <tr v-if="!(cart.items || []).length">
                      <td colspan="4" class="text-center py-4">Giỏ hàng của bạn đang trống.</td>
                    </tr>
                  </tbody>
                </table>
              </div>
  
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6">
                  <div class="continue__btn"><a href="/shop">Tiếp tục mua sắm</a></div>
                </div>
                <div class="col-lg-6 col-md-6 col-sm-6">
                  <div class="continue__btn update__btn"><a href="#" @click.prevent="clearAllCart"><i class="fa fa-trash"></i> Xóa toàn bộ</a></div>
                </div>
              </div>
            </div>
  
            <div class="col-lg-4">
              <div class="cart__discount">
                <h6>Ghi chú</h6>
                <form action="#">
                  <input type="text" placeholder="Mã giảm giá sẽ làm sau" disabled />
                  <button type="button" disabled>Áp dụng</button>
                </form>
              </div>
  
              <div class="cart__total">
                <h6>Tổng giỏ hàng</h6>
                <ul>
                  <li>Tạm tính <span>{{ formatCurrency(cartTotal) }}</span></li>
                  <li>Tổng cộng <span>{{ formatCurrency(cartTotal) }}</span></li>
                </ul>
                <a href="/checkout" class="primary-btn">Tiến hành thanh toán</a>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { clearCart, getCart, removeCartItem, resolveImageUrl, updateCartItem } from '@/shared/lib/shop.api'
  import type { CartItem, CartResponse } from '@/shared/lib/shop.types'
  import { getCustomerId, isCustomerLoggedIn } from '@/shared/lib/auth'
  
  const cart = ref<CartResponse>({ items: [], totalAmount: 0 })
  const error = ref('')
  const message = ref('')
  const loggedIn = ref(false)
  
  function toNumber(value: unknown) {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : 0
  }
  
  function formatCurrency(value: unknown) {
    return `${toNumber(value).toLocaleString('vi-VN')}₫`
  }
  
  const cartTotal = computed(() => toNumber(cart.value.totalAmount))
  const cartCount = computed(() => (cart.value.items || []).reduce((sum, item) => sum + toNumber(item.quantity), 0))
  
  function variantText(item: CartItem) {
    const values = [item.color, item.size].filter(Boolean)
    return values.length ? values.join(' / ') : 'Biến thể mặc định'
  }
  
  async function loadCartData() {
    error.value = ''
    message.value = ''
    loggedIn.value = isCustomerLoggedIn()
  
    if (!loggedIn.value) {
      cart.value = { items: [], totalAmount: 0 }
      return
    }
  
    const customerId = getCustomerId()
    if (!customerId) {
      error.value = 'Không tìm thấy mã khách hàng để lấy giỏ hàng.'
      return
    }
  
    try {
      cart.value = await getCart(customerId)
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể tải giỏ hàng'
    }
  }
  
  async function changeItemQuantity(item: CartItem, delta: number) {
    const customerId = getCustomerId()
    if (!customerId) return
  
    const nextQuantity = Math.max(1, toNumber(item.quantity) + delta)
    try {
      cart.value = await updateCartItem(item.itemId, {
        customerId,
        variantId: item.variantId,
        quantity: nextQuantity,
      })
      message.value = 'Đã cập nhật số lượng sản phẩm.'
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể cập nhật số lượng'
    }
  }
  
  async function removeItemFromCart(itemId: number) {
    try {
      cart.value = await removeCartItem(itemId)
      message.value = 'Đã xóa sản phẩm khỏi giỏ hàng.'
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể xóa sản phẩm khỏi giỏ hàng'
    }
  }
  
  async function clearAllCart() {
    const customerId = getCustomerId()
    if (!customerId) return
    try {
      cart.value = await clearCart(customerId)
      message.value = 'Đã xóa toàn bộ giỏ hàng.'
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể xóa toàn bộ giỏ hàng'
    }
  }
  
  onMounted(async () => {
    document.title = 'GoalStore | Giỏ hàng'
    const preloader = document.getElementById('preloder')
    if (preloader) preloader.style.display = 'none'
    window.scrollTo({ top: 0 })
    await loadCartData()
  })
  </script>
  
  <style scoped>
  .cart-qty-inline {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .cart-qty-inline .qtybtn {
    width: 32px;
    height: 32px;
    border: 1px solid #ddd;
    background: #fff;
  }
  
  .cart-qty-inline input {
    width: 60px;
    text-align: center;
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
  