<template>
    <div class="legacy-page checkout-page">
      <div id="preloder"><div class="loader"></div></div>
  
      <section class="breadcrumb-option">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="breadcrumb__text">
                <h4>Thanh toán</h4>
                <div class="breadcrumb__links">
                  <RouterLink to="/">Trang chủ</RouterLink>
                  <RouterLink to="/shop">Sản phẩm</RouterLink>
                  <span>Thanh toán</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
  
      <section class="checkout spad">
        <div class="container">
          <div v-if="error" class="alert alert-danger mb-4">{{ error }}</div>
  
          <div v-if="!loggedIn" class="checkout-empty-state">
            <h4>Bạn cần đăng nhập để thanh toán</h4>
            <p>Đăng nhập tài khoản khách hàng để lưu giỏ hàng, địa chỉ nhận hàng và đặt đơn.</p>
            <RouterLink to="/login" class="primary-btn">Đăng nhập ngay</RouterLink>
          </div>
  
          <div v-else-if="loading" class="checkout-empty-state">
            <h4>Đang tải thông tin thanh toán...</h4>
          </div>
  
          <div v-else-if="orderSuccess" class="checkout-success-card">
            <div class="checkout-success-card__icon">✓</div>
            <p class="checkout-success-card__eyebrow">Đặt hàng thành công</p>
            <h3>Đơn hàng {{ orderSuccess.code || `#${orderSuccess.id}` }} đã được tạo</h3>
            <p>
              GoalStore đã ghi nhận đơn hàng của bạn. Nhân viên sẽ liên hệ sớm để xác nhận và giao hàng.
            </p>
  
            <div class="checkout-success-grid">
              <div class="checkout-success-box">
                <span>Mã đơn</span>
                <strong>{{ orderSuccess.code || `#${orderSuccess.id}` }}</strong>
              </div>
              <div class="checkout-success-box">
                <span>Trạng thái</span>
                <strong>{{ formatStatus(orderSuccess.status) }}</strong>
              </div>
              <div class="checkout-success-box">
                <span>Thanh toán</span>
                <strong>{{ formatPaymentMethod(orderSuccess.paymentMethod) }}</strong>
              </div>
              <div class="checkout-success-box">
                <span>Tổng tiền</span>
                <strong>{{ formatCurrency(orderSuccess.total) }}</strong>
              </div>
            </div>
  
            <div class="checkout-success-summary">
              <div class="checkout-success-summary__head">
                <h5>Sản phẩm đã đặt</h5>
                <span>{{ submittedItems.length }} sản phẩm</span>
              </div>
  
              <div v-for="item in submittedItems" :key="`${item.itemId}-${item.variantId}`" class="checkout-success-item">
                <img :src="resolveImageUrl(item.imageUrl) || '/legacy/img/shopping-cart/cart-1.jpg'" :alt="item.productName || 'Sản phẩm'" />
                <div>
                  <h6>{{ item.productName || 'Sản phẩm' }}</h6>
                  <p>{{ variantText(item) }}</p>
                  <span>{{ item.quantity }} x {{ formatCurrency(item.unitPrice) }}</span>
                </div>
                <strong>{{ formatCurrency(item.lineTotal) }}</strong>
              </div>
            </div>
  
            <div class="checkout-success-address">
              <h5>Thông tin nhận hàng</h5>
              <p><strong>Người nhận:</strong> {{ orderSuccess.receiverName }}</p>
              <p><strong>Số điện thoại:</strong> {{ orderSuccess.receiverPhone }}</p>
              <p><strong>Địa chỉ:</strong> {{ orderSuccess.shippingAddress }}</p>
              <p v-if="orderSuccess.note"><strong>Ghi chú:</strong> {{ orderSuccess.note }}</p>
            </div>
  
            <div class="checkout-success-actions">
              <RouterLink to="/orders" class="primary-btn">Theo dõi đơn hàng</RouterLink>
              <RouterLink to="/shop" class="secondary-btn">Tiếp tục mua sắm</RouterLink>
            </div>
          </div>
  
          <div v-else-if="!cartItems.length" class="checkout-empty-state">
            <h4>Giỏ hàng của bạn đang trống</h4>
            <p>Hãy quay lại cửa hàng và chọn sản phẩm trước khi thanh toán.</p>
            <RouterLink to="/shop" class="primary-btn">Mua sắm ngay</RouterLink>
          </div>
  
          <div v-else class="checkout__form">
            <form @submit.prevent="placeOrder">
              <div class="row">
                <div class="col-lg-8 col-md-7">
                  <div class="checkout-panel">
                    <div class="checkout-panel__header">
                      <h6 class="checkout__title">Thông tin nhận hàng</h6>
                      <p>Điền đúng thông tin để GoalStore giao hàng nhanh và chính xác.</p>
                    </div>
  
                    <div v-if="addresses.length" class="checkout-address-picker">
                      <label for="saved-address">Địa chỉ đã lưu</label>
                      <select id="saved-address" v-model="selectedAddressKey">
                        <option v-for="address in addresses" :key="address.id" :value="String(address.id)">
                          {{ address.receiverName }} - {{ shortAddress(address) }}
                          {{ address.isDefault ? ' (Mặc định)' : '' }}
                        </option>
                        <option value="new">+ Thêm địa chỉ mới</option>
                      </select>
                    </div>
  
                    <div class="row">
                      <div class="col-lg-6">
                        <div class="checkout__input">
                          <p>Người nhận<span>*</span></p>
                          <input v-model="form.receiverName" type="text" placeholder="Nhập tên người nhận" />
                        </div>
                      </div>
                      <div class="col-lg-6">
                        <div class="checkout__input">
                          <p>Số điện thoại<span>*</span></p>
                          <input v-model="form.receiverPhone" type="text" placeholder="VD: 09xxxxxxxx" />
                        </div>
                      </div>
                    </div>
  
                    <div class="row">
                      <div class="col-lg-4">
                        <div class="checkout__input">
                          <p>Tỉnh/Thành phố<span>*</span></p>
                          <input v-model="form.province" type="text" placeholder="Ví dụ: Hà Nội" />
                        </div>
                      </div>
                      <div class="col-lg-4">
                        <div class="checkout__input">
                          <p>Quận/Huyện<span>*</span></p>
                          <input v-model="form.district" type="text" placeholder="Ví dụ: Hoài Đức" />
                        </div>
                      </div>
                      <div class="col-lg-4">
                        <div class="checkout__input">
                          <p>Phường/Xã<span>*</span></p>
                          <input v-model="form.ward" type="text" placeholder="Ví dụ: Trạm Trôi" />
                        </div>
                      </div>
                    </div>
  
                    <div class="checkout__input">
                      <p>Địa chỉ chi tiết<span>*</span></p>
                      <input
                        v-model="form.detailAddress"
                        type="text"
                        class="checkout__input__add"
                        placeholder="Số nhà, ngõ, đường, tòa nhà..."
                      />
                    </div>
  
                    <div class="checkout__input">
                      <p>Ghi chú đơn hàng</p>
                      <textarea v-model="note" rows="4" placeholder="VD: Giao giờ hành chính, gọi trước khi giao..."></textarea>
                    </div>
                  </div>
  
                  <div class="checkout-panel mt-4">
                    <div class="checkout-panel__header">
                      <h6 class="checkout__title">Phương thức thanh toán</h6>
                      <p>Chọn hình thức thanh toán phù hợp với bạn.</p>
                    </div>
  
                    <div class="payment-method-list">
                      <label v-for="option in paymentOptions" :key="option.value" class="payment-method-item">
                        <input v-model="paymentMethod" type="radio" :value="option.value" />
                        <div>
                          <strong>{{ option.label }}</strong>
                          <p>{{ option.description }}</p>
                        </div>
                      </label>
                    </div>
  
                    <label class="checkout-agreement">
                      <input v-model="agreeTerms" type="checkbox" />
                      <span>Tôi đồng ý với chính sách mua hàng, giao hàng và đổi trả của GoalStore.</span>
                    </label>
                  </div>
                </div>
  
                <div class="col-lg-4 col-md-5">
                  <div class="checkout__order checkout-order-card sticky-top">
                    <h4 class="order__title">Đơn hàng của bạn</h4>
                    <div class="checkout__order__products">Sản phẩm <span>Tạm tính</span></div>
  
                    <div class="checkout-order-items">
                      <div v-for="item in cartItems" :key="item.itemId" class="checkout-order-item">
                        <img :src="resolveImageUrl(item.imageUrl) || '/legacy/img/shopping-cart/cart-1.jpg'" :alt="item.productName || 'Sản phẩm'" />
                        <div>
                          <h6>{{ item.productName || 'Sản phẩm' }}</h6>
                          <p>{{ variantText(item) }}</p>
                          <span>{{ item.quantity }} x {{ formatCurrency(item.unitPrice) }}</span>
                        </div>
                        <strong>{{ formatCurrency(item.lineTotal) }}</strong>
                      </div>
                    </div>
  
                    <ul class="checkout__total__all">
                      <li>Tạm tính <span>{{ formatCurrency(subtotal) }}</span></li>
                      <li>Phí vận chuyển <span>{{ shippingFee ? formatCurrency(shippingFee) : 'Miễn phí' }}</span></li>
                      <li>Tổng cộng <span>{{ formatCurrency(grandTotal) }}</span></li>
                    </ul>
  
                    <p class="checkout-order-note">
                      Đơn từ {{ formatCurrency(1000000) }} sẽ được miễn phí vận chuyển toàn quốc.
                    </p>
  
                    <button type="submit" class="site-btn" :disabled="placing">
                      {{ placing ? 'Đang đặt hàng...' : 'ĐẶT HÀNG' }}
                    </button>
  
                    <RouterLink to="/cart" class="checkout-back-link">Quay lại giỏ hàng</RouterLink>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </section>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, onMounted, reactive, ref, watch } from 'vue'
  import { useRouter } from 'vue-router'
  import { getCustomerId, getDisplayName, isCustomerLoggedIn } from '@/shared/lib/auth'
  import {
    checkoutCart,
    createCustomerAddress,
    getCart,
    getCustomerAddresses,
    resolveImageUrl,
    updateCustomerAddress,
  } from '@/shared/lib/shop.api'
  import type { CartItem, CartResponse, CustomerAddress, OrderResponse } from '@/shared/lib/shop.types'
  
  const router = useRouter()
  const loading = ref(true)
  const placing = ref(false)
  const loggedIn = ref(false)
  const error = ref('')
  const cart = ref<CartResponse>({ items: [], totalAmount: 0 })
  const addresses = ref<CustomerAddress[]>([])
  const selectedAddressKey = ref('new')
  const paymentMethod = ref<'COD' | 'BANKING' | 'MOMO' | 'VNPAY'>('COD')
  const note = ref('')
  const agreeTerms = ref(true)
  const orderSuccess = ref<OrderResponse | null>(null)
  const submittedItems = ref<CartItem[]>([])
  
  const form = reactive({
    receiverName: '',
    receiverPhone: '',
    province: '',
    district: '',
    ward: '',
    detailAddress: '',
  })
  
  const paymentOptions = [
    {
      value: 'COD' as const,
      label: 'Thanh toán khi nhận hàng (COD)',
      description: 'Phù hợp khi bạn muốn kiểm tra hàng trước khi thanh toán.',
    },
    {
      value: 'BANKING' as const,
      label: 'Chuyển khoản ngân hàng',
      description: 'Xác nhận chuyển khoản nhanh sau khi đặt đơn.',
    },
    {
      value: 'MOMO' as const,
      label: 'Ví MoMo',
      description: 'Thanh toán nhanh trên điện thoại, thao tác đơn giản.',
    },
    {
      value: 'VNPAY' as const,
      label: 'VNPay',
      description: 'Hỗ trợ thẻ ATM, QR và nhiều ngân hàng nội địa.',
    },
  ]
  
  function toNumber(value: unknown) {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : 0
  }
  
  function formatCurrency(value: unknown) {
    return `${toNumber(value).toLocaleString('vi-VN')}₫`
  }
  
  function formatPaymentMethod(value?: string | null) {
    const map: Record<string, string> = {
      COD: 'Thanh toán khi nhận hàng',
      BANKING: 'Chuyển khoản ngân hàng',
      MOMO: 'Ví MoMo',
      VNPAY: 'VNPay',
    }
    return value ? map[value] || value : 'Chưa xác định'
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
    return value ? map[value] || value : 'Mới'
  }
  
  function variantText(item: CartItem) {
    const values = [item.color, item.size].filter(Boolean)
    return values.length ? values.join(' / ') : 'Biến thể mặc định'
  }
  
  function shortAddress(address: CustomerAddress) {
    return [address.detailAddress, address.ward, address.district, address.province].filter(Boolean).join(', ')
  }
  
  function fillForm(address?: CustomerAddress | null) {
    if (!address) {
      form.receiverName = getDisplayName()
      form.receiverPhone = ''
      form.province = ''
      form.district = ''
      form.ward = ''
      form.detailAddress = ''
      return
    }
  
    form.receiverName = address.receiverName || ''
    form.receiverPhone = address.receiverPhone || ''
    form.province = address.province || ''
    form.district = address.district || ''
    form.ward = address.ward || ''
    form.detailAddress = address.detailAddress || ''
  }
  
  const cartItems = computed(() => cart.value.items || [])
  const subtotal = computed(() => toNumber(cart.value.totalAmount))
  const shippingFee = computed(() => (subtotal.value >= 1000000 ? 0 : 30000))
  const grandTotal = computed(() => subtotal.value + shippingFee.value)
  
  watch(selectedAddressKey, (value) => {
    if (value === 'new') {
      fillForm(null)
      return
    }
  
    const address = addresses.value.find((item) => String(item.id) === String(value))
    if (address) {
      fillForm(address)
    }
  })
  
  async function loadCheckoutData() {
    loading.value = true
    error.value = ''
    loggedIn.value = isCustomerLoggedIn()
  
    if (!loggedIn.value) {
      loading.value = false
      return
    }
  
    const customerId = getCustomerId()
    if (!customerId) {
      error.value = 'Không tìm thấy mã khách hàng để thanh toán.'
      loading.value = false
      return
    }
  
    try {
      const [cartResponse, addressResponse] = await Promise.all([
        getCart(customerId),
        getCustomerAddresses(Number(customerId)),
      ])
  
      cart.value = cartResponse
      addresses.value = [...addressResponse].sort((a, b) => Number(Boolean(b.isDefault)) - Number(Boolean(a.isDefault)) || b.id - a.id)
  
      if (addresses.value.length) {
        const preferred = addresses.value.find((item) => item.isDefault) || addresses.value[0]
        selectedAddressKey.value = String(preferred.id)
        fillForm(preferred)
      } else {
        selectedAddressKey.value = 'new'
        fillForm(null)
      }
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể tải thông tin thanh toán.'
    } finally {
      loading.value = false
    }
  }
  
  function validateForm() {
    if (!form.receiverName.trim()) return 'Vui lòng nhập tên người nhận.'
    if (!form.receiverPhone.trim()) return 'Vui lòng nhập số điện thoại người nhận.'
    if (!form.province.trim()) return 'Vui lòng nhập tỉnh/thành phố.'
    if (!form.district.trim()) return 'Vui lòng nhập quận/huyện.'
    if (!form.ward.trim()) return 'Vui lòng nhập phường/xã.'
    if (!form.detailAddress.trim()) return 'Vui lòng nhập địa chỉ chi tiết.'
    if (!agreeTerms.value) return 'Bạn cần đồng ý điều khoản mua hàng trước khi đặt đơn.'
    if (!cartItems.value.length) return 'Giỏ hàng đang trống.'
    return ''
  }
  
  async function upsertAddress(customerId: number) {
    const payload = {
      customerId,
      receiverName: form.receiverName.trim(),
      receiverPhone: form.receiverPhone.trim(),
      province: form.province.trim(),
      district: form.district.trim(),
      ward: form.ward.trim(),
      detailAddress: form.detailAddress.trim(),
      isDefault: true,
    }
  
    if (selectedAddressKey.value === 'new') {
      const created = await createCustomerAddress(payload)
      addresses.value = [created, ...addresses.value.filter((item) => item.id !== created.id).map((item) => ({ ...item, isDefault: false }))]
      selectedAddressKey.value = String(created.id)
      return created.id
    }
  
    const updated = await updateCustomerAddress(Number(selectedAddressKey.value), payload)
    addresses.value = [updated, ...addresses.value.filter((item) => item.id !== updated.id).map((item) => ({ ...item, isDefault: false }))]
    return updated.id
  }
  
  async function placeOrder() {
    error.value = ''
  
    const validationMessage = validateForm()
    if (validationMessage) {
      error.value = validationMessage
      return
    }
  
    const customerId = Number(getCustomerId())
    if (!customerId) {
      error.value = 'Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.'
      return
    }
  
    placing.value = true
  
    try {
      const addressId = await upsertAddress(customerId)
      submittedItems.value = [...cartItems.value]
      orderSuccess.value = await checkoutCart({
        customerId,
        addressId,
        paymentMethod: paymentMethod.value,
        note: note.value.trim(),
        shippingFee: shippingFee.value,
        discountAmount: 0,
      })
      cart.value = { items: [], totalAmount: 0 }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } catch (e: any) {
      error.value = e?.response?.data?.message || e?.message || 'Không thể đặt hàng. Vui lòng thử lại.'
    } finally {
      placing.value = false
    }
  }
  
  onMounted(async () => {
    document.title = 'GoalStore | Thanh toán'
    const preloader = document.getElementById('preloder')
    if (preloader) {
      preloader.style.display = 'none'
    }
    window.scrollTo({ top: 0 })
    await loadCheckoutData()
  })
  </script>
  
  <style scoped>
  .checkout-page {
    background: #f8f5f0;
  }
  
  .checkout-panel,
  .checkout__order,
  .checkout-success-card,
  .checkout-empty-state {
    background: #ffffff;
    border: 1px solid #efefef;
  }
  
  .checkout-panel {
    padding: 28px;
  }
  
  .checkout-panel__header {
    margin-bottom: 18px;
  }
  
  .checkout-panel__header p,
  .checkout-order-note,
  .checkout-empty-state p,
  .checkout-success-card > p {
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
  }
  
  .checkout__input textarea {
    min-height: 120px;
    resize: vertical;
  }
  
  .payment-method-list {
    display: grid;
    gap: 12px;
  }
  
  .payment-method-item {
    display: flex;
    gap: 12px;
    align-items: flex-start;
    border: 1px solid #ececec;
    padding: 14px 16px;
    cursor: pointer;
  }
  
  .payment-method-item input {
    margin-top: 4px;
  }
  
  .payment-method-item p {
    margin: 4px 0 0;
    color: #666;
    font-size: 13px;
  }
  
  .checkout-agreement {
    margin-top: 18px;
    display: flex;
    gap: 10px;
    align-items: flex-start;
    color: #444;
  }
  
  .checkout-order-card {
    padding: 24px;
    top: 24px;
  }
  
  .checkout-order-items {
    display: flex;
    flex-direction: column;
    gap: 14px;
    margin: 20px 0;
  }
  
  .checkout-order-item,
  .checkout-success-item {
    display: grid;
    grid-template-columns: 64px 1fr auto;
    gap: 12px;
    align-items: center;
  }
  
  .checkout-order-item img,
  .checkout-success-item img {
    width: 64px;
    height: 64px;
    object-fit: cover;
    border: 1px solid #eee;
  }
  
  .checkout-order-item h6,
  .checkout-success-item h6 {
    margin-bottom: 4px;
    font-size: 14px;
  }
  
  .checkout-order-item p,
  .checkout-success-item p {
    margin: 0 0 4px;
    color: #777;
    font-size: 12px;
  }
  
  .checkout-order-item span,
  .checkout-success-item span {
    color: #444;
    font-size: 12px;
  }
  
  .checkout-back-link {
    display: inline-block;
    margin-top: 14px;
    color: #111;
    text-decoration: underline;
  }
  
  .checkout-empty-state,
  .checkout-success-card {
    padding: 42px 32px;
    text-align: center;
  }
  
  .checkout-success-card__icon {
    width: 74px;
    height: 74px;
    margin: 0 auto 16px;
    border-radius: 50%;
    display: grid;
    place-items: center;
    background: #111;
    color: #fff;
    font-size: 30px;
    font-weight: 700;
  }
  
  .checkout-success-card__eyebrow {
    text-transform: uppercase;
    letter-spacing: 0.12em;
    font-weight: 700;
    color: #c78a2c !important;
    margin-bottom: 8px;
  }
  
  .checkout-success-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 14px;
    margin: 28px 0;
    text-align: left;
  }
  
  .checkout-success-box {
    border: 1px solid #ececec;
    padding: 16px;
  }
  
  .checkout-success-box span {
    display: block;
    color: #777;
    font-size: 12px;
    margin-bottom: 6px;
  }
  
  .checkout-success-summary,
  .checkout-success-address {
    text-align: left;
    border: 1px solid #ececec;
    padding: 20px;
    margin-top: 20px;
  }
  
  .checkout-success-summary__head {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    margin-bottom: 16px;
  }
  
  .checkout-success-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 24px;
  }
  
  .secondary-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 180px;
    height: 50px;
    padding: 0 24px;
    background: #f1ede6;
    color: #111;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 2px;
    text-transform: uppercase;
  }
  
  .alert {
    padding: 14px 18px;
    border-radius: 8px;
  }
  
  .alert-danger {
    background: #fff2f2;
    color: #c53030;
  }
  
  @media (max-width: 991px) {
    .checkout-success-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
  
  @media (max-width: 767px) {
    .checkout-panel,
    .checkout-order-card,
    .checkout-success-card,
    .checkout-empty-state {
      padding: 22px;
    }
  
    .checkout-success-grid {
      grid-template-columns: 1fr;
    }
  
    .checkout-order-item,
    .checkout-success-item {
      grid-template-columns: 56px 1fr;
    }
  
    .checkout-order-item strong,
    .checkout-success-item strong {
      grid-column: 2;
    }
  }
  </style>
  