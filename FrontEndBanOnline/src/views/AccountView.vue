<template>
  <div class="legacy-page account-page">
    <div id="preloder"><div class="loader"></div></div>

    <section class="breadcrumb-option">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="breadcrumb__text">
              <h4>Tài khoản của tôi</h4>
              <div class="breadcrumb__links">
                <RouterLink to="/">Trang chủ</RouterLink>
                <span>Thông tin khách hàng</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="checkout spad">
      <div class="container">
        <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
        <div v-if="message" class="alert alert-success mb-4">{{ message }}</div>

        <div v-if="!loggedIn" class="account-empty-state">
          <h4>Bạn cần đăng nhập để quản lý tài khoản</h4>
          <p>Đăng nhập tài khoản khách hàng để sửa thông tin cá nhân và địa chỉ nhận hàng.</p>
          <RouterLink to="/login" class="primary-btn">Đăng nhập ngay</RouterLink>
        </div>

        <div v-else-if="loading" class="account-empty-state">
          <h4>Đang tải thông tin tài khoản...</h4>
        </div>

        <div v-else class="row">
          <div class="col-lg-4 col-md-5">
            <div class="checkout__order account-summary-card">
              <h4 class="order__title">Tài khoản GoalStore</h4>
              <div class="account-summary-head">
                <div class="account-avatar">{{ avatarText }}</div>
                <div>
                  <h5>{{ profileForm.ten || displayName }}</h5>
                  <p>{{ profileForm.email || 'Chưa cập nhật email' }}</p>
                </div>
              </div>

              <div class="checkout__total__products account-summary-list">
                <ul>
                  <li>
                    Số điện thoại
                    <span>{{ profileForm.sdt || '---' }}</span>
                  </li>
                  <li>
                    Loại khách
                    <span>{{ formatLoaiKhach(profile.loaiKhach) }}</span>
                  </li>
                  <li>
                    Điểm tích lũy
                    <span>{{ Number(profile.diemTichLuy || 0) }} điểm</span>
                  </li>
                  <li>
                    Ngày sinh
                    <span>{{ profileForm.ngaySinh || 'Chưa cập nhật' }}</span>
                  </li>
                </ul>
              </div>

              <div class="account-summary-actions">
                <RouterLink to="/orders" class="primary-btn w-100 text-center">Theo dõi đơn hàng</RouterLink>
                <RouterLink to="/voucher" class="secondary-btn w-100 text-center">Kho voucher</RouterLink>
                <RouterLink to="/shop" class="secondary-btn w-100 text-center">Tiếp tục mua sắm</RouterLink>
              </div>
            </div>
          </div>

          <div class="col-lg-8 col-md-7">
            <div class="checkout-panel account-stats-panel mb-4">
              <div class="checkout-panel__header stats-header-row">
                <div>
                  <h6 class="checkout__title">Thống kê mua sắm của bạn</h6>
                  <p>Nhìn nhanh doanh thu, đơn hàng, voucher và tiến độ mua sắm theo từng tháng.</p>
                </div>
                <select v-model="selectedYear" class="stats-year-select">
                  <option v-for="year in yearOptions" :key="year" :value="year">Năm {{ year }}</option>
                </select>
              </div>

              <div class="account-stats-grid">
                <div class="account-stat-card">
                  <span>Tổng chi tiêu</span>
                  <strong>{{ Number(dashboard?.summary.totalSpent || 0).toLocaleString('vi-VN') }}₫</strong>
                  <small>Giá trị đơn trung bình {{ Number(dashboard?.summary.averageOrderValue || 0).toLocaleString('vi-VN') }}₫</small>
                </div>
                <div class="account-stat-card">
                  <span>Đơn hàng</span>
                  <strong>{{ dashboard?.summary.totalOrders || 0 }}</strong>
                  <small>{{ dashboard?.summary.completedOrders || 0 }} đơn hoàn tất</small>
                </div>
                <div class="account-stat-card">
                  <span>Điểm loyalty</span>
                  <strong>{{ dashboard?.loyaltyPoints || 0 }}</strong>
                  <small>Hạng {{ dashboard?.loaiKhach || 'THUONG' }}</small>
                </div>
                <div class="account-stat-card">
                  <span>Voucher khả dụng</span>
                  <strong>{{ dashboard?.wallet.available || 0 }}</strong>
                  <small>{{ dashboard?.wallet.used || 0 }} voucher đã dùng</small>
                </div>
              </div>

              <div class="account-stats-layout">
                <div class="mini-chart-card">
                  <div class="mini-chart-card__head">
                    <h6>Chi tiêu theo tháng</h6>
                    <span>{{ selectedYear }}</span>
                  </div>
                  <div class="mini-bars">
                    <div v-for="item in spendingChartBars" :key="item.month" class="mini-bars__item">
                      <div class="mini-bars__track">
                        <div class="mini-bars__fill" :style="{ height: `${item.percent}%` }"></div>
                      </div>
                      <strong>{{ item.label }}</strong>
                    </div>
                  </div>
                </div>

                <div class="mini-chart-card">
                  <div class="mini-chart-card__head">
                    <h6>Cơ cấu trạng thái đơn</h6>
                    <span>{{ statusTotal }} đơn</span>
                  </div>
                  <div class="status-breakdown">
                    <div v-for="item in dashboard?.orderStatusBreakdown || []" :key="item.status" class="status-breakdown__item">
                      <div>
                        <strong>{{ item.label }}</strong>
                        <span>{{ item.count }} đơn</span>
                      </div>
                      <div class="status-breakdown__bar">
                        <div class="status-breakdown__fill" :style="{ width: `${statusTotal ? Math.max(8, Math.round((item.count / statusTotal) * 100)) : 0}%` }"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="checkout-panel">
              <div class="checkout-panel__header">
                <h6 class="checkout__title">Thông tin cá nhân</h6>
                <p>Cập nhật hồ sơ khách hàng để GoalStore hỗ trợ bạn nhanh hơn khi đặt hàng.</p>
              </div>

              <div class="row">
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Họ và tên<span>*</span></p>
                    <input v-model="profileForm.ten" type="text" placeholder="Nhập họ và tên" />
                  </div>
                </div>
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Số điện thoại<span>*</span></p>
                    <input v-model="profileForm.sdt" type="text" placeholder="Nhập số điện thoại" />
                  </div>
                </div>
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Email<span>*</span></p>
                    <input v-model="profileForm.email" type="email" placeholder="Nhập email" />
                  </div>
                </div>
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Ngày sinh</p>
                    <input v-model="profileForm.ngaySinh" type="date" />
                  </div>
                </div>
                <div class="col-lg-12">
                  <div class="checkout__input">
                    <p>Ghi chú</p>
                    <textarea v-model="profileForm.ghiChu" rows="4" placeholder="Thêm ghi chú để GoalStore hỗ trợ bạn tốt hơn"></textarea>
                  </div>
                </div>
              </div>

              <button type="button" class="site-btn" :disabled="savingProfile" @click="handleSaveProfile">
                {{ savingProfile ? 'Đang lưu...' : 'Lưu thông tin cá nhân' }}
              </button>
            </div>

            <div class="checkout-panel mt-4">
              <div class="checkout-panel__header">
                <h6 class="checkout__title">Địa chỉ nhận hàng</h6>
                <p>Chọn địa chỉ đã lưu hoặc thêm địa chỉ mới giống như lúc đặt hàng.</p>
              </div>

              <div v-if="savedAddresses.length" class="checkout-address-picker">
                <label for="saved-address">Địa chỉ đã lưu</label>
                <select id="saved-address" v-model="selectedAddressKey">
                  <option v-for="address in savedAddresses" :key="address.id" :value="String(address.id)">
                    {{ address.receiverName }} - {{ shortAddress(address) }}{{ address.isDefault ? ' (Mặc định)' : '' }}
                  </option>
                  <option value="new">+ Thêm địa chỉ mới</option>
                </select>
              </div>

              <div class="row">
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Người nhận<span>*</span></p>
                    <input v-model="addressForm.receiverName" type="text" placeholder="Tên người nhận" />
                  </div>
                </div>
                <div class="col-lg-6">
                  <div class="checkout__input">
                    <p>Số điện thoại nhận hàng<span>*</span></p>
                    <input v-model="addressForm.receiverPhone" type="text" placeholder="Số điện thoại người nhận" />
                  </div>
                </div>
                <div class="col-lg-4">
                  <div class="checkout__input">
                    <p>Tỉnh/Thành phố<span>*</span></p>
                    <input v-model="addressForm.province" type="text" placeholder="Hà Nội" />
                  </div>
                </div>
                <div class="col-lg-4">
                  <div class="checkout__input">
                    <p>Quận/Huyện<span>*</span></p>
                    <input v-model="addressForm.district" type="text" placeholder="Hoài Đức" />
                  </div>
                </div>
                <div class="col-lg-4">
                  <div class="checkout__input">
                    <p>Phường/Xã<span>*</span></p>
                    <input v-model="addressForm.ward" type="text" placeholder="Trạm Trôi" />
                  </div>
                </div>
                <div class="col-lg-12">
                  <div class="checkout__input">
                    <p>Địa chỉ chi tiết<span>*</span></p>
                    <input v-model="addressForm.detailAddress" type="text" class="checkout__input__add" placeholder="Số nhà, ngõ, đường..." />
                  </div>
                </div>
              </div>

              <button type="button" class="site-btn" :disabled="savingAddress" @click="handleSaveAddress">
                {{ savingAddress ? 'Đang lưu...' : 'Lưu địa chỉ nhận hàng' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { getCustomerId, getDisplayName, isCustomerLoggedIn } from '@/shared/lib/auth'
import {
  createCustomerAddress,
  getCustomerAddresses,
  getCustomerProfile,
  updateCustomerAddress,
  updateCustomerProfile,
  getCustomerDashboard,
} from '@/shared/lib/shop.api'
import type { CustomerAddress, CustomerDashboardResponse, CustomerProfile } from '@/shared/lib/shop.types'

const loading = ref(true)
const savingProfile = ref(false)
const savingAddress = ref(false)
const loggedIn = ref(false)
const customerId = ref<number | null>(null)
const displayName = ref('Khách hàng')
const error = ref('')
const message = ref('')
const profile = ref<CustomerProfile>({ id: 0, ten: '', email: '', sdt: '', ngaySinh: '', loaiKhach: 'THUONG', diemTichLuy: 0, ghiChu: '' })
const dashboard = ref<CustomerDashboardResponse | null>(null)
const selectedYear = ref(new Date().getFullYear())
const defaultAddress = ref<CustomerAddress | null>(null)
const savedAddresses = ref<CustomerAddress[]>([])
const selectedAddressKey = ref('new')

const profileForm = reactive({
  ten: '',
  email: '',
  sdt: '',
  ngaySinh: '',
  ghiChu: '',
})

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  district: '',
  ward: '',
  detailAddress: '',
})

const avatarText = computed(() => {
  const source = profileForm.ten || displayName.value || 'K'
  return source
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
})


const yearOptions = computed(() => Array.from({ length: 4 }, (_, index) => new Date().getFullYear() - index))

const spendingChartBars = computed(() => {
  const items = dashboard.value?.spendingByMonth || []
  const max = Math.max(...items.map((item) => Number(item.value || 0)), 0)
  return items.map((item) => ({
    ...item,
    percent: max > 0 ? Math.max(10, Math.round((Number(item.value || 0) / max) * 100)) : 8,
  }))
})

const statusTotal = computed(() => {
  return (dashboard.value?.orderStatusBreakdown || []).reduce((sum, item) => sum + Number(item.count || 0), 0)
})

async function loadDashboard() {
  if (!customerId.value) return
  dashboard.value = await getCustomerDashboard(customerId.value, selectedYear.value)
}

function resetMessage() {
  error.value = ''
  message.value = ''
}

function applyProfile(value: CustomerProfile) {
  profile.value = value
  profileForm.ten = value.ten || ''
  profileForm.email = value.email || ''
  profileForm.sdt = value.sdt || ''
  profileForm.ngaySinh = value.ngaySinh || ''
  profileForm.ghiChu = value.ghiChu || ''
}


function fillAddressForm(value?: CustomerAddress | null) {
  defaultAddress.value = value || null
  addressForm.receiverName = value?.receiverName || profileForm.ten || ''
  addressForm.receiverPhone = value?.receiverPhone || profileForm.sdt || ''
  addressForm.province = value?.province || ''
  addressForm.district = value?.district || ''
  addressForm.ward = value?.ward || ''
  addressForm.detailAddress = value?.detailAddress || ''
}

function applyAddress(value?: CustomerAddress | null) {
  fillAddressForm(value)
  if (value?.id) {
    selectedAddressKey.value = String(value.id)
  } else if (savedAddresses.value.length) {
    selectedAddressKey.value = 'new'
  }
}

function shortAddress(value?: CustomerAddress | null) {
  return [value?.detailAddress, value?.ward, value?.district, value?.province].filter(Boolean).join(', ')
}


function formatLoaiKhach(value?: string | null) {
  const normalized = String(value || '').toUpperCase()
  if (normalized === 'VIP') return 'VIP'
  return 'Thường'
}

async function loadData() {
  resetMessage()
  loggedIn.value = isCustomerLoggedIn()
  displayName.value = getDisplayName()
  const rawCustomerId = getCustomerId()
  customerId.value = rawCustomerId === null ? null : Number(rawCustomerId)

  if (!loggedIn.value || !customerId.value) {
    loading.value = false
    return
  }

  loading.value = true

  try {
    const [customer, addresses, customerDashboard] = await Promise.all([
      getCustomerProfile(customerId.value),
      getCustomerAddresses(customerId.value),
      getCustomerDashboard(customerId.value, selectedYear.value),
    ])

    applyProfile(customer)
    dashboard.value = customerDashboard
    savedAddresses.value = [...(addresses || [])].sort((a, b) => Number(Boolean(b.isDefault)) - Number(Boolean(a.isDefault)) || b.id - a.id)
    const selectedAddress = savedAddresses.value.find((item) => item.isDefault) || savedAddresses.value[0] || null
    if (selectedAddress) {
      selectedAddressKey.value = String(selectedAddress.id)
    }
    applyAddress(selectedAddress)
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Không tải được thông tin khách hàng'
  } finally {
    loading.value = false
  }
}

function validateProfile() {
  if (!profileForm.ten.trim()) return 'Bạn chưa nhập họ và tên'
  if (!profileForm.email.trim()) return 'Bạn chưa nhập email'
  if (!profileForm.sdt.trim()) return 'Bạn chưa nhập số điện thoại'
  return ''
}

function validateAddress() {
  if (!addressForm.receiverName.trim()) return 'Bạn chưa nhập tên người nhận'
  if (!addressForm.receiverPhone.trim()) return 'Bạn chưa nhập số điện thoại người nhận'
  if (!addressForm.province.trim()) return 'Bạn chưa nhập tỉnh/thành phố'
  if (!addressForm.district.trim()) return 'Bạn chưa nhập quận/huyện'
  if (!addressForm.ward.trim()) return 'Bạn chưa nhập phường/xã'
  if (!addressForm.detailAddress.trim()) return 'Bạn chưa nhập địa chỉ chi tiết'
  return ''
}

async function handleSaveProfile() {
  resetMessage()
  const validation = validateProfile()
  if (validation) {
    error.value = validation
    return
  }

  if (!customerId.value) return

  savingProfile.value = true
  try {
    const updated = await updateCustomerProfile(customerId.value, {
      ten: profileForm.ten.trim(),
      email: profileForm.email.trim(),
      sdt: profileForm.sdt.trim(),
      ngaySinh: profileForm.ngaySinh || null,
      loaiKhach: profile.value.loaiKhach || 'THUONG',
      diemTichLuy: Number(profile.value.diemTichLuy || 0),
      ghiChu: profileForm.ghiChu.trim(),
    })
    applyProfile(updated)
    if (!defaultAddress.value) {
      addressForm.receiverName = profileForm.ten.trim()
      addressForm.receiverPhone = profileForm.sdt.trim()
    }
    message.value = 'Cập nhật thông tin cá nhân thành công'
    window.dispatchEvent(new Event('storage'))
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Cập nhật thông tin cá nhân thất bại'
  } finally {
    savingProfile.value = false
  }
}

async function handleSaveAddress() {
  resetMessage()
  const validation = validateAddress()
  if (validation) {
    error.value = validation
    return
  }

  if (!customerId.value) return

  savingAddress.value = true
  try {
    const payload = {
      customerId: customerId.value,
      receiverName: addressForm.receiverName.trim(),
      receiverPhone: addressForm.receiverPhone.trim(),
      province: addressForm.province.trim(),
      district: addressForm.district.trim(),
      ward: addressForm.ward.trim(),
      detailAddress: addressForm.detailAddress.trim(),
      isDefault: true,
    }

    const currentAddressId = defaultAddress.value?.id ?? null
    const isCreatingNewAddress = selectedAddressKey.value === 'new' || !currentAddressId
    const saved = !isCreatingNewAddress
      ? await updateCustomerAddress(currentAddressId as number, payload)
      : await createCustomerAddress(payload)

    savedAddresses.value = [saved, ...savedAddresses.value.filter((item) => item.id !== saved.id).map((item) => ({ ...item, isDefault: false }))]
    applyAddress(saved)
    message.value = isCreatingNewAddress ? 'Thêm địa chỉ mới thành công' : 'Cập nhật địa chỉ nhận hàng thành công'
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Cập nhật địa chỉ thất bại'
  } finally {
    savingAddress.value = false
  }
}


watch(selectedAddressKey, (value) => {
  if (value === 'new') {
    defaultAddress.value = null
    addressForm.receiverName = profileForm.ten || ''
    addressForm.receiverPhone = profileForm.sdt || ''
    addressForm.province = ''
    addressForm.district = ''
    addressForm.ward = ''
    addressForm.detailAddress = ''
    return
  }

  const address = savedAddresses.value.find((item) => String(item.id) === String(value)) || null
  fillAddressForm(address)
})

onMounted(() => {
  document.title = 'GoalStore | Tài khoản của tôi'
  const preloader = document.getElementById('preloder')
  if (preloader) {
    preloader.style.display = 'none'
  }
  window.scrollTo({ top: 0 })
  loadData()
})

watch(selectedYear, () => {
  if (loggedIn.value && customerId.value) {
    loadDashboard().catch(() => {})
  }
})
</script>

<style scoped>
.account-page {
  background: #f8f5f0;
}

.checkout-panel,
.checkout__order,
.account-empty-state {
  background: #ffffff;
  border: 1px solid #efefef;
}

.checkout-panel,
.account-empty-state {
  padding: 28px;
}

.checkout-panel__header {
  margin-bottom: 18px;
}

.checkout-panel__header p,
.account-empty-state p,
.account-summary-head p {
  color: #666;
}


.checkout-address-picker {
  margin-bottom: 24px;
}

.checkout-address-picker label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
}

.checkout-address-picker select,
.checkout__input textarea {
  width: 100%;
  border: 1px solid #d7d7d7;
  padding: 12px 14px;
  color: #111;
  background: #fff;
  min-height: 120px;
  resize: vertical;
}

.account-empty-state {
  text-align: center;
}

.account-summary-card {
  padding: 30px 26px;
  position: sticky;
  top: 24px;
}

.account-summary-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 22px 0;
}

.account-summary-head h5 {
  margin-bottom: 6px;
  font-size: 20px;
}

.account-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #111111;
  color: #ffffff;
  font-weight: 700;
  font-size: 18px;
}

.account-summary-list ul {
  margin-bottom: 0;
}

.account-summary-list li {
  font-size: 14px;
}

.account-summary-list li span {
  color: #111111;
  font-weight: 600;
}

.account-summary-actions {
  display: grid;
  gap: 12px;
  margin-top: 26px;
}

.w-100 {
  width: 100%;
}

.text-center {
  text-align: center;
}

.mt-4 {
  margin-top: 24px;
}

.mb-4 {
  margin-bottom: 24px;
}

.stats-header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.stats-year-select {
  min-width: 140px;
  border: 1px solid #d7d7d7;
  background: #fff;
  padding: 10px 12px;
}

.account-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 20px;
}

.account-stat-card {
  background: #f7f7f7;
  border: 1px solid #ececec;
  padding: 18px;
}

.account-stat-card span,
.account-stat-card small {
  display: block;
  color: #666;
}

.account-stat-card strong {
  display: block;
  font-size: 24px;
  color: #111;
  margin: 8px 0 6px;
}

.account-stats-layout {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
}

.mini-chart-card {
  border: 1px solid #ececec;
  background: #fff;
  padding: 20px;
}

.mini-chart-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.mini-chart-card__head h6 {
  margin: 0;
  font-size: 16px;
}

.mini-chart-card__head span {
  color: #666;
  font-size: 13px;
}

.mini-bars {
  height: 230px;
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 10px;
  align-items: end;
}

.mini-bars__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  height: 100%;
}

.mini-bars__track {
  width: 100%;
  flex: 1;
  display: flex;
  align-items: end;
  background: #f1f1f1;
  border-radius: 999px;
  overflow: hidden;
  min-height: 140px;
}

.mini-bars__fill {
  width: 100%;
  background: linear-gradient(180deg, #111111 0%, #8f8f8f 100%);
  border-radius: 999px;
}

.mini-bars__item strong {
  font-size: 12px;
  color: #111;
}

.status-breakdown {
  display: grid;
  gap: 14px;
}

.status-breakdown__item strong {
  display: block;
  color: #111;
}

.status-breakdown__item span {
  font-size: 13px;
  color: #666;
}

.status-breakdown__bar {
  margin-top: 10px;
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background: #f1f1f1;
  overflow: hidden;
}

.status-breakdown__fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #111111 0%, #7a7a7a 100%);
}

@media (max-width: 1199px) {
  .account-stats-grid,
  .account-stats-layout {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767px) {
  .stats-header-row,
  .account-stats-grid,
  .account-stats-layout {
    grid-template-columns: 1fr;
    display: grid;
  }

  .mini-bars {
    grid-template-columns: repeat(6, minmax(0, 1fr));
    row-gap: 16px;
    height: auto;
  }

  .mini-bars__track {
    min-height: 100px;
  }
}

@media (max-width: 991px) {
  .account-summary-card {
    position: static;
    margin-bottom: 24px;
  }
}
</style>
