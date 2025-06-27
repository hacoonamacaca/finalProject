    <template>
        <div class="admin-layout">
        <!-- Header + 漢堡按鈕 -->
        <header class="admin-header">
            <button class="hamburger" @click="toggleSidebar">☰</button>
            <h3>網站後台管理系統</h3>
        </header>
    
        <div class="d-flex">
            <!-- 側邊欄 -->
            <aside :class="['sidebar', { open: sidebarOpen }]" id="sidebar-wrapper">
            <div class="list-group list-group-flush">
                <!-- 帳號管理 -->
                <div>
                <a class="list-group-item list-group-item-action parent"
                    :class="{ active: isAccountActive }"
                    @click="toggleSection('account')"
                >帳號管理</a>
                <router-link v-if="open.account" to="/account"
                            class="list-group-item list-group-item-action child"
                            :class="{ active: isAccountActive }"
                >帳號相關審核</router-link>
                </div>
    
                <!-- 餐廳管理 -->
                <div>
                <a class="list-group-item list-group-item-action parent"
                    :class="{ active: isRestaurantActive }"
                    @click="toggleSection('restaurant')"
                >餐廳管理</a>
                <router-link v-if="open.restaurant" to="/restaurant"
                            class="list-group-item list-group-item-action child"
                            :class="{ active: isRestaurantActive }"
                >餐廳審核</router-link>
                </div>
    
                <!-- 其餘項目…… -->
                <router-link to="/review" class="list-group-item list-group-item-action parent"
                            :class="{ active: isReviewActive }"
                >評價管理</router-link>
                <router-link to="/discount" class="list-group-item list-group-item-action parent"
                            :class="{ active: isDiscountActive }"
                >優惠活動</router-link>
    
                <div>
                <a class="list-group-item list-group-item-action parent"
                    :class="{ active: isSubscriptionPlanActive }"
                    @click="toggleSection('subscription')"
                >訂閱管理</a>
                <router-link v-if="open.subscription" to="/subscription/plan"
                            class="list-group-item list-group-item-action child"
                            :class="{ active: isSubscriptionPlanActive }"
                >訂閱方案</router-link>
                <router-link v-if="open.subscription" to="/subscription/record"
                            class="list-group-item list-group-item-action child"
                            :class="{ active: isSubscriptionRecordActive }"
                >訂閱紀錄</router-link>
                </div>
    
                <router-link to="/restaurantTag" class="list-group-item list-group-item-action parent"
                            :class="{ active: isRestaurantTagActive }"
                >餐廳標籤</router-link>
                <router-link to="/foodTag" class="list-group-item list-group-item-action parent"
                            :class="{ active: isFoodTagActive }"
                >食物標籤</router-link>
                <router-link to="/recommendation" class="list-group-item list-group-item-action parent"
                            :class="{ active: isRecommendationActive }"
                >網站推薦</router-link>
            </div>
            </aside>
    
            <!-- 主內容區 -->
            <div class="flex-fill">
            <!-- 麵包屑 -->
            <nav aria-label="breadcrumb" class="breadcrumb-bar">
                <ol class="breadcrumb mb-0">
                <li class="breadcrumb-item">
                    <router-link to="/">首頁</router-link>
                </li>
                <li v-if="crumbTitle" class="breadcrumb-item active" aria-current="page">
                    {{ crumbTitle }}
                </li>
                </ol>
            </nav>
    
            <!-- 這裡放 router-view -->
            <div class="p-4">
                <router-view />
            </div>
            </div>
        </div>
        </div>
    </template>
    
    <script setup>
    import { ref, computed, watch } from 'vue'
    import { useRoute } from 'vue-router'
    
    const route = useRoute()
    
    // sidebar 顯示狀態
    const sidebarOpen = ref(true)
    
    // 控制分組
    const open = ref({ account: false, restaurant: false, subscription: false })
    
    // 初始根據 path 展開
    watch(() => route.path, path => {
        open.value.account = path.startsWith('/account')
        open.value.restaurant = path.startsWith('/restaurant')
        open.value.subscription = path.startsWith('/subscription')
    }, { immediate: true })
    
    function toggleSidebar() {
        sidebarOpen.value = !sidebarOpen.value
    }
    
    function toggleSection(sec) {
        open.value[sec] = !open.value[sec]
        // 收合其他
        for (const k of Object.keys(open.value)) {
        if (k !== sec) open.value[k] = false
        }
    }
    
    // active 判斷
    const isAccountActive = computed(() => route.path.startsWith('/account'))
    const isRestaurantActive = computed(() => route.path.startsWith('/restaurant'))
    const isReviewActive = computed(() => route.path.startsWith('/review'))
    const isDiscountActive = computed(() => route.path.startsWith('/discount'))
    const isSubscriptionPlanActive = computed(() => route.path === '/subscription/plan')
    const isSubscriptionRecordActive = computed(() => route.path === '/subscription/record')
    const isRestaurantTagActive = computed(() => route.path.startsWith('/restaurantTag'))
    const isFoodTagActive = computed(() => route.path.startsWith('/foodTag'))
    const isRecommendationActive = computed(() => route.path.startsWith('/recommendation'))
    
    // 麵包屑標題對照表
    const crumbTitle = computed(() => {
        const map = {
        '/promotion': '行銷活動',
        '/product': '我的商品',
        '/coupon': '我的領率',
        '/advertise': '廣告',
        '/feedback': '意見反饋',
        '/expert': '專家諮詢',
        '/profile': '個人資料',
        '/account': '帳號相關審核',
        '/restaurant': '餐廳審核',
        '/review': '評價管理',
        '/discount': '優惠活動',
        '/subscription/plan': '訂閱方案',
        '/subscription/record': '訂閱紀錄',
        '/subscription/status': '訂閱狀態',
        '/restaurantTag': '餐廳標籤',
        '/foodTag': '食物標籤',
        '/recommendation': '網站推薦',
        }
        return map[route.path] || ''
    })
    </script>
    
    <style scoped>
    .admin-header {
        display: flex;
        align-items: center;
        padding: 0.5rem 1rem;
        background: #fff;
        border-bottom: 1px solid #eee;
    }
    .hamburger {
        font-size: 1.5rem;
        background: none;
        border: none;
        cursor: pointer;
        margin-right: 1rem;
    }
    
    /* 佈局 */
    .d-flex { display: flex; }
    .flex-fill { flex: 1; }
    
    /* 側邊欄 */
    .sidebar {
        width: 220px;
        transition: transform 0.2s;
        background: #f8f9fa;
        overflow-y: auto;
    }
    .sidebar.open {
        transform: translateX(0);
    }
    .sidebar:not(.open) {
        transform: translateX(-100%);
    }
    
    /* 麵包屑 */
    .breadcrumb-bar {
        padding: 0.75rem 1rem;
        background: #f8f9fa;
        border-bottom: 1px solid #ddd;
    }
    
    /* active 樣式 */
    .list-group-item.active {
        font-weight: bold;
        background-color: #e9ecef;
    }
    </style>
    