<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">餐廳業者資料</h3>
            <i class="text-secondary ms-2"></i>
        </div>
        <div class="mx-auto" style="max-width: 420px;">
            <!-- 餐廳名稱 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳名稱</label>
                <input v-model="storeName" type="text" class="form-control rounded-pill" placeholder="請輸入餐廳名稱" />
            </div>
            <!-- 餐廳地址 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳地址</label>
                <input v-model="address" type="text" class="form-control rounded-pill" placeholder="請輸入餐廳地址" />
            </div>
            <!-- 餐廳介紹 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳介紹</label>
                <textarea v-model="intro" class="form-control" rows="4" placeholder="請輸入餐廳介紹" style="resize:vertical"></textarea>
            </div>
            <!-- 餐廳照片 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳照片</label>
                <input type="file" multiple class="form-control" @change="onFileChange" />
            </div>
            <!-- 手機號碼 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">手機號碼</label>
                <input v-model="phone" class="form-control rounded-pill" placeholder="0912345678" />
            </div>
            <!-- 電子郵件 -->
            <div class="mb-2 text-center">
                <label class="form-label w-100 text-start">電子郵件</label>
                <input type="email" v-model="emailLocal" class="form-control rounded-pill" placeholder="請輸入 email" />
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
                <i class="bi me-2"
                    :class="isEmailVerified ? 'bi-check-circle-fill text-success' : 'bi-exclamation-circle-fill text-warning'"></i>
                <small class="text-secondary">
                    {{ isEmailVerified ? '已驗證' : '未驗證' }}
                </small>
            </div>
            <!-- 合併一顆儲存按鈕 -->
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mb-2" :disabled="!isDirty"
                @click="handleSave">
                儲存
            </button>
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mt-2 mb-4" @click="goBack">
                返回
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()

function goBack() {
    router.push('/store')
}

const initial = reactive({
    storeName: '',
    address: '',
    intro: '',
    phone: '',
    email: '',
    isEmailVerified: true,
    photos: []
})

const storeName = ref('')
const address = ref('')
const intro = ref('')
const phone = ref('')
const emailLocal = ref('')
const photos = ref([]) // 多檔案

onMounted(() => {
    storeName.value = localStorage.getItem('storeName') || ''
    address.value = localStorage.getItem('storeAddress') || ''
    intro.value = localStorage.getItem('storeIntro') || ''
    phone.value = localStorage.getItem('userPhone') || ''
    emailLocal.value = localStorage.getItem('userEmail') || ''

    // 初始化
    initial.storeName = storeName.value
    initial.address = address.value
    initial.intro = intro.value
    initial.phone = phone.value
    initial.email = emailLocal.value
    initial.isEmailVerified = true
})

const isDirty = computed(() => {
    return (
        storeName.value !== initial.storeName ||
        address.value !== initial.address ||
        intro.value !== initial.intro ||
        phone.value !== initial.phone ||
        emailLocal.value !== initial.email ||
        photos.value.length > 0 // 有新照片也算異動
    )
})

const isEmailVerified = computed(() =>
    emailLocal.value === initial.email && initial.isEmailVerified
)

function onFileChange(e) {
    photos.value = Array.from(e.target.files)
}

function handleSave() {
    if (storeName.value !== initial.storeName) {
        initial.storeName = storeName.value
        localStorage.setItem('storeName', storeName.value)
    }
    if (address.value !== initial.address) {
        initial.address = address.value
        localStorage.setItem('storeAddress', address.value)
    }
    if (intro.value !== initial.intro) {
        initial.intro = intro.value
        localStorage.setItem('storeIntro', intro.value)
    }
    if (phone.value !== initial.phone) {
        initial.phone = phone.value
        localStorage.setItem('userPhone', phone.value)
    }
    if (emailLocal.value !== initial.email) {
        initial.email = emailLocal.value
        initial.isEmailVerified = false
        localStorage.setItem('userEmail', emailLocal.value)
    }
    // 儲存照片（此處僅 demo，通常你要上傳到後端）
    if (photos.value.length > 0) {
        // 這裡只是示意，如果你要上傳給 API，請改用 FormData 並送出
        alert(`共選擇了 ${photos.value.length} 張照片，請自行串接API上傳！`)
        // 儲存照片清空
        photos.value = []
    }
}
</script>

<style scoped>
.form-control.rounded-pill {
    border-radius: 50px;
}

.btn.rounded-pill {
    border-radius: 50px;
}

.btn.btn-primary {
    background-color: #ffba20;
    border-color: #ffba20;
    color: #fff;
}

.btn.btn-primary:disabled {
    background-color: #d5d5d5;
    border-color: #d5d5d5;
    color: #fff;
}

.btn.btn-primary:hover:not(:disabled) {
    background-color: #f1cd78;
    border-color: #f1cd78;
}
</style>