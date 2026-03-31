<template>
    <div class="legacy-page">
      <!-- Page Preloder -->
      <div id="preloder">
          <div class="loader"></div>
      </div>
  
      <!-- Offcanvas Menu Begin -->
  
      <!-- Header Section End -->
  
      <!-- Breadcrumb Section Begin -->
      <section class="breadcrumb-option">
          <div class="container">
              <div class="row">
                  <div class="col-lg-12">
                      <div class="breadcrumb__text">
                          <h4>Thanh toán</h4>
                          <div class="breadcrumb__links">
                              <a href="/">Trang chủ</a>
                              <a href="/shop">Sản phẩm</a>
                              <span>Thanh toán</span>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
      </section>
      <!-- Breadcrumb Section End -->
  
      <!-- Checkout Section Begin -->
      <section class="checkout spad">
          <div class="container">
              <div class="checkout__form">
                  <form action="#">
                      <div class="row">
                          <div class="col-lg-8 col-md-6">
                              <h6 class="coupon__code">
                                  <span class="icon_tag_alt"></span> Có mã giảm giá?
                                  <a href="#">Bấm vào đây</a> để nhập mã
                              </h6>
  
                              <h6 class="checkout__title">Thông tin thanh toán</h6>
  
                              <div class="row">
                                  <div class="col-lg-6">
                                      <div class="checkout__input">
                                          <p>Họ<span>*</span></p>
                                          <input type="text">
                                      </div>
                                  </div>
                                  <div class="col-lg-6">
                                      <div class="checkout__input">
                                          <p>Tên<span>*</span></p>
                                          <input type="text">
                                      </div>
                                  </div>
                              </div>
  
                              <div class="checkout__input">
                                  <p>Quốc gia<span>*</span></p>
                                  <input type="text" value="Việt Nam">
                              </div>
  
                              <div class="checkout__input">
                                  <p>Địa chỉ<span>*</span></p>
                                  <input type="text" placeholder="Số nhà, tên đường..." class="checkout__input__add">
                                  <input type="text" placeholder="Căn hộ/Tòa nhà (không bắt buộc)">
                              </div>
  
                              <div class="checkout__input">
                                  <p>Quận/Huyện<span>*</span></p>
                                  <input type="text">
                              </div>
  
                              <div class="checkout__input">
                                  <p>Tỉnh/Thành phố<span>*</span></p>
                                  <input type="text">
                              </div>
  
                              <div class="checkout__input">
                                  <p>Mã bưu chính<span>*</span></p>
                                  <input type="text" placeholder="VD: 100000">
                              </div>
  
                              <div class="row">
                                  <div class="col-lg-6">
                                      <div class="checkout__input">
                                          <p>Số điện thoại<span>*</span></p>
                                          <input type="text" placeholder="VD: 09xxxxxxxx">
                                      </div>
                                  </div>
                                  <div class="col-lg-6">
                                      <div class="checkout__input">
                                          <p>Email<span>*</span></p>
                                          <input type="text" placeholder="VD: you@example.com">
                                      </div>
                                  </div>
                              </div>
  
                              <div class="checkout__input__checkbox">
                                  <label for="acc">
                                      Tạo tài khoản?
                                      <input type="checkbox" id="acc">
                                      <span class="checkmark"></span>
                                  </label>
                                  <p>Nếu bạn đã có tài khoản, vui lòng đăng nhập để theo dõi đơn hàng.</p>
                              </div>
  
                              <div class="checkout__input">
                                  <p>Mật khẩu<span>*</span></p>
                                  <input type="password" placeholder="Nhập mật khẩu">
                              </div>
  
                              <div class="checkout__input__checkbox">
                                  <label for="diff-acc">
                                      Ghi chú đơn hàng (tuỳ chọn)
                                      <input type="checkbox" id="diff-acc">
                                      <span class="checkmark"></span>
                                  </label>
                              </div>
  
                              <div class="checkout__input">
                                  <p>Ghi chú<span>*</span></p>
                                  <input type="text" placeholder="VD: giao giờ hành chính, gọi trước khi giao...">
                              </div>
                          </div>
  
                          <div class="col-lg-4 col-md-6">
                              <div class="checkout__order">
                                  <h4 class="order__title">Đơn hàng của bạn</h4>
                                  <div class="checkout__order__products">Sản phẩm <span>Tạm tính</span></div>
  
                                  <ul class="checkout__total__products">
                                      <li>01. Giày sân cỏ nhân tạo <span>650.000₫</span></li>
                                      <li>02. Giày futsal <span>720.000₫</span></li>
                                      <li>03. Găng tay thủ môn <span>199.000₫</span></li>
                                      <li>04. Bảo vệ ống đồng <span>89.000₫</span></li>
                                  </ul>
  
                                  <ul class="checkout__total__all">
                                      <li>Tạm tính <span>1.658.000₫</span></li>
                                      <li>Tổng cộng <span>1.658.000₫</span></li>
                                  </ul>
  
                                  <div class="checkout__input__checkbox">
                                      <label for="acc-or">
                                          Đồng ý điều khoản mua hàng
                                          <input type="checkbox" id="acc-or">
                                          <span class="checkmark"></span>
                                      </label>
                                  </div>
  
                                  <p>Bằng việc đặt hàng, bạn đồng ý với chính sách giao hàng & đổi trả của GoalStore.</p>
  
                                  <div class="checkout__input__checkbox">
                                      <label for="payment">
                                          Thanh toán khi nhận hàng (COD)
                                          <input type="checkbox" id="payment">
                                          <span class="checkmark"></span>
                                      </label>
                                  </div>
  
                                  <div class="checkout__input__checkbox">
                                      <label for="paypal">
                                          Thanh toán online (PayPal)
                                          <input type="checkbox" id="paypal">
                                          <span class="checkmark"></span>
                                      </label>
                                  </div>
  
                                  <button type="submit" class="site-btn">ĐẶT HÀNG</button>
                              </div>
                          </div>
  
                      </div>
                  </form>
              </div>
          </div>
      </section>
      <!-- Checkout Section End -->
  
      <!-- Footer Section Begin -->
  
      <!-- Footer Section End -->
  
      <!-- Search Begin -->
      <div class="search-model">
          <div class="h-100 d-flex align-items-center justify-content-center">
              <div class="search-close-switch">+</div>
              <form class="search-model-form">
                  <input type="text" id="search-input" placeholder="Tìm kiếm sản phẩm...">
              </form>
          </div>
      </div>
      <!-- Search End -->
  
      <!-- Js Plugins -->
  
  
  
  
  
  
  
  
  
  
    </div>
  </template>
  
  <script setup lang="ts">
  import { onMounted } from 'vue'
  
  onMounted(() => {
    document.title = 'GoalStore | Thanh toán'
    const preloader = document.getElementById('preloder')
    if (preloader) {
      preloader.style.display = 'none'
    }
    window.scrollTo({ top: 0 })
  })
  </script>
  