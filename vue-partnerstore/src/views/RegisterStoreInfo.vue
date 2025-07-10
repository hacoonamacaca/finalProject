<template>
    <div class="store-info-bg py-5">
        <div class="container" style="max-width: 560px;">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 class="fw-bold mb-0">告訴我們您的商家資訊</h3>
                <button class="btn btn-outline-secondary btn-sm" @click="onSaveDraft">儲存並離開</button>
            </div>
            <div class="mb-3 text-secondary" style="font-size:15px;">
                此訊息將顯示在 金碗 平台上，以便消費者有任何問題時可以搜尋並與您聯繫。
            </div>
            <form @submit.prevent="onSubmit">
                <!-- 餐廳名稱 -->
                <!--required放在v-model -->
                <div class="mb-3">
                    <input type="text" class="form-control" v-model="storeName" placeholder="餐廳或商店名稱 *" />
                    <span class="qmark" tabindex="0" title="請填寫完整店名">
                        <svg width="19" height="19" fill="none" viewBox="0 0 24 24">
                            <circle cx="12" cy="12" r="10" stroke="#ffba20" stroke-width="2" />
                            <text x="7" y="16" font-size="13" fill="#ffba20">?</text>
                        </svg>
                    </span>
                </div>
                <!-- 選擇商家類型後才顯示 餐點類型 -->
                <!--required放在v-model -->
                <div class="mb-3">
                    <select class="form-select" v-model="storeCategory">
                        <option value="" disabled>餐廳類型 *</option>
                        <option v-for="c in categories" :key="c.id" :value="c.name">{{ c.name }}</option>
                    </select>
                </div>
                <!-- 電話 -->
                <!--required放在v-model -->
                <div class="mb-3 d-flex align-items-center">
                    <div class="flag-box d-flex align-items-center px-2 me-2">
                        <img src="https://flagcdn.com/h20/tw.png" alt="台灣" style="width:22px; height:16px" />
                        <span class="ms-1">+886</span>
                    </div>
                    <input type="tel" class="form-control" v-model="phone" pattern="[0-9]{9,10}" placeholder="行動電話 *"
                    style="flex:1" />
                </div>
                <!-- 餐廳介紹 -->
                <div class="mb-3">
                    <textarea v-model="storeIntro" class="form-control"  placeholder="餐廳介紹" rows="4" style="resize: vertical; min-height: 90px;">
                    </textarea>
                </div>
                <!-- 餐廳照片 -->
                <div class="mb-3">
                    <input type="file" class="form-control" multiple @change="onFileChange" />
                </div>
                <!-- 下一步 -->
                <div class="d-flex justify-content-end">
                    <button class="btn btn-main px-4" style="font-size:18px;" type="submit">繼續</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStoreRegister } from '@/stores/user.js'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const regStore = useStoreRegister()

const storeName = ref('')
const storeCategory = ref('')
const phone = ref('')
const storeIntro = ref('')
const files = ref(null);
const categories = ref([])

// 處裡多檔案選擇
function onFileChange(e) {
    files.value = e.target.files;
}


onMounted(async () => {
    // 驗證 ownerId
    let ownerId = route.query.ownerId || regStore.ownerId || localStorage.getItem('registerOwnerId');
    if (!ownerId) {
        alert('請先註冊或登入')
        router.push('/')
        return
    }
    regStore.ownerId = ownerId + ''
    localStorage.setItem('registerOwnerId', ownerId + '')

    // 若有 storeId 也帶上
    let storeId = route.query.storeId || regStore.storeId || localStorage.getItem('registerStoreId')
    if (storeId) {
        regStore.storeId = storeId + ''
        localStorage.setItem('registerStoreId', storeId + '')
    }

    storeName.value = regStore.storeName || route.query.storeName || ''
    phone.value = regStore.phone || route.query.phone || ''
    storeIntro.value = regStore.storeIntro || route.query.storeIntro || ''
    storeCategory.value = regStore.storeCategory || ''

    // 載入類別選單
    try {
        const res = await axios.get('/api/category/all')
        categories.value = res.data
    } catch (e) {
        categories.value = []
        console.error('載入分類失敗', e)
    }
})

// 表單送出
async function onSubmit() {
    const ownerId = regStore.ownerId || localStorage.getItem('registerOwnerId')
    if (!ownerId || !storeName.value || !storeCategory.value) {
        alert('請完整填寫店名和類型')
        return
    }

    // 建立 FormData（多筆欄位 + 多張照片）
    const formData = new FormData()
    formData.append('ownerId', ownerId + '')
    formData.append('name', storeName.value)
    formData.append('storeCategory', storeCategory.value)
    formData.append('storeIntro', storeIntro.value)
    formData.append('phone', phone.value)

    if (files.value && files.value.length > 0) {
        for (let i = 0; i < files.value.length; i++) {
            formData.append('photos', files.value[i])
        }
    }
    
    // Debug 1: 印出formData實際內容
    for (let pair of formData.entries()) {
        console.log(pair[0] + ', ' + pair[1])
    }
    
    // Debug 2: 印出檔案型別
    if (files.value && files.value.length > 0) {
        for (let i = 0; i < files.value.length; i++) {
            console.log('檔案', i, files.value[i].name, files.value[i].type)
        }
    } else {
        console.log('沒有檔案')
    }

    try {
        // Debug 3: axios預設不設 headers
        const res = await axios.post('/api/store/registerInfo', formData)
        // Debug 4: 印response
        console.log('後端回應', res.data)
        
        if (res.data.success) {
            regStore.storeId = res.data.storeId + ''
            localStorage.setItem('registerStoreId', res.data.storeId + '')
            regStore.setBasicInfo({
                ownerId: ownerId + '',
                storeName: storeName.value,
                storeCategory: storeCategory.value,
                phone: phone.value,
                storeIntro: storeIntro.value
            })
            router.push({
                path: '/verifyAddress',
                query: { ownerId: ownerId + '', storeId: res.data.storeId + '' }
            })
        } else {
            alert(res.data.message || '商家資訊送出失敗')
        }
    } catch (e) {
        // Debug 5: 印錯誤訊息
        if(e.response) {
            // 回傳內容
            console.error('錯誤response', e.response)
            alert('伺服器錯誤 (HTTP ' + e.response.status + ')：' + (e.response.data?.message || ''))
        } else {
            console.error('發送失敗:', e)
            alert('伺服器錯誤，請稍後再試')
        }
    }
}

// 儲存草稿
function onSaveDraft() {
    regStore.setBasicInfo({
        ownerId: regStore.ownerId,
        storeName: storeName.value,
        storeCategory: storeCategory.value,
        phone: phone.value,
        storeIntro: storeIntro.value,
        files: files.value
    })
    alert('已儲存草稿，返回主頁')
    router.push('/')
}
</script>

<style scoped>
.store-info-bg {
    min-height: 100vh;
    background: #fafafb;
}

.container {
    background: #fff;
    border-radius: 13px;
    box-shadow: 0 2px 18px 4px rgba(0, 0, 0, 0.08);
    padding: 38px 24px 28px 24px;
}

.qmark {
    position: absolute;
    right: 10px;
    top: 13px;
    cursor: pointer;
    display: flex;
    align-items: center;
}

.flag-box {
    background: #f7f7f7;
    border-radius: 7px;
    border: 1px solid #eee;
    height: 46px;
}

.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    border-radius: 10px;
    border: none;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
}

.btn-main:hover {
    filter: brightness(1.08);
    background: #ffba20;
}

.text-pink {
    color: #ffba20;
}
</style>
