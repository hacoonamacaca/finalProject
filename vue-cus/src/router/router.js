import { createRouter, createWebHistory } from 'vue-router'

import Home from "@/views/Jimmy/Home.vue"
import NotFound from "@/views/NotFound.vue"
import Forbidden from "@/views/Forbidden.vue"
import Search from "@/views/Jimmy/Search.vue" 
import OrderList from "@/views/Ted/OrderList.vue"
import VoucherWallet from "@/views/Yifan/VoucherWallet.vue"


const routes = [
    {
        path: "/",
        component: Home,
        name: "home-link"
    },
    {
        path: "/:pathMatch(.*)*",
        component: NotFound,
        name: "404-link"
    },
    {
        path: "/403",
        component: Forbidden,
        name: "403-link"
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
];

const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

export default router;