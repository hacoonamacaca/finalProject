import { createRouter, createWebHistory } from 'vue-router'

import Home from "@/views/Home.vue"
import NotFound from "@/views/NotFound.vue"
import Forbidden from "@/views/Forbidden.vue"
import Search from "@/views/Search.vue" 
import Login from '@/views/Login.vue'

// import Chat from "@/views/Chat.vue"
// import Product from '@/views/pages/Product.vue'

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
        path: "/login",
        component: Login,
        name: "secure-login-link"
    },
    // {
    //     path: "/pages/product",
    //     component: Product,
    //     name: "page-product-link"
    // },
    // {
    //     path: "/chat",
    //     component: Chat,
    //     name: "chat-link"
    // },
    {
        path: "/search",
        component: Search,
        name: "search-link"
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

export default router;