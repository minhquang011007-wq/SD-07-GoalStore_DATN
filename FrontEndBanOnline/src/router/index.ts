import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ShopView from '@/views/ShopView.vue'
import ShopDetailsView from '@/views/ShopDetailsView.vue'
import ShoppingCartView from '@/views/ShoppingCartView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import MyOrdersView from '@/views/MyOrdersView.vue'
import ContactView from '@/views/ContactView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { getRole } from '@/shared/lib/auth'
import { hydrateSessionFromUrl } from '@/shared/lib/cross-app-auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: HomeView },
    { path: '/shop', name: 'Shop', component: ShopView },
    { path: '/shop-details/:id?', name: 'ShopDetails', component: ShopDetailsView },
    { path: '/cart', name: 'ShoppingCart', component: ShoppingCartView },
    { path: '/checkout', name: 'Checkout', component: CheckoutView },
    { path: '/orders', name: 'MyOrders', component: MyOrdersView },
    { path: '/contact', name: 'Contact', component: ContactView },
    { path: '/login', name: 'Login', component: LoginView, meta: { public: true } },
    { path: '/register', name: 'Register', component: RegisterView, meta: { public: true } },
  ],
})

router.beforeEach((to, _from, next) => {
  hydrateSessionFromUrl()

  const role = getRole()

  if (role === 'CUSTOMER' && (to.path === '/login' || to.path === '/register')) {
    return next('/')
  }

  next()
})

export default router
