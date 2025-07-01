<script setup>
// 把 Logo 和 Avatar 的導入也移到這裡
import logoUrl from '../assets/logo.png';
import avataUrl from '../assets/avata.png';
</script>

<template>
    <div class="page-wrapper">
        <!-- Header: 在 Flex 容器中，它是一個獨立的區塊 -->
        <header class="bg-warning p-3 d-flex justify-content-between align-items-center shadow-sm flex-shrink-0">
            <div class="d-flex align-items-center">
                <img :src="logoUrl" alt="Logo" style="height: 40px; margin-right: 12px;">
                <h1 class="h5 m-0">商家管理中心</h1>
            </div>
            <div>
                <span>Kinan, 您好！</span>
                <img :src="avataUrl" alt="Avata" style="height: 40px; margin-right: 12px;">
            </div>
        </header>

    <div class="main-container">
        <!-- Sidebar: 現在是 main-container 的一個 flex item -->
        <nav class="sidebar bg-light p-3">
            <!-- 側邊欄連結使用 router-link -->
            <div class="sidebar-sticky">
                <h6 class="sidebar-heading px-3 mt-4 mb-1 text-muted">管理你的商家資訊</h6>
                    <ul class="nav flex-column">
                    <!-- 使用 <router-link> 並透過 "to" 屬性指定目標路徑 -->
                        <router-link to="#" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">👨‍🍳商家資料</a>
                            </li>
                        </router-link>
                        <router-link to="/menu" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">🍳菜單管理</a>
                            </li>
                        </router-link> 
                        <router-link to="#" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">🍽️店鋪管理</a>
                            </li>
                        </router-link>
                    </ul>
                    <h6 class="sidebar-heading px-3 mt-4 mb-1 text-muted">主要功能設定</h6>
                    <ul class="nav flex-column mb-2">
                        <router-link to="/orders" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">📃訂單管理</a>
                            </li>
                        </router-link>
                        <router-link to="#" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">🪑訂位管理</a>
                            </li>
                        </router-link>                        
                        <router-link to="#" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">🕒營業時間</a>
                            </li>
                        </router-link>
                        <router-link to="#" custom v-slot="{ href, navigate, isActive }">
                            <li class="nav-item">
                                <a :href="href" @click="navigate" class="nav-link" :class="{ 'active-link': isActive }">📰評論回覆</a>
                            </li>
                        </router-link>
                        <!-- <li class="nav-item"><a class="nav-link" href="#">欄位名稱</a></li> 改router前的格式寫法 -->
                        <!-- <router-link to="#" class="nav-link">欄位名稱</router-link> 改custom v-slot前的簡化格式寫法 -->
                    </ul>
                </div>
        </nav>

        <!-- 主內容區域：這是一個 "插座"，用來顯示子路由頁面 -->
            <main class="main-content p-4">
            <router-view />
        </main>
    </div>
    
        <!-- Footer -->
        <footer class="bg-warning text-white text-center p-3 flex-shrink-0">
            Footer
        </footer>
    </div>
</template>

<style scoped>
/* 把 MenuManagement.vue 中的所有佈局 CSS 剪下貼到這裡 */

.page-wrapper {
    display: flex;
    flex-direction: column;
    /* 讓 header, main-container, footer 垂直排列 */
    height: 100vh;
}

.main-container {
    display: flex;
    /* 讓 sidebar 和 main-content 水平排列 */
    flex-grow: 1;
    /* 佔滿 page-wrapper 中除了 header 和 footer 的所有空間 */
    overflow: hidden;
}

.sidebar {
    flex-shrink: 0;
    width: 250px;
    overflow-y: auto;
    border-right: 1px solid #dee2e6;
    /* 模仿原始設計的陰影/邊框 */
}

.main-content {
    flex-grow: 1;
    overflow-y: auto;
}

.nav-link.active {
    font-weight: bold;
    color: #0d6efd;
}

/* 確保 header 和 footer 不會被壓縮 */
header,
footer {
    flex-shrink: 0;
}
</style>