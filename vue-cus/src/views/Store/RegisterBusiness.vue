<template>
    <div class="register-bg d-flex align-items-center justify-content-center">
        <div class="register-card px-3 py-4">
            <h4 class="mb-4 fw-bold text-dark text-center" style="font-size:22px;">
                準備好拓展您的業務了嗎？
            </h4>
            <form @submit.prevent="onSubmit">
                <!-- 餐廳名稱 -->
                <div class="mb-3 position-relative">
                    <input type="text" class="form-control form-input" v-model="storeName" placeholder="餐廳或商店名稱 *" />
                </div>
                <!-- 負責人姓名 -->
                <div class="mb-3 position-relative">
                    <input type="text" class="form-control form-input" v-model="ownerFullName"
                        placeholder="負責人姓名 *" />
                </div>
                <!-- 電子郵件 -->
                <div class="mb-3">
                    <input type="email" class="form-control form-input" v-model="ownerEmail" placeholder="電子郵件 *" />
                </div>
                <!-- 設定密碼 -->
                <div class="mb-3 position-relative">
                    <input :type="showPwd1 ? 'text' : 'password'" class="form-control form-input" v-model="password"
                        placeholder="請設定密碼 *" required autocomplete="new-password" />
                    <button type="button" class="eye-btn" @click="showPwd1 = !showPwd1" tabindex="-1">
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
                <div class="mb-3 position-relative">
                    <input :type="showPwd2 ? 'text' : 'password'" class="form-control form-input"
                        v-model="passwordConfirm" placeholder="請再次確認密碼 *" required autocomplete="new-password" />
                    <button type="button" class="eye-btn" @click="showPwd2 = !showPwd2" tabindex="-1">
                        <svg v-if="!showPwd2" width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" stroke="#ffba20" stroke-width="2" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                        <svg v-else width="22" height="22" fill="none" viewBox="0 0 24 24">
                            <path
                                d="M17.94 17.94C16.14 19.25 14.12 20 12 20c-7 0-11-8-11-8a19.89 19.89 0 0 1-4.13 5.11M10.11 6.11A7.01 7.01 0 0 1 12 6c7 0 11 8 11 8a19.89 19.89 0 0 1-4.13 5.11M1 1l22 22"
                                stroke="#ffba20" stroke-width="2" stroke-linecap="round" />
                            <circle cx="12" cy="12" r="3" stroke="#ffba20" stroke-width="2" />
                        </svg>
                    </button>
                    <div v-if="passwordConfirm && !isPwdMatch" class="text-danger mb-2" style="font-size:15px;">
                        兩次輸入的密碼不一致，請再檢查！
                    </div>
                </div>
                <!-- 密碼強度條&說明 -->
                <div class="d-flex align-items-center" style="margin-bottom:6px;">
                    <span style="font-size:16px;color:#888;">密碼強度</span>
                    <span :style="{ color: pwdStrength.color, 'font-weight': 'bold', 'margin-left': '6px', 'font-size': '15px' }">
                        {{ pwdStrength.label }}
                    </span>
                </div>
                <div class="progress my-2" style="height: 6px; background: #eee;">
                    <div class="progress-bar" :style="{
                        width: `${pwdStrength.percent * 100}%`,
                        background: pwdStrength.color
                    }"></div>
                </div>
                <div class="text-start mb-2" style="font-size:15px;">
                    <span>密碼必須包含：</span>
                    <ul class="mb-1" style="list-style: none; padding-left: 0;">
                        <li class="require-item">
                            <span :class="{ 'check': hasLen }">
                                <svg v-if="hasLen" width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <path d="M6 12l4 4 8-8" stroke="#4caf50" stroke-width="2" stroke-linecap="round" />
                                </svg>
                                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <circle cx="9" cy="12" r="1.5" fill="#bbb" />
                                    <circle cx="15" cy="12" r="1.5" fill="#bbb" />
                                </svg>
                            </span>
                            至少需要8個字
                        </li>
                        <li class="require-item">
                            <span :class="{ 'check': hasLower }">
                                <svg v-if="hasLower" width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <path d="M6 12l4 4 8-8" stroke="#4caf50" stroke-width="2" stroke-linecap="round" />
                                </svg>
                                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <circle cx="9" cy="12" r="1.5" fill="#bbb" />
                                    <circle cx="15" cy="12" r="1.5" fill="#bbb" />
                                </svg>
                            </span>
                            至少需要一個小寫字母 (a-z)
                        </li>
                        <li class="require-item">
                            <span :class="{ 'check': hasNumber }">
                                <svg v-if="hasNumber" width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <path d="M6 12l4 4 8-8" stroke="#4caf50" stroke-width="2" stroke-linecap="round" />
                                </svg>
                                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <circle cx="9" cy="12" r="1.5" fill="#bbb" />
                                    <circle cx="15" cy="12" r="1.5" fill="#bbb" />
                                </svg>
                            </span>
                            至少需要一個數字 (0-9)
                        </li>
                    </ul>
                </div>
                <!-- 電話 -->
                <div class="mb-4 d-flex align-items-center">
                    <div class="flag-box d-flex align-items-center px-2 me-2">
                        <img src="https://flagcdn.com/h20/tw.png" alt="台灣" style="width:22px; height:16px" />
                        <span class="ms-1">+886</span>
                    </div>
                    <input type="tel" class="form-control form-input" v-model="phone" pattern="[0-9]{9,10}"
                        placeholder="聯絡電話 *" style="flex:1" />
                </div>
                <button type="submit" class="btn btn-main w-100" style="font-size:20px;"
                    :disabled="!canSubmit">
                    開始
                </button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plungins/axios.js'
import { useUserStore } from '@/stores/user.js'

const storeName = ref('')
const ownerFullName = ref('')
const ownerEmail = ref('')
const password = ref('')
const passwordConfirm = ref('')
const phone = ref('')
const error = ref('')
const showPwd1 = ref(false)
const showPwd2 = ref(false)
const router = useRouter()
const userStore = useUserStore()

// 密碼規則驗證
const hasLen = computed(() => password.value.length >= 8)
const hasLower = computed(() => /[a-z]/.test(password.value))
const hasNumber = computed(() => /\d/.test(password.value))
const isPwdMatch = computed(() => password.value === passwordConfirm.value)
const pwdStrength = computed(() => {
    if (hasLen.value && hasLower.value && hasNumber.value)
        return { label: '高', color: '#fbc02d', percent: 1 }
    if ((hasLen.value && hasLower.value) || (hasLen.value && hasNumber.value) || (hasLower.value && hasNumber.value))
        return { label: '中等', color: '#ffb300', percent: 0.7 }
    if (hasLen.value || hasLower.value || hasNumber.value)
        return { label: '弱', color: '#e57373', percent: 0.4 }
    return { label: '', color: '#eee', percent: 0 }
})

// 密碼一致性檢查
const passwordError = computed(() =>
    passwordConfirm.value && !isPwdMatch.value ? '兩次輸入的密碼不一致，請再檢查！' : ''
)

const canSubmit = computed(() =>
    storeName.value &&
    ownerFullName.value &&
    ownerEmail.value &&
    phone.value &&
    hasLen.value &&
    hasLower.value &&
    hasNumber.value &&
    isPwdMatch.value &&
    !error.value
)


async function onSubmit() {
    if (!canSubmit.value) return
    try {
        const res = await axios.post('/api/owner/check-email', { email: ownerEmail.value })
        if (res.data.exists) {
            error.value = '此 Email 已註冊，請登入或用其他 Email'
            return
        }

        userStore.setStoreName(storeName.value)
        userStore.setOwnerFullName(ownerFullName.value)


        const regRes = await axios.post('/api/owner/register', {
            email: ownerEmail.value,
            password: password.value,
            name: ownerFullName.value,
            phone: phone.value
        })
        if (regRes.data.success) {
            const ownerId = regRes.data.ownerId + ''
            // localStorage.setItem('registerOwnerId', ownerId)
            // localStorage.setItem('registerStoreName', storeName.value)
            // localStorage.setItem('registerPhone', phone.value)
            router.push({
                path: '/registerStoreInfo',
                query: {
                    ownerId: ownerId,
                    storeName: storeName.value,
                    phone: phone.value
                }
            })
        } else {
            error.value = regRes.data.message || '註冊失敗'
        }
    } catch (e) {
        error.value = '伺服器錯誤，請稍後再試'
    }
}
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
