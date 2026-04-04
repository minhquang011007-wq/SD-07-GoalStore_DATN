<template>
  <div class="legacy-page voucher-page">
    <div id="preloder"><div class="loader"></div></div>

    <section class="breadcrumb-option">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="breadcrumb__text">
              <h4>Kho voucher</h4>
              <div class="breadcrumb__links">
                <RouterLink to="/">Trang chủ</RouterLink>
                <span>Voucher</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="coupon spad">
      <div class="container">
        <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
        <div v-if="message" class="alert alert-success mb-4">{{ message }}</div>

        <div v-if="!loggedIn" class="voucher-empty-state">
          <h4>Bạn cần đăng nhập để nhận voucher</h4>
          <p>Đăng nhập tài khoản khách hàng để vào trang voucher và lưu từng voucher vào ví của bạn.</p>
          <RouterLink to="/login" class="primary-btn">Đăng nhập ngay</RouterLink>
        </div>

        <div v-else>
          <div class="voucher-hero-card">
            <div>
              <p class="voucher-hero-card__eyebrow">Ưu đãi từ admin</p>
              <h3>Nhận voucher cho tài khoản của bạn</h3>
              <p>
                Mỗi tài khoản được nhận tối đa <strong>1 lần cho mỗi voucher</strong>.
                Mỗi voucher cũng có <strong>số lượng giới hạn</strong>, nhận hết là sẽ không ai lấy thêm được nữa.
              </p>
            </div>

            <div class="voucher-stats-grid">
              <div class="voucher-stat-item">
                <span>Đã nhận</span>
                <strong>{{ claimedCount }}/{{ wallet.length }}</strong>
              </div>
              <div class="voucher-stat-item">
                <span>Chưa dùng</span>
                <strong>{{ availableCount }}</strong>
              </div>
              <div class="voucher-stat-item">
                <span>Đã dùng</span>
                <strong>{{ usedCount }}</strong>
              </div>
            </div>
          </div>

          <div class="row mt-4">
            <div v-for="voucher in wallet" :key="voucher.id" class="col-lg-6 col-md-6">
              <div class="voucher-card" :class="{ 'voucher-card--claimed': voucher.claimed && !voucher.used, 'voucher-card--used': voucher.used }">
                <div class="voucher-card__badge">-{{ voucher.discountPercent }}%</div>

                <div class="voucher-card__content">
                  <div class="voucher-card__head">
                    <div>
                      <p class="voucher-card__code">{{ voucher.code }}</p>
                      <h5>{{ voucher.name }}</h5>
                    </div>
                    <span class="voucher-status" :class="voucher.used ? 'voucher-status--used' : voucher.claimed ? 'voucher-status--claimed' : 'voucher-status--new'">
                      {{ voucher.used ? 'Đã dùng' : voucher.claimed ? 'Đã nhận' : (voucher.remainingQuantity || 0) > 0 ? 'Chưa nhận' : 'Hết lượt' }}
                    </span>
                  </div>

                  <p class="voucher-card__desc">{{ voucher.description || 'Voucher giảm giá cho đơn hàng GoalStore.' }}</p>

                  <ul class="voucher-card__meta">
                    <li>
                      <span>Mã voucher</span>
                      <strong>{{ voucher.code }}</strong>
                    </li>
                    <li>
                      <span>Giảm giá</span>
                      <strong>{{ voucher.discountPercent }}%</strong>
                    </li>
                    <li>
                      <span>Đơn tối thiểu</span>
                      <strong>{{ voucher.minOrderAmount ? formatCurrency(voucher.minOrderAmount) : 'Không yêu cầu' }}</strong>
                    </li>
                    <li>
                      <span>Số lượng</span>
                      <strong>{{ voucher.remainingQuantity || 0 }}/{{ voucher.totalQuantity || 0 }} còn lại</strong>
                    </li>
                    <li v-if="voucher.claimedAt">
                      <span>Đã nhận lúc</span>
                      <strong>{{ formatVoucherDate(voucher.claimedAt) }}</strong>
                    </li>
                    <li v-if="voucher.usedAt">
                      <span>Đã dùng lúc</span>
                      <strong>{{ formatVoucherDate(voucher.usedAt) }}</strong>
                    </li>
                  </ul>

                  <div class="voucher-card__actions">
                    <button
                      v-if="!voucher.claimed"
                      type="button"
                      class="site-btn"
                      :disabled="(voucher.remainingQuantity || 0) <= 0"
                      @click="handleClaim(voucher.id)"
                    >
                      {{ (voucher.remainingQuantity || 0) <= 0 ? 'Đã hết lượt' : 'Nhận voucher' }}
                    </button>

                    <template v-else-if="voucher.used">
                      <button type="button" class="secondary-btn" disabled>
                        Đã sử dụng{{ voucher.usedOrderCode ? ` (${voucher.usedOrderCode})` : '' }}
                      </button>
                    </template>

                    <template v-else>
                      <button type="button" class="secondary-btn" @click="selectVoucher(voucher.id)">
                        {{ selectedVoucherId === voucher.id ? 'Đang chọn để thanh toán' : 'Dùng khi thanh toán' }}
                      </button>
                      <RouterLink to="/checkout" class="primary-btn voucher-link-btn">Thanh toán ngay</RouterLink>
                    </template>
                  </div>
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
import { getCustomerId, isCustomerLoggedIn } from '@/shared/lib/auth'
import {
  claimCustomerVoucher,
  formatVoucherDate,
  getCustomerVoucherWallet,
  getSelectedVoucherForCustomer,
  setSelectedVoucherForCustomer,
  type CustomerVoucherWalletItem,
} from '@/shared/lib/voucher.api'

const loggedIn = ref(false)
const customerId = ref<number | null>(null)
const loading = ref(false)
const error = ref('')
const message = ref('')
const wallet = ref<CustomerVoucherWalletItem[]>([])
const selectedVoucherId = ref<number | null>(null)

const claimedCount = computed(() => wallet.value.filter((item) => item.claimed).length)
const usedCount = computed(() => wallet.value.filter((item) => item.used).length)
const availableCount = computed(() => wallet.value.filter((item) => item.claimed && !item.used).length)

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatCurrency(value: unknown) {
  return `${toNumber(value).toLocaleString('vi-VN')}₫`
}

function resetNotice() {
  error.value = ''
  message.value = ''
}

async function loadWallet() {
  if (!customerId.value) {
    wallet.value = []
    selectedVoucherId.value = null
    return
  }

  loading.value = true
  try {
    wallet.value = await getCustomerVoucherWallet(customerId.value)
    selectedVoucherId.value = getSelectedVoucherForCustomer(customerId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Không thể tải danh sách voucher.'
  } finally {
    loading.value = false
  }
}

async function handleClaim(voucherId: number) {
  resetNotice()

  if (!customerId.value) {
    error.value = 'Không tìm thấy tài khoản khách hàng để nhận voucher.'
    return
  }

  try {
    await claimCustomerVoucher(customerId.value, voucherId)
    await loadWallet()
    message.value = 'Bạn đã nhận voucher thành công. Voucher này giờ đã nằm trong ví của bạn.'
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Không thể nhận voucher.'
  }
}

function selectVoucher(voucherId: number) {
  resetNotice()

  if (!customerId.value) {
    error.value = 'Không tìm thấy tài khoản khách hàng để chọn voucher.'
    return
  }

  setSelectedVoucherForCustomer(customerId.value, voucherId)
  selectedVoucherId.value = voucherId
  message.value = 'Đã chọn voucher. Bạn có thể sang trang thanh toán để áp dụng ngay.'
}

onMounted(async () => {
  document.title = 'GoalStore | Kho voucher'
  const preloader = document.getElementById('preloder')
  if (preloader) {
    preloader.style.display = 'none'
  }
  window.scrollTo({ top: 0 })

  loggedIn.value = isCustomerLoggedIn()
  const rawCustomerId = getCustomerId()
  customerId.value = rawCustomerId === null ? null : Number(rawCustomerId)

  if (loggedIn.value && customerId.value) {
    await loadWallet()
  }
})
</script>

<style scoped>
.voucher-page {
  background: #f8f5f0;
}

.voucher-empty-state,
.voucher-hero-card,
.voucher-card {
  background: #fff;
  border: 1px solid #efefef;
}

.voucher-empty-state {
  padding: 48px 24px;
  text-align: center;
}

.voucher-empty-state p {
  color: #666;
  margin: 12px auto 20px;
  max-width: 520px;
}

.voucher-hero-card {
  padding: 28px;
  display: grid;
  grid-template-columns: 1.3fr 1fr;
  gap: 24px;
  align-items: center;
}

.voucher-hero-card__eyebrow,
.voucher-card__code {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  font-weight: 700;
  color: #c28f2c;
}

.voucher-hero-card h3 {
  margin: 10px 0 12px;
}

.voucher-hero-card p {
  color: #666;
  margin: 0;
}

.voucher-stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.voucher-stat-item {
  border: 1px solid #ececec;
  padding: 16px;
  text-align: center;
}

.voucher-stat-item span {
  display: block;
  color: #666;
  font-size: 12px;
  margin-bottom: 6px;
}

.voucher-stat-item strong {
  font-size: 24px;
}

.voucher-card {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: 120px 1fr;
  min-height: 100%;
  margin-bottom: 24px;
}

.voucher-card__badge {
  background: #111;
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 30px;
  font-weight: 700;
}

.voucher-card__content {
  padding: 22px;
}

.voucher-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.voucher-card__head h5 {
  margin: 8px 0 0;
}

.voucher-card__desc {
  color: #666;
  margin: 12px 0 16px;
}

.voucher-card__meta {
  list-style: none;
  margin: 0 0 20px;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.voucher-card__meta li {
  border: 1px solid #f0f0f0;
  padding: 12px;
}

.voucher-card__meta span {
  display: block;
  color: #777;
  font-size: 12px;
  margin-bottom: 4px;
}

.voucher-card__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.voucher-status {
  display: inline-flex;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 700;
}

.voucher-status--new {
  background: #f6f1e5;
  color: #a66c00;
}

.voucher-status--claimed {
  background: #eef7ef;
  color: #22753b;
}

.voucher-status--used {
  background: #f4f4f4;
  color: #666;
}

.voucher-card--claimed {
  border-color: #d9f0de;
}

.voucher-card--used {
  opacity: 0.78;
}

.voucher-link-btn,
.secondary-btn,
.primary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 160px;
}

.secondary-btn {
  border: 1px solid #111;
  background: #fff;
  color: #111;
  padding: 12px 24px;
  cursor: pointer;
}

.secondary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.site-btn[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 991px) {
  .voucher-hero-card,
  .voucher-card {
    grid-template-columns: 1fr;
  }

  .voucher-card__badge {
    min-height: 90px;
  }
}

@media (max-width: 767px) {
  .voucher-stats-grid,
  .voucher-card__meta {
    grid-template-columns: 1fr;
  }
}
</style>
