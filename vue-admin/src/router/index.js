// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router'
import PromotionList from '@/pages/PromotionList.vue'

const routes = [
    {
        path: '/promotion',
        name: 'PromotionList',
        component: PromotionList
    },
    // 其他路由...

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
