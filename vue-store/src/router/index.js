import { createRouter, createWebHistory } from 'vue-router';

// 為了讓路由設定更乾淨，我們在這裡導入頁面組件
// 我們假設您已經將 AdminPage.vue 重新命名為 MenuManagement.vue
import MenuManagement from '../pages/MenuManagement.vue';

const routes = [
    {
        path: '/',
        // 我們將根目錄暫時重導向到菜單管理頁面
        // 未來當您開發好 Dashboard，只需要把這行改成
        // component: () => import('../pages/SellerDashboard.vue') 即可
        redirect: '/menu'
    },
    {
        path: '/menu',
        name: 'MenuManagement',
        component: MenuManagement
    },
    // 未來您可以繼續在這裡新增路由
    // {
    //   path: '/orders',
    //   name: 'OrderManagement',
    //   component: () => import('../pages/OrderManagement.vue')
    // }
];

const router = createRouter({
    history: createWebHistory(),
    routes, // 相當於 routes: routes
});

export default router;