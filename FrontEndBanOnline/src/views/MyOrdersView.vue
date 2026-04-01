<template>
  <div class="legacy-page my-orders-page">
    <div id="preloder"><div class="loader"></div></div>

    <section class="breadcrumb-option">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="breadcrumb__text">
              <h4>Theo dõi đơn hàng</h4>
              <div class="breadcrumb__links">
                <RouterLink to="/">Trang chủ</RouterLink>
                <span>Đơn hàng của tôi</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="spad">
      <div class="container">
        <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
        <div v-if="message" class="alert alert-success mb-4">{{ message }}</div>

        <div v-if="!loggedIn" class="orders-empty-state">
          <h4>Bạn cần đăng nhập để xem đơn hàng</h4>
          <p>Đăng nhập tài khoản khách hàng để theo dõi trạng thái đơn, hủy đơn hoặc gửi yêu cầu trả hàng.</p>
          <RouterLink to="/login" class="primary-btn">Đăng nhập ngay</RouterLink>
        </div>

        <div v-else-if="loading" class="orders-empty-state">
          <h4>Đang tải danh sách đơn hàng...</h4>
        </div>

        <div v-else-if="!filteredOrders.length" class="orders-empty-state">
          <h4>Chưa có đơn hàng nào</h4>
          <p>Bạn chưa đặt đơn nào tại GoalStore. Hãy chọn sản phẩm và hoàn tất đơn đầu tiên nhé.</p>
          <RouterLink to="/shop" class="primary-btn">Mua sắm ngay</RouterLink>
        </div>

        <div v-else class="row g-4">
          <div class="col-lg-5">
            <div class="orders-panel">
              <div class="orders-panel__head">
                <div>
                  <p class="orders-panel__eyebrow">Khách hàng</p>
                  <h5>{{ displayName }}</h5>
                </div>
                <RouterLink to="/shop" class="orders-panel__action">Mua thêm</RouterLink>
              </div>

              <div class="order-filter-list">
                <button
                  v-for="item in filterOptions"
                  :key="item.value"
                  type="button"
                  class="order-filter-chip"
                  :class="{ active: activeFilter === item.value }"
                  @click="activeFilter = item.value"
                >
                  {{ item.label }}
                  <span>{{ countByStatus(item.value) }}</span>
                </button>
              </div>

              <div class="orders-list">
                <button
                  v-for="order in filteredOrders"
                  :key="String(order.id ?? '')"
                  type="button"
                  class="order-card"
                  :class="{ active: selectedOrder?.id === order.id }"
                  @click="selectOrder(order)"
                >
                  <div class="order-card__top">
                    <div>
                      <p>{{ order.code || `#${order.id}` }}</p>
                      <strong>{{ formatDate(order.orderDate) }}</strong>
                    </div>
                    <span class="status-pill" :class="statusClass(order.status)">{{ formatStatus(order.status) }}</span>
                  </div>

                  <div class="order-card__meta">
                    <span>{{ order.items?.length || 0 }} sản phẩm</span>
                    <span>{{ formatPaymentMethod(order.paymentMethod) }}</span>
                  </div>

                  <div class="order-card__bottom">
                    <div>
                      <small>Người nhận</small>
                      <strong>{{ order.receiverName || order.customerName || 'Khách hàng' }}</strong>
                    </div>
                    <div class="text-right">
                      <small>Tổng tiền</small>
                      <strong>{{ formatCurrency(order.total) }}</strong>
                    </div>
                  </div>
                </button>
              </div>
            </div>
          </div>

          <div class="col-lg-7">
            <div v-if="selectedOrder" class="order-detail-card">
              <div class="order-detail-head">
                <div>
                  <p class="orders-panel__eyebrow">Chi tiết đơn hàng</p>
                  <h3>{{ selectedOrder.code || `#${selectedOrder.id}` }}</h3>
                  <span>Đặt lúc {{ formatDateTime(selectedOrder.orderDate) }}</span>
                </div>
                <span class="status-pill status-pill--lg" :class="statusClass(selectedOrder.status)">
                  {{ formatStatus(selectedOrder.status) }}
                </span>
              </div>

              <div class="order-progress-grid">
                <div v-for="step in progressSteps" :key="step.key" class="order-progress-step" :class="progressClass(step.key)">
                  <div class="order-progress-step__dot"></div>
                  <strong>{{ step.label }}</strong>
                  <p>{{ step.description }}</p>
                </div>
              </div>

              <div class="order-summary-grid">
                <div class="order-summary-box">
                  <span>Thanh toán</span>
                  <strong>{{ formatPaymentMethod(selectedOrder.paymentMethod) }}</strong>
                  <p>Trạng thái: {{ formatPaymentStatus(selectedOrder.paymentStatus) }}</p>
                </div>
                <div class="order-summary-box">
                  <span>Người nhận</span>
                  <strong>{{ selectedOrder.receiverName || 'Chưa cập nhật' }}</strong>
                  <p>{{ selectedOrder.receiverPhone || 'Chưa có số điện thoại' }}</p>
                </div>
                <div class="order-summary-box order-summary-box--full">
                  <span>Địa chỉ giao hàng</span>
                  <strong>{{ selectedOrder.shippingAddress || 'Chưa có địa chỉ' }}</strong>
                  <p v-if="selectedOrder.note">Ghi chú: {{ selectedOrder.note }}</p>
                </div>
              </div>

              <div class="order-items-section">
                <div class="section-head">
                  <h5>Sản phẩm trong đơn</h5>
                  <span>{{ selectedOrder.items?.length || 0 }} sản phẩm</span>
                </div>

                <div v-for="item in selectedOrder.items || []" :key="`${selectedOrder.id}-${item.itemId}-${item.variantId}`" class="order-item-row">
                  <img :src="resolveImageUrl(item.imageUrl) || '/legacy/img/shopping-cart/cart-1.jpg'" :alt="item.productName || 'Sản phẩm'" />
                  <div>
                    <h6>{{ item.productName || item.sku || 'Sản phẩm' }}</h6>
                    <p>{{ variantText(item) }}</p>
                    <span>SKU: {{ item.sku || '---' }}</span>
                  </div>
                  <div class="text-right">
                    <p>{{ item.quantity || 0 }} x {{ formatCurrency(item.unitPrice) }}</p>
                    <strong>{{ formatCurrency(item.lineTotal) }}</strong>
                  </div>
                </div>
              </div>

              <div class="order-total-box">
                <div><span>Tạm tính</span><strong>{{ formatCurrency(selectedOrder.subtotal) }}</strong></div>
                <div><span>Phí vận chuyển</span><strong>{{ formatCurrency(selectedOrder.shippingFee) }}</strong></div>
                <div><span>Giảm giá</span><strong>- {{ formatCurrency(selectedOrder.discountAmount) }}</strong></div>
                <div class="order-total-box__grand"><span>Tổng thanh toán</span><strong>{{ formatCurrency(selectedOrder.total) }}</strong></div>
              </div>

              <div class="order-actions">
                <button
                  v-if="canCancel(selectedOrder)"
                  type="button"
                  class="secondary-btn"
                  :disabled="actionLoading"
                  @click="handleCancelOrder"
                >
                  {{ actionLoading ? 'Đang xử lý...' : 'Hủy đơn hàng' }}
                </button>

                <button
                  v-if="canReturn(selectedOrder)"
                  type="button"
                  class="primary-btn outline-btn"
                  :disabled="actionLoading"
                  @click="toggleReturnForm"
                >
                  {{ showReturnForm ? 'Đóng yêu cầu trả hàng' : 'Yêu cầu trả hàng' }}
                </button>

                <RouterLink to="/shop" class="primary-btn">Mua lại</RouterLink>
              </div>

              <div v-if="showReturnForm && canReturn(selectedOrder)" class="return-request-box">
                <h5>Tạo yêu cầu trả hàng cơ bản</h5>
                <p>Chỉ dùng cho đơn đã hoàn tất. Sau khi gửi, trạng thái đơn sẽ chuyển sang <strong>Trả hàng</strong>.</p>
                <div class="checkout__input">
                  <p>Lý do trả hàng<span>*</span></p>
                  <input v-model="returnForm.reason" type="text" placeholder="Ví dụ: Sai size, giao nhầm màu, lỗi sản phẩm..." />
                </div>
                <div class="checkout__input">
                  <p>Ghi chú thêm</p>
                  <textarea v-model="returnForm.note" rows="4" placeholder="Mô tả thêm tình trạng sản phẩm hoặc yêu cầu hoàn tiền"></textarea>
                </div>
                <button type="button" class="site-btn" :disabled="actionLoading" @click="handleCreateReturn">
                  {{ actionLoading ? 'Đang gửi yêu cầu...' : 'Gửi yêu cầu trả hàng' }}
                </button>
              </div>

              <div v-if="lastReturnInfo && Number(lastReturnInfo.orderId) === Number(selectedOrder.id)" class="return-result-box">
                <h6>Yêu cầu trả hàng gần nhất</h6>
                <p><strong>Lý do:</strong> {{ lastReturnInfo.reason || '---' }}</p>
                <p v-if="lastReturnInfo.note"><strong>Ghi chú:</strong> {{ lastReturnInfo.note }}</p>
                <p><strong>Số tiền hoàn dự kiến:</strong> {{ formatCurrency(lastReturnInfo.refundTotal) }}</p>
                <p><strong>Thời gian gửi:</strong> {{ formatDateTime(lastReturnInfo.returnDate) }}</p>
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
import { RouterLink } from 'vue-router'
import { getCustomerId, getDisplayName, isCustomerLoggedIn } from '@/shared/lib/auth'
import {
  cancelCustomerOrder,
  createOrderReturn,
  getCustomerOrders,
  getOrderDetail,
  resolveImageUrl,
} from '@/shared/lib/shop.api'
import type { OrderItemSummary, OrderResponse, ReturnResponse } from '@/shared/lib/shop.types'

const loading = ref(true)
const actionLoading = ref(false)
const loggedIn = ref(false)
const displayName = ref('Khách hàng')
const error = ref('')
const message = ref('')
const orders = ref<OrderResponse[]>([])
const selectedOrder = ref<OrderResponse | null>(null)
const activeFilter = ref<'ALL' | 'MOI' | 'DANG_XU_LY' | 'DANG_GIAO' | 'HOAN_TAT' | 'HUY' | 'TRA_HANG'>('ALL')
const showReturnForm = ref(false)
const lastReturnInfo = ref<ReturnResponse | null>(null)
const returnForm = ref({ reason: '', note: '' })

const filterOptions = [
  { value: 'ALL' as const, label: 'Tất cả' },
  { value: 'MOI' as const, label: 'Mới' },
  { value: 'DANG_XU_LY' as const, label: 'Đang xử lý' },
  { value: 'DANG_GIAO' as const, label: 'Đang giao' },
  { value: 'HOAN_TAT' as const, label: 'Hoàn tất' },
  { value: 'TRA_HANG' as const, label: 'Trả hàng' },
  { value: 'HUY' as const, label: 'Đã hủy' },
]

const progressSteps = [
  { key: 'MOI', label: 'Đơn mới', description: 'GoalStore đã ghi nhận đơn hàng của bạn.' },
  { key: 'DANG_XU_LY', label: 'Đang xử lý', description: 'Đơn đang được xác nhận và chuẩn bị hàng.' },
  { key: 'DANG_GIAO', label: 'Đang giao', description: 'Đơn đã bàn giao cho vận chuyển.' },
  { key: 'HOAN_TAT', label: 'Hoàn tất', description: 'Khách đã nhận hàng thành công.' },
]

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatCurrency(value: unknown) {
  return `${toNumber(value).toLocaleString('vi-VN')}₫`
}

function formatDate(value?: string | null) {
  if (!value) return '--/--/----'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--/--/----'
  return date.toLocaleDateString('vi-VN')
}

function formatDateTime(value?: string | null) {
  if (!value) return '--/--/----'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--/--/----'
  return `${date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })} - ${date.toLocaleDateString('vi-VN')}`
}

function formatStatus(value?: string | null) {
  const map: Record<string, string> = {
    MOI: 'Mới',
    DANG_XU_LY: 'Đang xử lý',
    DANG_GIAO: 'Đang giao',
    HOAN_TAT: 'Hoàn tất',
    HUY: 'Đã hủy',
    TRA_HANG: 'Trả hàng',
  }
  return value ? map[value] || value : 'Chưa xác định'
}

function formatPaymentMethod(value?: string | null) {
  const map: Record<string, string> = {
    COD: 'Thanh toán khi nhận hàng',
    BANKING: 'Chuyển khoản',
    MOMO: 'Ví MoMo',
    VNPAY: 'VNPay',
  }
  return value ? map[value] || value : 'Chưa xác định'
}

function formatPaymentStatus(value?: string | null) {
  const map: Record<string, string> = {
    UNPAID: 'Chưa thanh toán',
    PAID: 'Đã thanh toán',
    REFUNDED: 'Đã hoàn tiền',
  }
  return value ? map[value] || value : 'Chưa xác định'
}

function variantText(item: OrderItemSummary) {
  const values = [item.color, item.size].filter(Boolean)
  return values.length ? values.join(' / ') : 'Biến thể mặc định'
}

function statusClass(status?: string | null) {
  const value = String(status || '').toLowerCase()
  return {
    'status-pill--new': value === 'moi',
    'status-pill--processing': value === 'dang_xu_ly',
    'status-pill--shipping': value === 'dang_giao',
    'status-pill--done': value === 'hoan_tat',
    'status-pill--cancel': value === 'huy',
    'status-pill--return': value === 'tra_hang',
  }
}

function progressClass(key: string) {
  const status = selectedOrder.value?.status
  const orderIndex = ['MOI', 'DANG_XU_LY', 'DANG_GIAO', 'HOAN_TAT']
  const currentIndex = orderIndex.indexOf(String(status || ''))
  const stepIndex = orderIndex.indexOf(key)
  return {
    active: currentIndex >= stepIndex && currentIndex >= 0,
    cancel: status === 'HUY',
    returned: status === 'TRA_HANG',
  }
}

function countByStatus(status: typeof activeFilter.value) {
  if (status === 'ALL') return orders.value.length
  return orders.value.filter((item) => item.status === status).length
}

const filteredOrders = computed(() => {
  if (activeFilter.value === 'ALL') return orders.value
  return orders.value.filter((item) => item.status === activeFilter.value)
})

watch(filteredOrders, (value) => {
  if (!value.length) {
    selectedOrder.value = null
    return
  }
  if (!selectedOrder.value || !value.some((item) => Number(item.id) === Number(selectedOrder.value?.id))) {
    selectedOrder.value = value[0]
  }
}, { immediate: true })

watch(selectedOrder, () => {
  showReturnForm.value = false
  returnForm.value = { reason: '', note: '' }
})

function canCancel(order: OrderResponse) {
  return order.status === 'MOI' || order.status === 'DANG_XU_LY'
}

function canReturn(order: OrderResponse) {
  return order.status === 'HOAN_TAT'
}

async function loadOrders() {
  loading.value = true
  error.value = ''
  message.value = ''
  loggedIn.value = isCustomerLoggedIn()
  displayName.value = getDisplayName()

  if (!loggedIn.value) {
    loading.value = false
    orders.value = []
    selectedOrder.value = null
    return
  }

  const customerId = Number(getCustomerId())
  if (!customerId) {
    error.value = 'Không tìm thấy mã khách hàng để lấy đơn hàng.'
    loading.value = false
    return
  }

  try {
    const response = await getCustomerOrders(customerId)
    orders.value = response
    if (response.length) {
      selectedOrder.value = await getOrderDetail(Number(response[0].id))
    }
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể tải danh sách đơn hàng.'
  } finally {
    loading.value = false
  }
}

async function selectOrder(order: OrderResponse) {
  if (!order.id) return
  error.value = ''
  message.value = ''
  try {
    selectedOrder.value = await getOrderDetail(Number(order.id))
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể tải chi tiết đơn hàng.'
  }
}

async function handleCancelOrder() {
  if (!selectedOrder.value?.id) return
  error.value = ''
  message.value = ''
  actionLoading.value = true
  try {
    const updated = await cancelCustomerOrder(Number(selectedOrder.value.id))
    selectedOrder.value = updated
    orders.value = orders.value.map((item) => Number(item.id) === Number(updated.id) ? updated : item)
    message.value = 'Đã hủy đơn hàng và cập nhật trạng thái thành công.'
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể hủy đơn hàng.'
  } finally {
    actionLoading.value = false
  }
}

function toggleReturnForm() {
  showReturnForm.value = !showReturnForm.value
}

async function handleCreateReturn() {
  if (!selectedOrder.value?.id) return
  error.value = ''
  message.value = ''

  if (!returnForm.value.reason.trim()) {
    error.value = 'Vui lòng nhập lý do trả hàng.'
    return
  }

  actionLoading.value = true
  try {
    const created = await createOrderReturn(Number(selectedOrder.value.id), {
      reason: returnForm.value.reason,
      note: returnForm.value.note,
    })
    lastReturnInfo.value = created
    const refreshed = await getOrderDetail(Number(selectedOrder.value.id))
    selectedOrder.value = refreshed
    orders.value = orders.value.map((item) => Number(item.id) === Number(refreshed.id) ? refreshed : item)
    message.value = 'Đã gửi yêu cầu trả hàng. GoalStore sẽ kiểm tra và liên hệ lại sớm.'
    showReturnForm.value = false
    returnForm.value = { reason: '', note: '' }
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể tạo yêu cầu trả hàng.'
  } finally {
    actionLoading.value = false
  }
}

onMounted(async () => {
  document.title = 'GoalStore | Đơn hàng của tôi'
  const preloader = document.getElementById('preloder')
  if (preloader) preloader.style.display = 'none'
  window.scrollTo({ top: 0 })
  await loadOrders()
})
</script>

<style scoped>
.my-orders-page {
  background: #f8f5f0;
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

.orders-empty-state,
.orders-panel,
.order-detail-card {
  background: #fff;
  border: 1px solid #efefef;
}

.orders-empty-state {
  padding: 40px 28px;
  text-align: center;
}

.orders-empty-state p {
  color: #666;
  margin: 10px 0 20px;
}

.orders-panel,
.order-detail-card {
  padding: 24px;
}

.orders-panel__head,
.order-detail-head,
.section-head,
.order-card__top,
.order-card__bottom,
.order-actions,
.order-total-box > div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.orders-panel__eyebrow {
  text-transform: uppercase;
  letter-spacing: .18em;
  color: #888;
  font-size: 11px;
  margin-bottom: 6px;
}

.orders-panel__head h5,
.order-detail-head h3,
.section-head h5 {
  margin-bottom: 0;
}

.orders-panel__action {
  font-weight: 600;
  color: #111;
}

.order-filter-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 18px 0 20px;
}

.order-filter-chip {
  border: 1px solid #dcdcdc;
  background: #fff;
  padding: 10px 14px;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.order-filter-chip span {
  min-width: 22px;
  height: 22px;
  border-radius: 999px;
  background: #f2f2f2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.order-filter-chip.active {
  border-color: #111;
  background: #111;
  color: #fff;
}

.order-filter-chip.active span {
  background: rgba(255,255,255,.18);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 920px;
  overflow: auto;
}

.order-card {
  width: 100%;
  text-align: left;
  border: 1px solid #e8e8e8;
  background: #fff;
  padding: 16px;
}

.order-card.active {
  border-color: #111;
  box-shadow: 0 8px 26px rgba(0,0,0,.07);
}

.order-card__top p,
.order-card__meta,
.order-card__bottom small,
.order-detail-head span,
.order-summary-box p,
.order-progress-step p,
.order-item-row p,
.order-item-row span,
.return-request-box p,
.return-result-box p {
  color: #666;
}

.order-card__top p,
.order-card__bottom strong,
.order-summary-box strong,
.order-item-row h6 {
  margin-bottom: 4px;
}

.order-card__meta,
.order-progress-grid,
.order-summary-grid {
  display: grid;
  gap: 12px;
}

.order-card__meta {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin: 12px 0;
  font-size: 13px;
}

.order-summary-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin: 24px 0;
}

.order-summary-box,
.return-request-box,
.return-result-box {
  border: 1px solid #ececec;
  background: #fafafa;
  padding: 18px;
}

.order-summary-box--full {
  grid-column: 1 / -1;
}

.order-progress-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin: 24px 0;
}

.order-progress-step {
  position: relative;
  padding: 18px 12px;
  border: 1px dashed #d8d8d8;
  background: #fcfcfc;
}

.order-progress-step__dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #d0d0d0;
  margin-bottom: 12px;
}

.order-progress-step.active {
  border-color: #111;
  background: #fff;
}

.order-progress-step.active .order-progress-step__dot {
  background: #111;
}

.order-progress-step.cancel,
.order-progress-step.returned {
  opacity: .55;
}

.order-items-section {
  margin-bottom: 24px;
}

.order-item-row {
  display: grid;
  grid-template-columns: 72px 1fr auto;
  gap: 14px;
  align-items: center;
  border-top: 1px solid #f1f1f1;
  padding: 16px 0;
}

.order-item-row img {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border: 1px solid #eee;
}

.order-total-box {
  border-top: 1px solid #ededed;
  padding-top: 18px;
}

.order-total-box > div {
  padding: 6px 0;
}

.order-total-box__grand {
  font-size: 18px;
  border-top: 1px solid #efefef;
  margin-top: 10px;
  padding-top: 14px !important;
}

.order-actions {
  flex-wrap: wrap;
  justify-content: flex-start;
  margin-top: 24px;
}

.primary-btn.outline-btn,
.secondary-btn {
  min-width: 180px;
  text-align: center;
}

.secondary-btn {
  display: inline-block;
  padding: 12px 24px;
  background: #fff;
  color: #111;
  border: 1px solid #111;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.status-pill--lg {
  font-size: 13px;
  padding: 10px 14px;
}

.status-pill--new {
  background: #edf2ff;
  color: #3145c5;
}

.status-pill--processing {
  background: #fff7e6;
  color: #b7791f;
}

.status-pill--shipping {
  background: #e6fffb;
  color: #0f766e;
}

.status-pill--done {
  background: #ecfdf3;
  color: #15803d;
}

.status-pill--cancel {
  background: #fff1f2;
  color: #be123c;
}

.status-pill--return {
  background: #f5f3ff;
  color: #6d28d9;
}

.checkout__input input,
.checkout__input textarea {
  width: 100%;
  border: 1px solid #d7d7d7;
  padding: 12px 14px;
  background: #fff;
}

.checkout__input textarea {
  min-height: 110px;
  resize: vertical;
}

.return-request-box {
  margin-top: 18px;
}

.return-result-box {
  margin-top: 16px;
  background: #f7fbf8;
  border-color: #d9ebe0;
}

@media (max-width: 991px) {
  .order-progress-grid,
  .order-summary-grid,
  .order-card__meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767px) {
  .order-card__bottom,
  .order-detail-head,
  .order-actions,
  .section-head,
  .order-total-box > div {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-item-row {
    grid-template-columns: 56px 1fr;
  }

  .order-item-row > div:last-child {
    grid-column: 2;
    text-align: left !important;
  }
}
</style>
