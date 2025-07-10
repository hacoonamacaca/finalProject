import { createRouter, createWebHistory } from 'vue-router'

import Home from "@/views/Jimmy/Home.vue"
import NotFound from "@/views/NotFound.vue"
import Forbidden from "@/views/Forbidden.vue"

import Search from "@/views/Jimmy/Search.vue"
import OrderList from "@/views/Ted/OrderList.vue"
import VoucherWallet from "@/views/Yifan/VoucherWallet.vue"
import CheckoutCoupon from '@/views/Yifan/CheckoutCoupon.vue'
import SubscriptionPlan from '@/views/Yifan/SubscriptionPlan.vue'
import Cart from '@/views/KTlu/Cart.vue'
import Register from '@/components/Ivy/RegisterModal.vue'
import VerifyPending from '@/views/Ivy/VerifyPending.vue'
import RegisterProfile from '@/views/Ivy/RegisterProfile.vue'
import EditUser from '@/views/Ivy/EditUser.vue'
import {
    getRestaurantById
} from "@/data/restaurants.js"
import WebRecom from "@/views/Jimmy/WebRecom.vue"
import ReportType from "@/views/Jimmy/ReportType.vue"
import ReservationRecords from '@/views/KTlu/ReservationRecords.vue'
import CC from "@/views/Jimmy/CC.vue"
import TagCategoryManager from "@/views/Jimmy/TagCategoryManager.vue"

const routes = [{
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
{
    path: '/Checkout',
    name: 'CheckoutCoupon',
    component: CheckoutCoupon
},
{
    path: '/subscription',
    name: 'subscriptionplan',
    component: SubscriptionPlan
},
{
    path: '/cart',
    name: 'Cart',
    component: Cart
},
{
    path: '/register',
    component: Register
},
{
    path: '/verify-pending',
    component: VerifyPending
},
{
    path: '/register-profile',
    component: RegisterProfile
},
{
    path: '/resetPasswordEmail',
    name: 'ResetPasswordEmail',
    component: () => import('@/views/Ivy/ResetPasswordEmail.vue'),
    props: router => ({
        email: router.query.email
    })
},
{
    // 餐廳頁面路由
    path: "/restaurant/:id",
    name: "Restaurant",
    component: () => import("@/views/KTlu/RestaurantPage.vue"),
    props: (route) => ({
        restaurant: getRestaurantById(parseInt(route.params.id)),
    }),
},
{
    path: '/profile',
    name: 'EditUser',
    component: EditUser
},
{
    path: '/WebRecom',
    name: 'WebRecom',
    component: WebRecom
},
{
    path: '/ReportType',
    name: 'ReportType',
    component: ReportType
},
{
    path: '/reservation-records',
    name: 'ReservationRecords',
    component: ReservationRecords
},
{
    path: '/CC',
    name: 'CC',
    component: CC
},
{
    path: '/TagCategoryManager',
    name: 'TagCategoryManager',
    component: TagCategoryManager
},
];

const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

export default router;