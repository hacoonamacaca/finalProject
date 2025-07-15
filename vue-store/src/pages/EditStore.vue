<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">餐廳資料</h3>
            <i class="text-secondary ms-2"></i>
        </div>

        <!-- 🔥 NEW: 載入中狀態 - 最小化加入 -->
        <div v-if="isLoading" class="text-center py-5">
            <div class="spinner-border text-warning" role="status">
                <span class="visually-hidden">載入中...</span>
            </div>
            <p class="mt-3">載入店家資料中...</p>
        </div>

        <div v-else class="mx-auto" style="max-width: 420px;">
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
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mb-2" :disabled="!isDirty || isSaving"
                @click="handleSave">
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                {{ isSaving ? '儲存中...' : '儲存' }}
            </button>
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mt-2 mb-4" @click="goBack">
                返回
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watchEffect, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.js'
import { useStore } from '@/composables/useStore.js' // 🔥 NEW: 僅為了店家切換
import axios from '@/plungins/axios.js'
import { uploadImage } from '@/plungins/firebase-storage.js'

const router = useRouter()
const userStore = useUserStore()

// 🔥 NEW: 加入 stores 來同步更新下拉選單
const { selectedStore, stores } = useStore()

const photoFile = ref(null) // 上傳圖片

// 🔥 NEW: 最小化加入載入和儲存狀態
const isLoading = ref(false)
const isSaving = ref(false)

// 🔥 原始設計 - 保持不變
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

// 🔥 NEW: 擴展 fetchStoreProfile 來支援特定店家 ID
const fetchStoreProfile = async (storeId = null) => {
    try {
        isLoading.value = true
        console.log(`🚀 [EditStore] 載入店家資料 (storeId: ${storeId})`)
        
        if (storeId) {
            // 🔥 NEW: 載入特定店家的詳細資料
            const response = await axios.get(`/api/stores/${storeId}`)
            if (response.data) {
                userStore.setStoreProfile(response.data)
                console.log('✅ [EditStore] 載入特定店家資料成功:', response.data)
            }
        } else {
            // 🔥 保持原始邏輯 - 載入預設店家
            await userStore.fetchStoreProfile?.()
        }
    } catch (error) {
        console.error('❌ [EditStore] 載入店家資料失敗:', error)
    } finally {
        isLoading.value = false
    }
}

// 🔥 原始設計 - 完全保持不變
onMounted(async () => {
    // 🔥 修正：優先載入當前選中的店家，而不是預設店家
    const targetStoreId = selectedStore.value
    if (targetStoreId) {
        console.log(`🎯 [EditStore] onMounted - 載入當前選中的店家 ${targetStoreId}`)
        await fetchStoreProfile(targetStoreId)
    } else {
        console.log(`🎯 [EditStore] onMounted - 沒有選中店家，載入預設店家`)
        await fetchStoreProfile()
    }
    console.log('[onMounted] userStore.storeProfile:', JSON.stringify(userStore.storeProfile, null, 2))
})

// 🔥 NEW: 最小化加入店家切換監聽 - 使用組員相同的邏輯模式
watch(selectedStore, async (newStoreId, oldStoreId) => {
    if (newStoreId && newStoreId !== oldStoreId) {
        console.log(`🔄 [EditStore] 店家切換: ${oldStoreId} → ${newStoreId}`)
        await fetchStoreProfile(newStoreId)
    }
}, { immediate: false })

// 🔥 原始設計 - 完全保持不變，這個設計真的很優雅！
watchEffect(() => {
    const p = userStore.storeProfile
    if (p) {
        // debug: 每次 pinia 資料同步也 log 一下
        console.log('[watchEffect] Sync localProfile with userStore.storeProfile:', JSON.stringify(p, null, 2))
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

// 🔥 原始設計 - 保持不變，但加入 null 安全檢查
const isDirty = computed(() => {
    if (!userStore.storeProfile) return false
    return Object.keys(localProfile).some(
        key => localProfile[key] !== userStore.storeProfile[key]
    ) || photoFile.value
})

// 🔥 原始設計 - 完全保持不變
const isEmailVerified = computed(() =>
    localProfile.email === userStore.storeProfile?.email && userStore.storeProfile?.isEmailVerified
)

// 🔥 原始設計 - 完全保持不變
function onFileChange(e) {
    photoFile.value = e.target.files[0] || null
    console.log('[onFileChange] photoFile:', photoFile.value)
}

// 🔥 原始設計 - 僅加入 isSaving 狀態指示
async function handleSave() {
    if (!localProfile.name || !localProfile.address) {
        alert("餐廳名稱/地址必填")
        return
    }

    try {
        isSaving.value = true // 🔥 NEW: 加入儲存狀態

        let photoUrl = userStore.storeProfile.photo || ""; // 預設維持舊照

        // 有新照片才傳
        if (photoFile.value) {
            photoUrl = await uploadImage(photoFile.value, "stores")
            console.log('[handleSave] uploadImage result photoUrl:', photoUrl)
        }

        const storeId = userStore.storeProfile.id
        // debug: 儲存前 log 關鍵資料
        console.log('[handleSave] storeId:', storeId)
        console.log('[handleSave] localProfile:', JSON.stringify(localProfile, null, 2))
        console.log('[handleSave] storeProfile:', JSON.stringify(userStore.storeProfile, null, 2))

        const payload = {
            name: localProfile.name,
            address: localProfile.address,
            storeIntro: localProfile.intro,
            phone: localProfile.phone,
            email: localProfile.email,
            photo: photoUrl, // 存網址
            lat: localProfile.lat,
            lon: localProfile.lon,
        }
        console.log('[handleSave] axios.put payload:', payload)

        const resp = await axios.put(`/api/stores/${storeId}`, payload)
        console.log('[handleSave] axios response:', resp)

        if (resp.data) {
            alert('儲存成功！')
            
            // 🔥 修正：正確對應欄位名稱，避免 intro/storeIntro 混亂
            const updatedStoreProfile = {
                ...userStore.storeProfile,
                name: localProfile.name,
                address: localProfile.address,
                storeIntro: localProfile.intro, // 🔥 關鍵：intro → storeIntro
                phone: localProfile.phone,
                email: localProfile.email,
                photo: photoUrl,
                lat: localProfile.lat,
                lon: localProfile.lon,
            }
            userStore.setStoreProfile(updatedStoreProfile)
            
            // 🔥 NEW: 同步更新下拉選單中的店家名稱
            const currentStore = stores.value.find(store => store.id === storeId)
            if (currentStore && currentStore.name !== localProfile.name) {
                currentStore.name = localProfile.name
                console.log('✅ [EditStore] 已同步更新下拉選單中的店家名稱')
            }
            
            photoFile.value = null
        } else {
            alert('儲存失敗：' + (resp.data.message || ''))
        }
    } catch (err) {
        console.error('[handleSave] error:', err)
        alert('發生錯誤：' + (err?.message || err))
    } finally {
        isSaving.value = false // 🔥 NEW: 重設儲存狀態
    }
}

// 🔥 原始設計 - 完全保持不變
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

/* 🔥 NEW: 最小化加入載入狀態樣式 */
.spinner-border {
    border-width: 0.2em;
}

.spinner-border-sm {
    width: 1rem;
    height: 1rem;
    border-width: 0.1em;
}
</style>