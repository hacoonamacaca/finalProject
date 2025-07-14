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
                <input v-model="localProfile.name" type="text" class="form-control rounded-pill" placeholder="請輸入餐廳名稱" />
            </div>
            <!-- 餐廳地址 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳地址</label>
                <input v-model="localProfile.address" type="text" class="form-control rounded-pill" placeholder="請輸入餐廳地址" />
            </div>
            <!-- 餐廳介紹 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳介紹</label>
                <textarea v-model="localProfile.intro" class="form-control" rows="4" placeholder="請輸入餐廳介紹" style="resize:vertical"></textarea>
            </div>
            <!-- 餐廳照片 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">餐廳照片</label>
                <input type="file" multiple class="form-control" @change="onFileChange" />
            </div>
            <!-- 手機號碼 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">手機號碼</label>
                <input v-model="localProfile.phone" class="form-control rounded-pill" placeholder="0912345678" />
            </div>
            <!-- 電子郵件 -->
            <div class="mb-2 text-center">
                <label class="form-label w-100 text-start">電子郵件</label>
                <input type="email" v-model="localProfile.email" class="form-control rounded-pill" placeholder="請輸入 email" />
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
import { ref, computed, onMounted, reactive, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.js'
import axios from '@/plungins/axios.js'
import { uploadImage } from '@/plungins/firebase-storage.js'

const router = useRouter()
const userStore = useUserStore()
const photoFile = ref(null); // 上傳圖片

const localProfile = reactive({
    name: '',
    address: '',
    intro: '',
    phone: '',
    email: '',
    isEmailVerified: false,
    lat: null,
    lon: null,
})

onMounted(async () => {
    await userStore.fetchStoreProfile?.()
})

// 只要 pinia 有更新，localProfile 就同步（雙向）
watchEffect(() => {
    const p = userStore.storeProfile
    if (p) {
        localProfile.name = p.name || ''
        localProfile.address = p.address || ''
        localProfile.intro = p.storeIntro || ''
        localProfile.phone = p.phone || ''
        localProfile.email = p.email || ''
        localProfile.isEmailVerified = p.isEmailVerified || false
        localProfile.lat = p.lat ?? null
        localProfile.lon = p.lon ?? null
    }
})

// 表單異動判斷（對照 pinia 狀態）
const isDirty = computed(() =>
    Object.keys(localProfile).some(
        key => localProfile[key] !== userStore.storeProfile[key]
    ) || photoFile.value
)

// Email 驗證顯示
const isEmailVerified = computed(() =>
    localProfile.email === userStore.storeProfile.email && userStore.storeProfile.isEmailVerified
)

// 檔案選擇
function onFileChange(e) {
    photoFile.value = e.target.files[0] || null
}

// 儲存表單
async function handleSave() {
    if (!localProfile.name || !localProfile.address) {
        alert("餐廳名稱/地址必填")
        return
    }

    let photoUrl = userStore.storeProfile.photo || ""; // 預設維持舊照

    // 有新照片才傳
    if (photoFile.value) {
        photoUrl = await uploadImage(photoFile.value, "stores")
    }

    const storeId = userStore.storeProfile.id
    try {
        const resp = await axios.put(`/api/stores/${storeId}`, {
            name: localProfile.name,
            address: localProfile.address,
            storeIntro: localProfile.intro,
            phone: localProfile.phone,
            email: localProfile.email,
            photo: photoUrl, // 存網址
            lat: localProfile.lat,
            lon: localProfile.lon,
        });
        if (resp.data) {
            alert('儲存成功！')
            userStore.setStoreProfile({ ...localProfile, photo: photoUrl })
            photoFile.value = null
        } else {
            alert('儲存失敗：' + (resp.data.message || ''))
        }
    } catch (err) {
        alert('發生錯誤：' + (err?.message || err))
    }
}

// 返回
function goBack() {
    router.push('/store')
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