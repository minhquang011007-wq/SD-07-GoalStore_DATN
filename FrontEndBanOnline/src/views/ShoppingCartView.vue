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
            <div v-if="(cart.items || []).length" class="cart-select-bar">
              <label class="cart-check-label">
                <input type="checkbox" :checked="allChecked" @change="toggleAll($event)" />
                <span>Chọn tất cả ({{ selectedCount }}/{{ (cart.items || []).length }} sản phẩm)</span>
              </label>
            </div>

            <div class="shopping__cart__table">
              <table>
                <thead>
                  <tr>
                    <th>Chọn</th>
                    <th>Sản phẩm</th>
                    <th>Số lượng</th>
                    <th>Tổng</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cart.items || []" :key="item.itemId">
                    <td class="cart__check">
                      <input
                        type="checkbox"
                        :checked="isSelected(item.itemId)"
                        @change="toggleItem(item.itemId, $event)"
                      />
                    </td>
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
                    <td colspan="5" class="text-center py-4">Giỏ hàng của bạn đang trống.</td>
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
                <li>Đã chọn <span>{{ selectedCount }} sản phẩm</span></li>
                <li>Tạm tính đã chọn <span>{{ formatCurrency(selectedTotal) }}</span></li>
                <li>Tổng cộng <span>{{ formatCurrency(selectedTotal) }}</span></li>
              </ul>
              <a href="#" class="primary-btn" @click.prevent="goToCheckout">Tiến hành thanh toán</a>
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { clearCart, getCart, getSelectedCartItemIds, removeCartItem, resolveImageUrl, setSelectedCartItemIds, updateCartItem } from '@/shared/lib/shop.api'
import type { CartItem, CartResponse } from '@/shared/lib/shop.types'
import { getCustomerId, isCustomerLoggedIn } from '@/shared/lib/auth'

const router = useRouter()
const cart = ref<CartResponse>({ items: [], totalAmount: 0 })
const selectedItemIds = ref<number[]>([])
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

const cartItems = computed(() => cart.value.items || [])
const selectedItems = computed(() => cartItems.value.filter((item) => selectedItemIds.value.includes(Number(item.itemId))))
const selectedTotal = computed(() => selectedItems.value.reduce((sum, item) => sum + toNumber(item.lineTotal), 0))
const selectedCount = computed(() => selectedItems.value.length)
const allChecked = computed(() => cartItems.value.length > 0 && selectedCount.value === cartItems.value.length)

function variantText(item: CartItem) {
  const values = [item.color, item.size].filter(Boolean)
  return values.length ? values.join(' / ') : 'Biến thể mặc định'
}

function syncSelectionFromCart() {
  const availableIds = cartItems.value.map((item) => Number(item.itemId)).filter((value) => Number.isFinite(value))
  const next = getSelectedCartItemIds().filter((id) => availableIds.includes(id))
  selectedItemIds.value = next
  setSelectedCartItemIds(next)
}

function isSelected(itemId: number) {
  return selectedItemIds.value.includes(Number(itemId))
}

function toggleItem(itemId: number, event: Event) {
  const checked = Boolean((event.target as HTMLInputElement | null)?.checked)
  const id = Number(itemId)
  const next = checked
    ? Array.from(new Set([...selectedItemIds.value, id]))
    : selectedItemIds.value.filter((value) => value !== id)

  selectedItemIds.value = next
  setSelectedCartItemIds(next)
}

function toggleAll(event: Event) {
  const checked = Boolean((event.target as HTMLInputElement | null)?.checked)
  const next = checked ? cartItems.value.map((item) => Number(item.itemId)) : []
  selectedItemIds.value = next
  setSelectedCartItemIds(next)
}

async function loadCartData() {
  error.value = ''
  message.value = ''
  loggedIn.value = isCustomerLoggedIn()

  if (!loggedIn.value) {
    cart.value = { items: [], totalAmount: 0 }
    selectedItemIds.value = []
    setSelectedCartItemIds([])
    return
  }

  const customerId = getCustomerId()
  if (!customerId) {
    error.value = 'Không tìm thấy mã khách hàng để lấy giỏ hàng.'
    return
  }

  try {
    cart.value = await getCart(customerId)
    syncSelectionFromCart()
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
    syncSelectionFromCart()
    message.value = 'Đã cập nhật số lượng sản phẩm.'
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể cập nhật số lượng'
  }
}

async function removeItemFromCart(itemId: number) {
  try {
    cart.value = await removeCartItem(itemId)
    syncSelectionFromCart()
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
    selectedItemIds.value = []
    setSelectedCartItemIds([])
    message.value = 'Đã xóa toàn bộ giỏ hàng.'
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể xóa toàn bộ giỏ hàng'
  }
}

function goToCheckout() {
  if (!selectedCount.value) {
    error.value = 'Hãy tích chọn ít nhất 1 sản phẩm để thanh toán.'
    return
  }

  setSelectedCartItemIds(selectedItemIds.value)
  router.push('/checkout')
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
.cart-select-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.cart-check-label {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #111;
}

.cart__check {
  width: 72px;
  text-align: center;
}

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
