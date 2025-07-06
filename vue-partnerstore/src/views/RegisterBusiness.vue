<template>
    <div class="register-bg d-flex align-items-center justify-content-center">
        <div class="register-card px-3 py-4">
            <h4 class="mb-4 fw-bold text-dark text-center" style="font-size:22px;">
                準備好拓展您的業務了嗎？
            </h4>
            <form @submit.prevent="onSubmit">
                <!-- 餐廳名稱 -->
                <!-- required  放在v-model後-->
                <div class="mb-3 position-relative">
                    <input type="text" class="form-control form-input" v-model="storeName" placeholder="餐廳或商店名稱 *" />
                </div>
                <!-- 負責人姓名 -->
                <!-- required  放在v-model後-->
                <div class="mb-3 position-relative">
                    <input type="text" class="form-control form-input" v-model="managerFullName"
                        placeholder="負責人姓名 *" />
                </div>
                <!-- 電子郵件 -->
                <!-- required  放在v-model後-->
                <div class="mb-3">
                    <input type="email" class="form-control form-input" v-model="email" placeholder="電子郵件 *" />
                    <div v-if="error" class="text-danger small mt-1">{{ error }}</div>
                </div>
                <!-- 設定密碼 -->
                <!-- required  放在v-model後-->
                <div class="mb-3 position-relative">
                    <input :type="showPwd1 ? 'text' : 'password'" class="form-control form-input" v-model="password"
                        placeholder="請設定密碼 *" />
                    <button type="button" class="eye-btn" @click="showPwd1 = !showPwd1" tabindex="-1">
                        <!-- 眼睛 icon 開關 -->
                        <svg v-if="!showPwd1" width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" stroke="#ffba20" stroke-width="2" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                        <svg v-else width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path
                                d="M17.94 17.94C16.14 19.25 14.12 20 12 20c-7 0-11-8-11-8a19.89 19.89 0 0 1 5.06-5.94M10.11 6.11A7.01 7.01 0 0 1 12 6c7 0 11 8 11 8a19.89 19.89 0 0 1-4.13 5.11M1 1l22 22"
                                stroke="#ffba20" stroke-width="2" stroke-linecap="round" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                    </button>
                </div>
                <!-- 再次確認密碼 -->
                <!-- required  放在v-model後-->
                <div class="mb-3 position-relative">
                    <input :type="showPwd2 ? 'text' : 'password'" class="form-control form-input"
                        v-model="passwordConfirm" placeholder="請再次確認密碼 *" />
                    <button type="button" class="eye-btn" @click="showPwd2 = !showPwd2" tabindex="-1">
                        <svg v-if="!showPwd2" width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" stroke="#ffba20" stroke-width="2" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                        <svg v-else width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path
                                d="M17.94 17.94C16.14 19.25 14.12 20 12 20c-7 0-11-8-11-8a19.89 19.89 0 0 1 5.06-5.94M10.11 6.11A7.01 7.01 0 0 1 12 6c7 0 11 8 11 8a19.89 19.89 0 0 1-4.13 5.11M1 1l22 22"
                                stroke="#ffba20" stroke-width="2" stroke-linecap="round" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                    </button>
                </div>
                <!-- 電話 -->
                <div class="mb-4 d-flex align-items-center">
                    <div class="flag-box d-flex align-items-center px-2 me-2">
                        <img src="https://flagcdn.com/h20/tw.png" alt="台灣" style="width:22px; height:16px" />
                        <span class="ms-1">+886</span>
                    </div>
                    <!-- required  放在pattern後-->
                    <input type="tel" class="form-control form-input" v-model="phone" pattern="[0-9]{9,10}"
                        placeholder="聯絡電話 *" style="flex:1" />
                </div>
                <button type="submit" class="btn btn-main w-100" style="font-size:20px;">
                    開始
                </button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const storeName = ref('')
const managerFullName = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const phone = ref('')
const error = ref('')
const showPwd1 = ref(false)
const showPwd2 = ref(false)
const router = useRouter()

async function onSubmit() {
    if (password.value !== passwordConfirm.value) return
    // 1. 檢查 email 是否已存在
    try {
        const res = await axios.post('/api/owner/check-email', { email: email.value })
        if (res.data.exists) {
            error.value = '此 Email 已註冊，請登入或用其他 Email'
            return
        }
    // 通過就送註冊
    const regRes = await axios.post('/api/owner/register', {
            email: email.value,
            password: password.value,
            name: managerFullName.value,
            phone: phone.value
        });
        // 註冊成功才跳下一步
        if (regRes.data.success) {
            // 這邊你可以記錄 ownerId、email 等資訊，傳遞到下一頁
            router.push({
                path: '/registerStoreInfo',
                query: {
                    ownerId: regRes.data.ownerId,   // 回傳 id 或 email 都可以
                    storeName: storeName.value,
                    phone: phone.value
                }
            });
        } else {
            error.value = regRes.data.message || '註冊失敗';
        }
    } catch (e) {
        error.value = '伺服器錯誤，請稍後再試';
    }
}

// 密碼一致性檢查
watch([password, passwordConfirm], () => {
    if (passwordConfirm.value && passwordConfirm.value !== password.value) {
        error.value = '兩次輸入的密碼不一致'
    } else {
        error.value = ''
    }
})


</script>

<style scoped>
.register-bg {
    min-height: 100vh;
    background: #fafafb;
}

.register-card {
    background: #fff;
    border-radius: 18px;
    box-shadow: 0 2px 18px 4px rgba(0, 0, 0, 0.10);
    max-width: 400px;
    width: 100%;
    margin: 32px auto;
}

.form-input {
    border-radius: 10px;
    border: 2px solid #ddd;
    font-size: 16px;
    padding: 13px 13px;
    margin-bottom: 0;
}

.flag-box {
    background: #f7f7f7;
    border-radius: 7px;
    border: 1px solid #eee;
    height: 46px;
}

.eye-btn {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    padding: 0;
    cursor: pointer;
}

.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    height: 48px;
    border-radius: 10px;
    border: none;
    letter-spacing: 2px;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
}

.btn-main:hover {
    filter: brightness(1.08);
    background: #ffba20;
}

.text-danger {
    color: #ffba20;
}

.small {
    font-size: 0.875rem;
}
</style>
