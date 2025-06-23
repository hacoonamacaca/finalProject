<template>
    <div class="sidebar bg-light border-end" id="sidebar-wrapper">
        <div class="list-group list-group-flush">
            <!-- 帳號管理 -->
            <div>
                <a
                    class="list-group-item list-group-item-action parent"
                    :class="{ active: isAccountActive }"
                    @click="toggleAccount"
                >帳號管理</a>
                <router-link
                    v-if="showAccount"
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
                    @click="toggleRestaurant"
                >餐廳管理</a>
                <router-link
                    v-if="showRestaurant"
                    to="/restaurant"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isRestaurantActive }"
                >餐廳審核</router-link>
            </div>
            <div>
            <!-- 評價管理 -->
            <router-link
                to="/review"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isReviewActive }"
                @click="toggleReview"
            >評價管理</router-link>
            </div>
            <div>
            <!-- 優惠活動 -->
            <router-link
                to="/discount"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isDiscountActive }"
                @click="toggleDiscount"
            >優惠活動</router-link>
            </div>
            <!-- 訂閱管理 -->
            <div>
                <a
                    class="list-group-item list-group-item-action parent"
                    :class="{ active: isSubscriptionActive }"
                    @click="toggleSubscription"
                >訂閱管理</a>
                <router-link
                    v-if="showSubscription"
                    to="/subscription"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isSubscriptionActive }"
                >訂閱方案</router-link>
                <router-link
                    v-if="showSubscription"
                    to="/subscription/record"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isSubscriptionRecordActive }"
                >訂閱紀錄</router-link>
                <router-link
                    v-if="showSubscription"
                    to="/subscription/status"
                    class="list-group-item list-group-item-action child"
                    :class="{ active: isSubscriptionStatusActive }"
                >訂閱狀態</router-link>
            </div>
            <div>
            <!-- 餐廳標籤 -->
            <router-link
                to="/restaurantTag"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isRestaurantTagActive }"
            >餐廳標籤</router-link>
            </div>
            <div>
            <!-- 食物標籤 -->
            <router-link
                to="/foodTag"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isFoodTagActive }"
            >食物標籤</router-link>
            </div>
            <div>
            <!-- 食物標籤 -->
            <router-link
                to="/recommendation"
                class="list-group-item list-group-item-action parent"
                :class="{ active: isRecommendationActive }"
            >網站推薦</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'

const showAccount = ref(false)
const showRestaurant = ref(false)
const showReview = ref(false)
const showDiscount = ref(false)
const showSubscription = ref(false)
const route = useRoute()

const isAccountActive = computed(() => route.path.startsWith('/account'))
const isRestaurantActive = computed(() => route.path.startsWith('/restaurant'))
const isReviewActive = computed(() => route.path.startsWith('/review'))
const isDiscountActive = computed(() => route.path.startsWith('/discount'))
const isSubscriptionActive = computed(() => route.path === '/subscription')
const isSubscriptionRecordActive = computed(() => route.path === '/subscription/record')
const isSubscriptionStatusActive = computed(() => route.path === '/subscription/status')
// 自動展開對應分組
watch(
    () => route.path,
    (path) => {
        showAccount.value = path.startsWith('/account')
        showRestaurant.value = path.startsWith('/restaurant')
        showReview.value = path.startsWith('/review')
        showDiscount.value = path.startsWith('/discount')
        showSubscription.value = path.startsWith('/subscription')
    },
    { immediate: true }
)

const toggleAccount = () => {
    showAccount.value = !showAccount.value
    if (showAccount.value) {
        showRestaurant.value = false
        showReview.value = false
        showDiscount.value = false
        showSubscription.value = false
    }
}
const toggleRestaurant = () => {
    showRestaurant.value = !showRestaurant.value
    if( showRestaurant.value) {
        showAccount.value = false
        showReview.value = false
        showDiscount.value = false
        showSubscription.value = false
    }
}
const toggleReview = () => {
    showReview.value = !showReview.value
    if (showReview.value) {
        showAccount.value = false
        showRestaurant.value = false
        showDiscount.value = false
        showSubscription.value = false
    }
}
const toggleDiscount = () => {
    showDiscount.value = !showDiscount.value
    if (showDiscount.value) {
        showAccount.value = false
        showRestaurant.value = false
        showReview.value = false
        showSubscription.value = false
    }
}
const toggleSubscription = () => {
    showSubscription.value = !showSubscription.value
    if (showSubscription.value) {
        showAccount.value = false
        showRestaurant.value = false
        showReview.value = false
        showDiscount.value = false
    }
}
</script>

<style scoped>
</style>
