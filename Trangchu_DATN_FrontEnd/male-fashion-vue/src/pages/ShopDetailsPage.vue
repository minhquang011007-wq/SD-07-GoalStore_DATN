<template>
    <!-- Breadcrumb -->
    <section class="shop-details">
      <div class="product__details__pic">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="product__details__breadcrumb">
                <RouterLink to="/">Home</RouterLink>
                <RouterLink to="/shop">Shop</RouterLink>
                <span>Product Details</span>
              </div>
            </div>
          </div>
  
          <div class="row">
            <div class="col-lg-3 col-md-3">
              <ul class="nav nav-tabs" role="tablist">
                <li class="nav-item" v-for="n in 4" :key="n">
                  <a
                    class="nav-link"
                    :class="{ active: n === 1 }"
                    data-toggle="tab"
                    :href="'#tabs-' + n"
                  >
                    <div
                      class="product__thumb__pic set-bg"
                      :data-setbg="`/img/shop-details/thumb-${n}.png`"
                    />
                  </a>
                </li>
              </ul>
            </div>
  
            <div class="col-lg-6 col-md-9">
              <div class="tab-content">
                <div
                  class="tab-pane"
                  :class="{ active: n === 1 }"
                  v-for="n in 4"
                  :key="n"
                  :id="'tabs-' + n"
                >
                  <div class="product__details__pic__item">
                    <img :src="`/img/shop-details/product-big-${n}.png`" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Product content -->
      <div class="product__details__content">
        <div class="container">
          <div class="row justify-content-center">
            <div class="col-lg-8">
              <div class="product__details__text">
                <h4>Hooded thermal anorak</h4>
                <h3>$270.00</h3>
                <p>
                  Coat with quilted lining and adjustable hood. Long sleeves,
                  adjustable cuff tabs.
                </p>
  
                <div class="product__details__cart__option">
                  <div class="quantity">
                    <input type="number" v-model="qty" min="1" />
                  </div>
                  <button class="primary-btn">Add to cart</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { RouterLink } from "vue-router";
  
  const qty = ref(1);
  
  onMounted(() => {
    document.querySelectorAll(".set-bg").forEach((el) => {
      const bg = el.dataset.setbg;
      if (bg) el.style.backgroundImage = `url(${bg})`;
    });
  });
  </script>
  