import {createRouter, createWebHistory} from "vue-router";

import HomePage from "../pages/HomePage.vue";
import ShopPage from "../pages/ShopPage.vue";
import ShopDetailsPage from "../pages/ShopDetailsPage.vue";
import CartPage from "../pages/CartPage.vue";
import CheckoutPage from "../pages/CheckoutPage.vue";
import ContactPage from "../pages/ContactPage.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {path: "/", name: "home", component: HomePage},
        {path: "/shop", name: "shop", component: ShopPage},
        {path: "/shop-details", name: "shop-details", component: ShopDetailsPage},
        {path: "/cart", name: "cart", component: CartPage},
        {path: "/checkout", name: "checkout", component: CheckoutPage},
        {path: "/contact", name: "contact", component: ContactPage},
    ],
    scrollBehavior() {
        return {top: 0};
    },
});

export default router;
