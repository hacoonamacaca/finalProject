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
                <!-- 商家類型 -->
                <!--required放在v-model -->
                <div class="mb-3">
                    <select class="form-select" v-model="storeType">
                        <option value="" disabled>商家種類 *</option>
                        <option value="餐廳店家">餐廳店家</option>
                        <option value="生鮮雜貨店家">生鮮雜貨店家</option>
                    </select>
                </div>
                <!-- 商家類型 -->
                <!--required放在v-model -->
                <div class="mb-3">
                    <select class="form-select" v-model="storeCategory">
                        <option value="" disabled>商家類型 *</option>
                        <option value="實體餐廳">實體餐廳</option>
                        <option value="雲端廚房/自宅出餐">雲端廚房/自宅出餐</option>
                    </select>
                </div>
                <!-- 選擇商家類型後才顯示 餐點類型 -->
                <!--required放在v-model -->
                <div class="mb-3" v-if="storeCategory">
                    <select class="form-select" v-model="mealType">
                        <option value="" disabled>餐點類型 *</option>
                        <option value="中式料理">中式料理</option>
                        <option value="健康食品">健康食品</option>
                        <option value="台式料理">台式料理</option>
                        <option value="咖啡">咖啡</option>
                        <option value="小吃">小吃</option>
                        <option value="日式料理">日式料理</option>
                        <option value="早餐">早餐</option>
                        <option value="東南亞">東南亞</option>
                        <option value="歐美料理">歐美料理</option>
                        <option value="港式料理">港式料理</option>
                        <option value="甜點">甜點</option>
                        <option value="異國">異國</option>
                        <option value="素食料理">素食料理</option>
                        <option value="非酒精飲料">非酒精飲料</option>
                        <option value="韓式料理">韓式料理</option>
                        <!-- 其他餐點類型 -->
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()

const storeName = ref(route.query.storeName || '')
const storeType = ref(route.query.storeType || '')
const storeCategory = ref('')
const mealType = ref('')
const phone = ref(route.query.phone || '')
const storeIntro = ref(route.query.storeIntro || '')
const files = ref(null);

function onSaveDraft() {
    alert('已儲存草稿，返回主頁')
    router.push('/') // 或其他頁面
}

function onFileChange(event) {
    files.value = event.target.files;
}

function onSubmit() {
    // 送出後自動跳轉到下一步
    router.push('/verifyAddress') // 請改成你要的下一步頁面
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
