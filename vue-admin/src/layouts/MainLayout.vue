    <template>
        <div class="app-wrapper">
        <!-- 顶栏 + 汉堡 -->
        <header class="admin-header">
            <button class="hamburger" @click="toggleSidebar">☰</button>
            <h2>網站後台管理系統</h2>
        </header>
    
        <div class="main-container">
            <!-- 抽屉式侧边栏 -->
            <aside class="sidebar" :class="{ open: sidebarOpen }">
            <div class="list-group list-group-flush">
                <!-- 帳號管理 -->
                <div>
                <a
                    class="list-group-item parent"
                    :class="{ active: isAccountActive }"
                    @click="toggle('account')"
                >帳號管理</a>
                <router-link
                    v-if="open.account"
                    to="/account"
                    class="list-group-item child"
                    :class="{ active: isAccountActive }"
                >帳號相關審核</router-link>
                </div>
    
                <!-- 餐廳管理 -->
                <div>
                <a
                    class="list-group-item parent"
                    :class="{ active: isRestaurantActive }"
                    @click="toggle('restaurant')"
                >餐廳管理</a>
                <router-link
                    v-if="open.restaurant"
                    to="/restaurant"
                    class="list-group-item child"
                    :class="{ active: isRestaurantActive }"
                >餐廳審核</router-link>
                </div>
    
                <!-- 評價管理 -->
                <router-link
                to="/review"
                class="list-group-item parent"
                :class="{ active: isReviewActive }"
                >評價管理</router-link>
    
                <!-- 優惠活動 -->
                <router-link
                to="/discount"
                class="list-group-item parent"
                :class="{ active: isDiscountActive }"
                >優惠活動</router-link>
    
                <!-- 訂閱管理 -->
                <div>
                <a
                    class="list-group-item parent"
                    :class="{ active: isSubscriptionPlanActive || isSubscriptionRecordActive }"
                    @click="toggle('subscription')"
                >訂閱管理</a>
                <router-link
                    v-if="open.subscription"
                    to="/subscription/plan"
                    class="list-group-item child"
                    :class="{ active: isSubscriptionPlanActive }"
                >訂閱方案</router-link>
                <router-link
                    v-if="open.subscription"
                    to="/subscription/record"
                    class="list-group-item child"
                    :class="{ active: isSubscriptionRecordActive }"
                >訂閱紀錄</router-link>
                </div>
    
                <!-- 餐廳標籤 -->
                <router-link
                to="/restaurantTag"
                class="list-group-item parent"
                :class="{ active: isRestaurantTagActive }"
                >餐廳標籤</router-link>
                <!-- 食物標籤 -->
                <router-link
                to="/foodTag"
                class="list-group-item parent"
                :class="{ active: isFoodTagActive }"
                >食物標籤</router-link>
                <!-- 網站推薦 -->
                <router-link
                to="/recommendation"
                class="list-group-item parent"
                :class="{ active: isRecommendationActive }"
                >網站推薦</router-link>
            </div>
            </aside>
    
            <!-- 主內容區 -->
            <section class="content">
            <!-- 麵包屑 -->
            <nav aria-label="breadcrumb" class="breadcrumb-bar">
                <ol class="breadcrumb mb-0">
                <li class="breadcrumb-item"><router-link to="/">首頁</router-link></li>
                <li v-if="crumbTitle" class="breadcrumb-item active">{{ crumbTitle }}</li>
                </ol>
            </nav>
    
            <!-- 路由页面输出 -->
            <div class="page-body">
                <router-view/>
            </div>
            </section>
        </div>
        </div>
    </template>
    
    <script setup>
    import { ref, computed, watch } from 'vue'
    import { useRoute } from 'vue-router'
    
    // 路由对象，供 computed/watch 使用
    const route = useRoute()
    
    // 控制侧栏展开/收起
    const sidebarOpen = ref(false)
    
    // 哪些子菜单展开
    const open = ref({
        account: false,
        restaurant: false,
        subscription: false
    })
    
    // 切换同一组菜单展开状态，收起其它组
    function toggle(section) {
        open.value[section] = !open.value[section]
        Object.keys(open.value).forEach(k => {
        if (k !== section) open.value[k] = false
        })
    }
    
    // 根据当前路由，自动展开对应子菜单
    watch(
        () => route.path,
        path => {
        open.value.account     = path.startsWith('/account')
        open.value.restaurant  = path.startsWith('/restaurant')
        open.value.subscription= path.startsWith('/subscription')
        },
        { immediate: true }
    )
    
    // 切换 sidebar
    function toggleSidebar() {
        sidebarOpen.value = !sidebarOpen.value
    }
    
    // Active 状态判断
    const isAccountActive           = computed(() => route.path.startsWith('/account'))
    const isRestaurantActive        = computed(() => route.path.startsWith('/restaurant'))
    const isReviewActive            = computed(() => route.path.startsWith('/review'))
    const isDiscountActive          = computed(() => route.path.startsWith('/discount'))
    const isSubscriptionPlanActive  = computed(() => route.path === '/subscription/plan')
    const isSubscriptionRecordActive= computed(() => route.path === '/subscription/record')
    const isRestaurantTagActive     = computed(() => route.path.startsWith('/restaurantTag'))
    const isFoodTagActive           = computed(() => route.path.startsWith('/foodTag'))
    const isRecommendationActive    = computed(() => route.path.startsWith('/recommendation'))
    
    // 面包屑标题映射
    const crumbTitle = computed(() => {
        const map = {
        '/account':                  '帳號相關審核',
        '/restaurant':               '餐廳審核',
        '/review':                   '評價管理',
        '/discount':                 '優惠活動',
        '/subscription/plan':        '訂閱方案',
        '/subscription/record':      '訂閱紀錄',
        '/subscription/status':      '訂閱狀態',
        '/restaurantTag':            '餐廳標籤',
        '/foodTag':                  '食物標籤',
        '/recommendation':           '網站推薦',
        }
        return map[route.path] || ''
    })
    </script>
    
    <style scoped>
    .app-wrapper { height:100vh; display:flex; flex-direction:column; }
    .admin-header {
        display:flex; align-items:center;
        padding:.5rem 1rem;
        background:#fff; border-bottom:1px solid #eee;
    }
    .hamburger {
        font-size:1.5rem;
        background:none; border:none;
        cursor:pointer; margin-right:1rem;
    }
    
    .main-container { flex:1; display:flex; overflow:hidden; }
    .sidebar {
        width:240px; background:#f8f9fa;
        border-right:1px solid #ddd;
        transform:translateX(-100%);
        transition:transform .2s;
    }
    .sidebar.open { transform:translateX(0); }
    .content { flex:1; overflow:auto; }
    
    /* 面包屑 */
    .breadcrumb-bar {
        padding:.75rem 1rem;
        background:#f0f0f0;
        margin:0;
    }
    
    /* 页面主体 */
    .page-body { padding:1rem; }
    
    /* Active 项 font-weight */
    .list-group-item.parent.active,
    .list-group-item.child.active {
        font-weight: bold;
    }
    </style>
    