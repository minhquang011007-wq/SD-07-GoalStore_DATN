<template>
    <section class="breadcrumb-option">
      <div class="container">
        <h4>Shopping Cart</h4>
      </div>
    </section>
  
    <section class="shopping-cart spad">
      <div class="container">
        <div class="row">
          <div class="col-lg-8">
            <table class="shopping__cart__table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Qty</th>
                  <th>Total</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in cart" :key="item.id">
                  <td class="product__cart__item">
                    <img :src="item.img" width="80" />
                    <h6>{{ item.name }}</h6>
                  </td>
                  <td>
                    <input type="number" v-model="item.qty" min="1" />
                  </td>
                  <td>${{ item.price * item.qty }}</td>
                  <td>
                    <button @click="remove(item.id)">X</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
  
          <div class="col-lg-4">
            <div class="cart__total">
              <h6>Cart total</h6>
              <p>Total: ${{ total }}</p>
              <RouterLink to="/checkout" class="primary-btn">
                Checkout
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, computed } from "vue";
  import { RouterLink } from "vue-router";
  
  const cart = ref([
    {
      id: 1,
      name: "T-shirt Contrast Pocket",
      price: 98.49,
      qty: 1,
      img: "/img/shopping-cart/cart-1.jpg",
    },
  ]);
  
  const remove = (id) => {
    cart.value = cart.value.filter((i) => i.id !== id);
  };
  
  const total = computed(() =>
    cart.value.reduce((sum, i) => sum + i.price * i.qty, 0)
  );
  </script>
  