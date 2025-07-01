<template>
    <div class="page-wrapper">
        <!-- Header -->
        <header class="navbar d-flex justify-content-between align-items-center px-4 py-2 shadow-sm"
            style="background-color: #ffba20;">
            <span class="brand-title">網站後台管理系統</span>
            <div class="d-flex align-items-center gap-3">
                <span class="text-white fw-semibold">使用者，您好！</span>
                <i class="bi bi-person-circle text-white" style="font-size: 2rem;"></i>
            </div>
        </header>

        <div class="main-container">
            <!-- Sidebar -->
            <nav class="sidebar">
                <div class="sidebar-sticky">
                    <div class="sidebar-section">
                        <ul class="list-group list-group-flush">
                            <!-- 帳號管理 -->
                            <li>
                                <a class="list-group-item parent" :class="{ active: isAccountActive }" @click="toggle('account')">
                                    <i class="bi bi-person-gear me-2"></i> 帳號管理
                                </a>
                                <router-link v-if="open.account" to="/account" class="list-group-item child"
                                    :class="{ active: isAccountActive }">
                                    <i class="bi bi-person-check me-2"></i> 帳號相關審核
                                </router-link>
                            </li>
                            <!-- 餐廳管理 -->
                            <li>
                                <a class="list-group-item parent" :class="{ active: isRestaurantActive }" @click="toggle('restaurant')">
                                    <i class="bi bi-shop-window me-2"></i> 餐廳管理
                                </a>
                                <router-link v-if="open.restaurant" to="/restaurant" class="list-group-item child"
                                    :class="{ active: isRestaurantActive }">
                                    <i class="bi bi-ui-checks-grid me-2"></i> 餐廳審核
                                </router-link>
                            </li>
                            <!-- 評價管理 -->
                            <li>
                                <router-link to="/review" class="list-group-item parent" :class="{ active: isReviewActive }">
                                    <i class="bi bi-star-half me-2"></i> 評價管理
                                </router-link>
                            </li>
                            <!-- 優惠活動 -->
                            <li>
                                <router-link to="/discount" class="list-group-item parent" :class="{ active: isDiscountActive }">
                                    <i class="bi bi-ticket-detailed me-2"></i> 優惠活動
                                </router-link>
                            </li>
                            <!-- 訂閱管理 -->
                            <li>
                                <a class="list-group-item parent"
                                    :class="{ active: isSubscriptionPlanActive || isSubscriptionRecordActive }"
                                    @click="toggle('subscription')">
                                    <i class="bi bi-envelope-paper me-2"></i> 訂閱管理
                                </a>
                                <router-link v-if="open.subscription" to="/subscription/plan" class="list-group-item child"
                                    :class="{ active: isSubscriptionPlanActive }">
                                    <i class="bi bi-archive me-2"></i> 訂閱方案
                                </router-link>
                                <router-link v-if="open.subscription" to="/subscription/record" class="list-group-item child"
                                    :class="{ active: isSubscriptionRecordActive }">
                                    <i class="bi bi-journal-bookmark-fill me-2"></i> 訂閱紀錄
                                </router-link>
                            </li>
                            <!-- 餐廳標籤 -->
                            <li>
                                <router-link to="/restaurantTag" class="list-group-item parent" :class="{ active: isRestaurantTagActive }">
                                    <i class="bi bi-tags me-2"></i> 餐廳標籤
                                </router-link>
                            </li>
                            <!-- 食物標籤 -->
                            <li>
                                <router-link to="/foodTag" class="list-group-item parent" :class="{ active: isFoodTagActive }">
                                    <i class="bi bi-egg-fried me-2"></i> 食物標籤
                                </router-link>
                            </li>
                            <!-- 網站推薦 -->
                            <li>
                                <router-link to="/recommendation" class="list-group-item parent" :class="{ active: isRecommendationActive }">
                                    <i class="bi bi-bookmark-heart me-2"></i> 網站推薦
                                </router-link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- 主內容區域 -->
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

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const open = ref({
    account: false,
    restaurant: false,
    subscription: false,
})

// 子選單收合
function toggle(section) {
    open.value[section] = !open.value[section]
    Object.keys(open.value).forEach(k => {
        if (k !== section) open.value[k] = false
    })
}

// 根據路徑自動展開
watch(
    () => route.path,
    path => {
        open.value.account = path.startsWith('/account')
        open.value.restaurant = path.startsWith('/restaurant')
        open.value.subscription = path.startsWith('/subscription')
    },
    { immediate: true }
)

// 判斷 active 狀態
const isAccountActive            = computed(() => route.path.startsWith('/account'))
const isRestaurantActive         = computed(() => route.path.startsWith('/restaurant'))
const isReviewActive             = computed(() => route.path.startsWith('/review'))
const isDiscountActive           = computed(() => route.path.startsWith('/discount'))
const isSubscriptionPlanActive   = computed(() => route.path === '/subscription/plan')
const isSubscriptionRecordActive = computed(() => route.path === '/subscription/record')
const isRestaurantTagActive      = computed(() => route.path.startsWith('/restaurantTag'))
const isFoodTagActive            = computed(() => route.path.startsWith('/foodTag'))
const isRecommendationActive     = computed(() => route.path.startsWith('/recommendation'))
</script>


<style scoped>
.brand-title {
color: #5c3203;
font-weight: bold;
font-size: 1.5rem;
}
.page-wrapper {
display: flex;
flex-direction: column;
height: 100vh;
background-color: #fdfaf5;
}
.main-container {
display: flex;
flex-grow: 1;
overflow: hidden;
}
.sidebar {
flex-shrink: 0;
width: 250px;
min-width: 200px;
overflow-y: auto;
border-right: 1px solid #dee2e6;
background-color: #f7f7f7;
padding: 2rem 1rem 2rem 1rem;
height: 100%;
}
.sidebar.open {
transform: translateX(0);
}
.sidebar-sticky {
position: sticky;
top: 0;
}
.sidebar-section {
margin-bottom: 2rem;
}
.list-group-item {
background: none;
border: none;
font-size: 15px;
color: #6c757d;
border-radius: 6px;
padding: 0.75rem 1rem;
display: flex;
align-items: center;
cursor: pointer;
transition: all 0.2s;
}
.list-group-item.parent {
font-weight: bold;
}
.list-group-item.child {
padding-left: 2.5rem;
font-weight: normal;
}
.list-group-item.active,
.list-group-item:hover {
background-color: #fcebc1;
color: #eca300;
}
.list-group-item.active i,
.list-group-item:hover i {
color: #eca300;
}
.list-group-item i {
color: #6c757d;
min-width: 18px;
}
.main-content {
flex-grow: 1;
overflow-y: auto;
background-color: white;
}
.breadcrumb-bar {
background: #f0f0f0;
padding: .75rem 1rem;
margin: 0 0 1.5rem 0;
border-radius: 0.5rem;
}
header,
footer {
flex-shrink: 0;
}
</style>
