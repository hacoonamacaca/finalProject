<<<<<<< HEAD
// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import PromotionList from '@/pages/PromotionList.vue'
// import CheckoutCoupon from '@/pages/CheckoutCoupon.vue'
// import VoucherWallet from '../../../vue-cus/src/pages/VoucherWallet.vue'

const routes = [
    {
        path: '/promotion',
        name: 'PromotionList',
        component: PromotionList
    },
    // {
    //     path: '/Checkout',
    //     name: 'CheckoutCoupon',
    //     component: CheckoutCoupon
    // },
    // {
    //     path: '/voucher-wallet',
    //     name: 'VoucherWallet',
    //     component: VoucherWallet
    // }
=======
import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import Home from '@/views/Home.vue'
import Account from '@/views/Account.vue'
import Restaurant from '@/views/Restaurant.vue'
import Review from '@/views/Review.vue'
import Discount from '@/views/Discount.vue'
import SubscriptionPlan from '@/views/SubscriptionPlan.vue'
import SubscriptionRecord from '@/views/SubscriptionRecord.vue'
import RestaurantTag from '@/views/RestaurantTag.vue'
import FoodTag from '@/views/FoodTag.vue'
import Recommendation from '@/views/Recommendation.vue'
import Login from '@/views/Login.vue'
// ...其餘頁面

const routes = [
    {
        path: '/',
        component: MainLayout,
        children: [
            { path: '', component: Home },
            { path: 'account', component: Account },
            { path: 'restaurant', component: Restaurant },
            { path: 'review', component: Review },
            { path: 'discount', component: Discount },
            { path: 'subscription/plan', component: SubscriptionPlan },
            { path: 'subscription/record', component: SubscriptionRecord },
            { path: 'restaurantTag', component: RestaurantTag },
            { path: 'foodTag', component: FoodTag },
            { path: 'recommendation', component: Recommendation },
            { path: 'login', component: Login },
        ]
    }
>>>>>>> ivy
]

const router = createRouter({
    history: createWebHistory(),
    routes
})
<<<<<<< HEAD

export default router
=======
export default router
>>>>>>> ivy
