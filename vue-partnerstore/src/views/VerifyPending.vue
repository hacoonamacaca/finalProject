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
import { onMounted, ref } from 'vue'
import { useStoreRegister } from '@/stores/user.js'
import axios from 'axios'

const router = useRouter()
const storeRegister = useStoreRegister()
const errorMsg = ref('')
const successMsg = ref('')

onMounted(async () => {
    // 只要進這頁，直接送一次資料
    try {
        // 你可以根據你的資料結構做調整
        const res = await axios.post('/api/store/registerInfo', {
            ownerId: storeRegister.ownerId,
            name: storeRegister.storeName,
            storeCategory: storeRegister.storeCategory,
            phone: storeRegister.phone,
            storeIntro: storeRegister.storeIntro,
            address: [
                storeRegister.zip,
                storeRegister.city,
                storeRegister.district,
                storeRegister.street,
                storeRegister.door
            ].filter(Boolean).join(' '),
            enAddress: storeRegister.enAddress,
            lat: storeRegister.lat,
            lon: storeRegister.lon
            // 如有上傳圖片，也一併帶上
        })
        if (res.data.success) {
            successMsg.value = '資料已送出，請等待審核通知！'
        } else {
            errorMsg.value = res.data.message || '送出失敗'
        }
    } catch (e) {
        errorMsg.value = '伺服器錯誤，請稍後再試'
    }
})

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
