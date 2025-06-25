<template>
    <div class="sidebar bg-light border-end" id="sidebar-wrapper">
        <div class="list-group list-group-flush">
            <!-- 帳號管理 -->
            <div>
                <a
                    class="list-group-item list-group-item-action parent"
                    :class="{ active: isAccountActive }"
                    @click="toggle('account')"
                >帳號管理</a>
                <router-link
                    v-if="open.account"
                    to="/account"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isAccountActive }"
                >帳號相關審核</router-link>
            </div>

            <!-- 餐廳管理 -->
            <div>
                <a
                    class="list-group-item list-group-item-action parent"
                    :class="{ active: isRestaurantActive }"
                    @click="toggle('restaurant')"
                >餐廳管理</a>
                <router-link
                    v-if="open.restaurant"
                    to="/restaurant"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isRestaurantActive }"
                >餐廳審核</router-link>
            </div>

            <!-- 評價管理 -->
            <router-link
                to="/review"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isReviewActive }"
            >評價管理</router-link>

            <!-- 優惠活動 -->
            <router-link
                to="/discount"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isDiscountActive }"
            >優惠活動</router-link>

            <!-- 訂閱管理 -->
            <div>
                <a
                    class="list-group-item list-group-item-action parent"
                    :class="{ active: isSubscriptionPlanActive }"
                    @click="toggle('subscription')"
                >訂閱管理</a>
                <router-link
                    v-if="open.subscription"
                    to="/subscription/plan"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isSubscriptionPlanActive }"
                >訂閱方案</router-link>
                <router-link
                    v-if="open.subscription"
                    to="/subscription/record"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isSubscriptionRecordActive }"
                >訂閱紀錄</router-link>
            </div>

            <!-- 餐廳標籤 -->
            <router-link
                to="/restaurantTag"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isRestaurantTagActive }"
            >餐廳標籤</router-link>
            <!-- 食物標籤 -->
            <router-link
                to="/foodTag"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isFoodTagActive }"
            >食物標籤</router-link>
            <!-- 網站推薦 -->
            <router-link
                to="/recommendation"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isRecommendationActive }"
            >網站推薦</router-link>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 控制展開哪些分組
const open = ref({
    account: false,
    restaurant: false,
    subscription: false
})

// 依 route 自動展開對應子選單
watch(() => route.path, (path) => {
    open.value.account = path.startsWith('/account')
    open.value.restaurant = path.startsWith('/restaurant')
    open.value.subscription = path.startsWith('/subscription')
}, { immediate: true })

const toggle = (section) => {
    open.value[section] = !open.value[section]
    // 收合其他
    Object.keys(open.value).forEach(key => {
        if (key !== section) open.value[key] = false
    })
}

// active 狀態判斷
const isAccountActive = computed(() => route.path.startsWith('/account'))
const isRestaurantActive = computed(() => route.path.startsWith('/restaurant'))
const isReviewActive = computed(() => route.path.startsWith('/review'))
const isDiscountActive = computed(() => route.path.startsWith('/discount'))
const isSubscriptionPlanActive = computed(() => route.path === '/subscription/plan')
const isSubscriptionRecordActive = computed(() => route.path === '/subscription/record')
const isSubscriptionStatusActive = computed(() => route.path === '/subscription/status')
const isRestaurantTagActive = computed(() => route.path.startsWith('/restaurantTag'))
const isFoodTagActive = computed(() => route.path.startsWith('/foodTag'))
const isRecommendationActive = computed(() => route.path.startsWith('/recommendation'))
</script>

<style scoped>
</style>