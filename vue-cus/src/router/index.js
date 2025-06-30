// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// import PromotionList from '@/pages/PromotionList.vue'
import CheckoutCoupon from '@/pages/CheckoutCoupon.vue'
import VoucherWallet from '../../../vue-cus/src/pages/VoucherWallet.vue'

const routes = [
    // {
    //     path: '/promotion',
    //     name: 'PromotionList',
    //     component: PromotionList
    // },
    {
        path: '/Checkout',
        name: 'CheckoutCoupon',
        component: CheckoutCoupon
    },
    {
        path: '/voucher-wallet',
        name: 'VoucherWallet',
        component: VoucherWallet
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
