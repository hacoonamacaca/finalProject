import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import Home from '@/views/Home.vue'
import Account from '@/views/Account.vue'
import Restaurant from '@/views/Restaurant.vue'
import Review from '@/views/Review.vue'
import Discount from '@/views/Discount.vue'
import Subscription from '@/views/Subscription.vue'

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
            { path: 'subscription', component: Subscription },
        ]
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router
