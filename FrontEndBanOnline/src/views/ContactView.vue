<template>
    <div class="legacy-page">
      <!-- Page Preloder -->
  <div id="preloder">
      <div class="loader"></div>
  </div>
  
  <!-- Offcanvas Menu Begin -->

  <!-- Header Section End -->
  
  <!-- Map Begin -->
  <div class="map">
      <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d111551.9926412813!2d-90.27317134641879!3d38.606612219170856!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x54eab584e432360b%3A0x1c3bb99243deb742!2sUnited%20States!5e0!3m2!1sen!2sbd!4v1597926938024!5m2!1sen!2sbd"
              height="500" style="border:0;" allowfullscreen aria-hidden="false" tabindex="0"></iframe>
  </div>
  <!-- Map End -->
  
  <!-- Contact Section Begin -->
  <section class="contact spad">
      <div class="container">
          <div class="row">
              <div class="col-lg-6 col-md-6">
                  <div class="contact__text">
                      <div class="section-title">
                          <span>Hỗ trợ</span>
                          <h2>Liên hệ GoalStore</h2>
                          <p>Nếu bạn cần tư vấn chọn giày, size, hoặc hỗ trợ đơn hàng — hãy để lại thông tin, GoalStore sẽ liên hệ sớm.</p>
                      </div>
                      <ul>
                          <li>
                              <h4>Cửa hàng</h4>
                              <p>GoalStore - Việt Nam <br/>Hotline: 09xx xxx xxx</p>
                          </li>
                          <li>
                              <h4>Hỗ trợ online</h4>
                              <p>Email: support@goalstore.vn <br/>Thời gian: 8:00 - 22:00</p>
                          </li>
                      </ul>
                  </div>
              </div>
  
              <div class="col-lg-6 col-md-6">
                  <div class="contact__form">
                      <form action="#">
                          <div class="row">
                              <div class="col-lg-6">
                                  <input type="text" placeholder="Họ tên">
                              </div>
                              <div class="col-lg-6">
                                  <input type="text" placeholder="Email">
                              </div>
                              <div class="col-lg-12">
                                  <textarea placeholder="Nội dung liên hệ"></textarea>
                                  <button type="submit" class="site-btn">Gửi liên hệ</button>
                              </div>
                          </div>
                      </form>
                  </div>
              </div>
  
          </div>
      </div>
  </section>
  <!-- Contact Section End -->
  
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
    document.title = 'GoalStore | Liên hệ'
    const preloader = document.getElementById('preloder')
    if (preloader) {
      preloader.style.display = 'none'
    }
    window.scrollTo({ top: 0 })
  })
  </script>
  