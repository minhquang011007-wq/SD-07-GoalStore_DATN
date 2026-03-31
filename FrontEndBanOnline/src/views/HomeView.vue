<script setup lang="ts">
import { onMounted } from "vue"

onMounted(() => {
  document.title = "GoalStore | Bán giày bóng đá & phụ kiện thể thao"
  const preloader = document.getElementById("preloder")
  if (preloader) {
    preloader.style.display = "none"
  }
  window.scrollTo({ top: 0 })
})
</script>

<template>
  <div class="legacy-page">
    <!-- Page Preloder -->
  <div id="preloder">
    <div class="loader"></div>
  </div>

  <!-- Offcanvas Menu Begin -->

  <!-- Header Section End -->

  <!-- Hero Section Begin -->
  <section class="hero">
    <div class="hero__slider">
      <div class="hero__items set-bg" data-setbg="/legacy/img/hero/hero-1.jpg">
        <div class="container">
          <div class="row">
            <div class="col-xl-6 col-lg-7 col-md-8">
              <div class="hero__text">
                <h6>GoalStore Collection</h6>
                <h2>Giày bóng đá & Phụ kiện thể thao</h2>
                <p>Chọn đúng đôi giày – nâng tầm trải nghiệm sân cỏ. Hàng chính hãng, tư vấn nhanh, đổi trả dễ.</p>
                <a href="/shop" class="primary-btn">Mua ngay <span class="arrow_right"></span></a>
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
  <!-- Hero Section End -->

  <!-- Banner Section Begin -->
  <section class="banner spad">
    <div class="container">
      <div class="row">
        <div class="col-lg-7 offset-lg-4">
          <div class="banner__item">
            <div class="banner__item__pic">
              <img src="/legacy/img/banner/banner-1.jpg" alt="">
            </div>
            <div class="banner__item__text">
              <h2>Giày sân cỏ nhân tạo</h2>
              <a href="/shop">Xem sản phẩm</a>
            </div>
          </div>
        </div>

        <div class="col-lg-5">
          <div class="banner__item banner__item--middle">
            <div class="banner__item__pic">
              <img src="/legacy/img/banner/banner-2.jpg" alt="">
            </div>
            <div class="banner__item__text">
              <h2>Phụ kiện luyện tập</h2>
              <a href="/shop">Xem sản phẩm</a>
            </div>
          </div>
        </div>

        <div class="col-lg-7">
          <div class="banner__item banner__item--last">
            <div class="banner__item__pic">
              <img src="/legacy/img/banner/banner-3.jpg" alt="">
            </div>
            <div class="banner__item__text">
              <h2>Giày futsal</h2>
              <a href="/shop">Xem sản phẩm</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <!-- Banner Section End -->

  <!-- Product Section Begin (giữ nguyên sample items để demo) -->
  <section class="product spad">
    <div class="container">
      <div class="row product__filter">
        <!-- Bạn có thể thay ảnh/tên/giá sau -->
        <div class="col-lg-3 col-md-6 col-sm-6 mix new-arrivals">
          <div class="product__item">
            <div class="product__item__pic set-bg" data-setbg="/legacy/img/product/product-1.jpg">
              <span class="label">New</span>
              <ul class="product__hover">
                <li><a href="#"><img src="/legacy/img/icon/heart.png" alt=""></a></li>
                <li><a href="#"><img src="/legacy/img/icon/search.png" alt=""></a></li>
              </ul>
            </div>
            <div class="product__item__text">
              <h6>Giày sân cỏ nhân tạo</h6>
              <a href="/cart" class="add-cart">+ Thêm vào giỏ</a>
              <h5>650.000₫</h5>
            </div>
          </div>
        </div>

        <div class="col-lg-3 col-md-6 col-sm-6 mix hot-sales">
          <div class="product__item">
            <div class="product__item__pic set-bg" data-setbg="/legacy/img/product/product-2.jpg">
              <ul class="product__hover">
                <li><a href="#"><img src="/legacy/img/icon/heart.png" alt=""></a></li>
                <li><a href="#"><img src="/legacy/img/icon/search.png" alt=""></a></li>
              </ul>
            </div>
            <div class="product__item__text">
              <h6>Giày futsal</h6>
              <a href="/cart" class="add-cart">+ Thêm vào giỏ</a>
              <h5>720.000₫</h5>
            </div>
          </div>
        </div>

        <div class="col-lg-3 col-md-6 col-sm-6 mix new-arrivals">
          <div class="product__item sale">
            <div class="product__item__pic set-bg" data-setbg="/legacy/img/product/product-3.jpg">
              <span class="label">Sale</span>
              <ul class="product__hover">
                <li><a href="#"><img src="/legacy/img/icon/heart.png" alt=""></a></li>
                <li><a href="#"><img src="/legacy/img/icon/search.png" alt=""></a></li>
              </ul>
            </div>
            <div class="product__item__text">
              <h6>Găng tay thủ môn</h6>
              <a href="/cart" class="add-cart">+ Thêm vào giỏ</a>
              <h5>199.000₫</h5>
            </div>
          </div>
        </div>

        <div class="col-lg-3 col-md-6 col-sm-6 mix hot-sales">
          <div class="product__item">
            <div class="product__item__pic set-bg" data-setbg="/legacy/img/product/product-4.jpg">
              <ul class="product__hover">
                <li><a href="#"><img src="/legacy/img/icon/heart.png" alt=""></a></li>
                <li><a href="#"><img src="/legacy/img/icon/search.png" alt=""></a></li>
              </ul>
            </div>
            <div class="product__item__text">
              <h6>Bảo vệ ống đồng</h6>
              <a href="/cart" class="add-cart">+ Thêm vào giỏ</a>
              <h5>89.000₫</h5>
            </div>
          </div>
        </div>

      </div>
    </div>
  </section>
  <!-- Product Section End -->

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

