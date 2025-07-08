<template>
    <div class="d-flex flex-column align-items-center justify-content-center" style="min-height: 80vh;">
        <h2 class="mb-4">註冊資料待審核</h2>
        <div class="mb-3 text-secondary">
            嗨，感謝您的註冊，待審核後會通知您。
        </div>
        <hr class="w-100" />
        <button class="btn btn-main my-4 px-5" @click="goHome">
            返回首頁
        </button>
        <div v-if="errorMsg" class="text-danger mt-2">{{ errorMsg }}</div>
        <div v-if="successMsg" class="text-success mt-2">{{ successMsg }}</div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
// import { useStoreRegister } from '@/stores/user.js'  // 不用再引入 storeRegister
// import axios from 'axios'  // 不用再引入 axios

const router = useRouter()
// 可依需求，預留訊息欄位（例如從其他頁 router.push 時可以帶 query 顯示）
const errorMsg = ref('')
const successMsg = ref('')

// onMounted 不需要了！
// 只需要一個 goHome function
function goHome() {
    router.push('/')
}
</script>

<style scoped>
.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    height: 48px;
    border-radius: 24px;
    border: none;
    letter-spacing: 2px;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
}

.btn-main:hover {
    filter: brightness(1.08);
    background: #ffba20;
}
</style>
