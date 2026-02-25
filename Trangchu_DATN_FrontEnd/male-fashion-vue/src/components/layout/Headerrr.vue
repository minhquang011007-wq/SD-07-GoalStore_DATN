<template>
    <!-- Header Section Begin -->
    <header class="header">
      <div class="header__top">
        <div class="container">
          <div class="row">
            <div class="col-lg-6 col-md-7">
              <div class="header__top__left">
                <p>Free shipping, 30-day return or refund guarantee.</p>
              </div>
            </div>
            <div class="col-lg-6 col-md-5">
              <div class="header__top__right">
                <div class="header__top__links">
                  <a href="#">Sign in</a>
                  <a href="#">FAQs</a>
                </div>
                <div class="header__top__hover">
                  <span>Usd <i class="arrow_carrot-down"></i></span>
                  <ul>
                    <li>USD</li>
                    <li>EUR</li>
                    <li>USD</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <div class="container">
        <div class="row">
          <div class="col-lg-3 col-md-3">
            <div class="header__logo">
              <RouterLink to="/"><img src="/img/logo.png" alt="" /></RouterLink>
            </div>
          </div>
  
          <div class="col-lg-6 col-md-6">
            <nav class="header__menu">
              <ul>
                <li :class="{ active: $route.path === '/' }">
                  <RouterLink to="/">Home</RouterLink>
                </li>
                <li :class="{ active: $route.path === '/shop' }">
                  <RouterLink to="/shop">Shop</RouterLink>
                </li>
                <li :class="{ active: $route.path === '/contact' }">
                  <RouterLink to="/contact">Contacts</RouterLink>
                </li>
                <li :class="{ active: $route.path === '/cart' }">
                  <RouterLink to="/cart">Cart</RouterLink>
                </li>
                <li :class="{ active: $route.path === '/checkout' }">
                  <RouterLink to="/checkout">Checkout</RouterLink>
                </li>
              </ul>
            </nav>
          </div>
  
          <div class="col-lg-3 col-md-3">
            <div class="header__nav__option">
              <a href="#" class="search-switch"><img src="/img/icon/search.png" alt="" /></a>
              <a href="#"><img src="/img/icon/heart.png" alt="" /></a>
              <a href="#"><img src="/img/icon/cart.png" alt="" /> <span>0</span></a>
              <div class="price">$0.00</div>
            </div>
          </div>
        </div>
  
        <div class="canvas__open"><i class="fa fa-bars"></i></div>
      </div>
    </header>
    <!-- Header Section End -->
  </template>
  
  <script setup>
  </script>
  