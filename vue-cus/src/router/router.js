import {
    createRouter,
    createWebHistory
} from 'vue-router'

import Home from "@/views/Home.vue"
import NotFound from "@/views/NotFound.vue"
import Forbidden from "@/views/Forbidden.vue"

import Search from "@/views/Search.vue"
import OrderList from "@/views/OrderList.vue"
import VoucherWallet from "@/views/VoucherWallet.vue"
import CheckoutCoupon from '@/pages/CheckoutCoupon.vue'
import {
    getRestaurantById
} from "@/restaurant-module"



const routes = [{
        path: "/",
        component: Home,
        name: "home-link"
    },
    {
        path: "/search",
        component: Search,
        name: "search-link"
    },
    {
        path: "/OrderList",
        component: OrderList,
        name: "OrderList-link"
    },
    {
        path: "/VoucherWallet",
        component: VoucherWallet,
        name: "VoucherWallet-link"
    },
    {
        path: '/Checkout',
        name: 'CheckoutCoupon',
        component: CheckoutCoupon
    },
    {
        // 餐廳頁面路由
        path: "/restaurant/:id",
        name: "Restaurant",
        component: () => import("@/views/RestaurantPage.vue"),
        props: (route) => ({
            restaurant: getRestaurantById(parseInt(route.params.id)),
        }),
    },
    {
        path: "/403",
        component: Forbidden,
        name: "403-link"
    },
    {
        path: "/:pathMatch(.*)*",
        component: NotFound,
        name: "404-link"
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

export default router;