<template>
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
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const crumbTitle = computed(() => {
    // 依你的 PromotionList.vue 範例內容，這裡路徑需包含 /promotion 對應到 '行銷活動'
    const map = {
        '/promotion': '行銷活動',
        '/product': '我的商品',
        '/coupon': '我的領率',
        '/advertise': '廣告',
        '/feedback': '意見反饋',
        '/expert': '專家諮詢',
        '/profile': '個人資料',
        // 其餘舊有對應
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
    // 精確比對 or 動態路徑（你可以依需求改寫判斷）
    return map[route.path] || ''
})
</script>

<style scoped>
/* 可以補充自定義樣式 */
</style>