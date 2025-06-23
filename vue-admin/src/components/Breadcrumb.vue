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
    const map = {
        '/account': '帳號相關審核',
        '/restaurant': '餐廳審核',
        '/review': '評價管理',
        '/discount': '優惠活動',
        '/subscription': '訂閱方案',
        '/subscription/record': '訂閱紀錄',
        '/subscription/status': '訂閱狀態',
    }
    return map[route.path] || ''
})
</script>


<style scoped>

</style>