<template>
  <div class="legacy-page vnpay-return-page">
    <section class="breadcrumb-option">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="breadcrumb__text">
              <h4>Kết quả thanh toán VNPAY</h4>
              <div class="breadcrumb__links">
                <RouterLink to="/">Trang chủ</RouterLink>
                <RouterLink to="/orders">Đơn hàng của tôi</RouterLink>
                <span>Kết quả VNPAY</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="checkout spad">
      <div class="container">
        <div class="return-card">
          <div class="return-icon" :class="statusClass">
            {{ statusIcon }}
          </div>
          <p class="return-eyebrow">VNPAY</p>
          <h3>{{ title }}</h3>
          <p class="return-message">{{ subtitle }}</p>

          <div v-if="loading" class="return-loading">Đang tải thông tin đơn hàng...</div>

          <div v-else-if="order" class="return-summary-grid">
            <div class="return-summary-box">
              <span>Mã đơn</span>
              <strong>{{ order.code || `#${order.id}` }}</strong>
            </div>
            <div class="return-summary-box">
              <span>Thanh toán</span>
              <strong>{{ formatPaymentStatus(order.paymentStatus) }}</strong>
            </div>
            <div class="return-summary-box">
              <span>Trạng thái đơn</span>
              <strong>{{ formatStatus(order.status) }}</strong>
            </div>
            <div class="return-summary-box">
              <span>Tổng tiền</span>
              <strong>{{ formatCurrency(order.total) }}</strong>
            </div>
          </div>

          <div class="return-meta">
            <p v-if="txnRef"><strong>Mã giao dịch merchant:</strong> {{ txnRef }}</p>
            <p v-if="responseCode"><strong>Mã phản hồi VNPAY:</strong> {{ responseCode }}</p>
            <p v-if="transactionStatus"><strong>Mã trạng thái giao dịch:</strong> {{ transactionStatus }}</p>
            <p v-if="messageText"><strong>Thông điệp:</strong> {{ messageText }}</p>
          </div>

          <div class="return-note">
            <p>
              Trạng thái thanh toán chính thức sẽ được backend cập nhật khi VNPAY gọi IPN về server.
              Nếu m đang test local, hãy dùng ngrok hoặc cloudflared để VNPAY gọi được IPN URL.
            </p>
          </div>

          <div class="return-actions">
            <RouterLink :to="orderLink" class="primary-btn">Xem đơn hàng</RouterLink>
            <RouterLink to="/shop" class="secondary-btn">Tiếp tục mua sắm</RouterLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { getOrderDetail } from '@/shared/lib/shop.api'
import type { OrderResponse } from '@/shared/lib/shop.types'

const route = useRoute()
const loading = ref(false)
const order = ref<OrderResponse | null>(null)

const status = computed(() => String(route.query.status || 'failed'))
const orderId = computed(() => Number(route.query.orderId || 0))
const txnRef = computed(() => String(route.query.txnRef || ''))
const responseCode = computed(() => String(route.query.responseCode || ''))
const transactionStatus = computed(() => String(route.query.transactionStatus || ''))
const messageText = computed(() => String(route.query.message || ''))
const orderLink = computed(() => orderId.value ? `/orders?orderId=${orderId.value}` : '/orders')

const title = computed(() => {
  if (status.value === 'success') return 'Thanh toán đang được xác nhận'
  if (status.value === 'invalid') return 'Kết quả trả về không hợp lệ'
  return 'Thanh toán chưa hoàn tất'
})

const subtitle = computed(() => {
  if (status.value === 'success') {
    return 'Khách đã hoàn tất thao tác trên VNPAY. Hệ thống sẽ tự động cập nhật đơn ngay khi IPN được gửi thành công.'
  }
  if (status.value === 'invalid') {
    return 'Chữ ký hoặc dữ liệu trả về từ VNPAY không hợp lệ. M hãy kiểm tra lại cấu hình return URL và hash secret.'
  }
  return 'Giao dịch có thể đã bị hủy hoặc thất bại. M vẫn có thể vào đơn hàng để thanh toán lại.'
})

const statusIcon = computed(() => {
  if (status.value === 'success') return '✓'
  if (status.value === 'invalid') return '!'
  return '×'
})

const statusClass = computed(() => ({
  success: status.value === 'success',
  invalid: status.value === 'invalid',
  failed: status.value !== 'success' && status.value !== 'invalid',
}))

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatCurrency(value: unknown) {
  return `${toNumber(value).toLocaleString('vi-VN')}₫`
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

function formatPaymentStatus(value?: string | null) {
  const map: Record<string, string> = {
    UNPAID: 'Chưa thanh toán',
    PENDING: 'Đang chờ xác nhận',
    PAID: 'Đã thanh toán',
    REFUNDED: 'Đã hoàn tiền',
  }
  const normalized = String(value || '').toUpperCase()
  return map[normalized] || value || 'Chưa xác định'
}

onMounted(async () => {
  document.title = 'GoalStore | Kết quả thanh toán VNPAY'
  window.scrollTo({ top: 0 })
  if (!orderId.value) return

  loading.value = true
  try {
    order.value = await getOrderDetail(orderId.value)
  } catch {
    order.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.vnpay-return-page {
  background: #f8f5f0;
}

.return-card {
  background: #fff;
  border: 1px solid #efefef;
  padding: 36px 28px;
  text-align: center;
}

.return-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
  background: #f7f7f7;
  color: #111;
}

.return-icon.success {
  background: #effaf3;
  color: #1f8f47;
}

.return-icon.failed,
.return-icon.invalid {
  background: #fff3f3;
  color: #cc2b2b;
}

.return-eyebrow {
  text-transform: uppercase;
  letter-spacing: .2em;
  color: #888;
  font-size: 12px;
  margin-bottom: 8px;
}

.return-message,
.return-loading,
.return-note p,
.return-meta p {
  color: #666;
}

.return-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin: 24px 0;
}

.return-summary-box {
  border: 1px solid #efefef;
  padding: 16px;
}

.return-summary-box span {
  display: block;
  color: #777;
  font-size: 12px;
  margin-bottom: 8px;
}

.return-summary-box strong {
  font-size: 16px;
  color: #111;
}

.return-meta {
  text-align: left;
  margin: 20px auto 0;
  max-width: 620px;
}

.return-note {
  margin-top: 20px;
  padding: 16px;
  background: #faf8f4;
  border: 1px dashed #d9d2c4;
}

.return-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

@media (max-width: 767px) {
  .return-summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
