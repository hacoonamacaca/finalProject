// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import PromotionList from '@/pages/PromotionList.vue'
import UserCouponList from '@/pages/UserCouponList.vue'
const routes = [
    {
        path: '/promotion',
        name: 'PromotionList',
        component: PromotionList
    },
    // 其他路由...
    {
        path: '/Coupon',
        name: 'UserCouponList',
        component: UserCouponList
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
