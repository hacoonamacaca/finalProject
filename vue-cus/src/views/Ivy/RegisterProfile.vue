<template>
    <div class="profile-bg">
        <div class="profile-card">
            <img src="https://cdn-icons-png.flaticon.com/512/2815/2815428.png" alt="icon" class="profile-img mb-2" />
            <h2 class="mb-1 fw-bold">讓我們開始吧！</h2>
            <div class="mb-3 text-secondary" style="font-size:15px;">
                首先，用 <span class="text-dark fw-bold">{{ email }}</span> 建立你的帳號。
            </div>
            <form @submit.prevent="onSubmit">
                <div class="row gx-2 mb-3">
                    <div class="col">
                        <input type="text" v-model="fullName" class="form-control custom-input" placeholder="姓名"
                            required />
                    </div>
                </div>
                <div class="mb-3 position-relative">
                    <input :type="showPassword ? 'text' : 'password'" v-model="password"
                        class="form-control custom-input" placeholder="密碼" required autocomplete="new-password" />
                    <button type="button" class="eye-btn" @click="togglePassword" tabindex="-1">
                        <svg v-if="!showPassword" width="22" height="22" fill="none" viewBox="0 0 24 24">
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

                <!-- 密碼強度條 & 說明 -->
                <div class="d-flex align-items-center mb-1">
                    <span style="font-size:16px;color:#888;">密碼強度</span>
                    <span :style="{
                        color: strength.color,
                        fontWeight: 'bold',
                        marginLeft: '6px',
                        fontSize: '15px'
                    }">
                        {{ strength.label }}
                    </span>
                </div>
                <div class="progress my-2" style="height: 6px; background: #eee;">
                    <div class="progress-bar" :style="{
                        width: `${strength.percent * 100}%`,
                        background: strength.color
                    }"></div>
                </div>
                <div class="text-start mb-2" style="font-size:15px;">
                    <span>密碼必須包含：</span>
                    <ul class="mb-1" style="list-style:none; padding-left:0;">
                        <li class="require-item">
                            <span :class="{ check: hasLen }">
                                <svg v-if="hasLen" width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <path d="M6 12l4 4 8-8" stroke="#4caf50" stroke-width="2" stroke-linecap="round" />
                                </svg>
                                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
                                    <circle cx="9" cy="12" r="1.5" fill="#bbb" />
                                    <circle cx="15" cy="12" r="1.5" fill="#bbb" />
                                </svg>
                            </span>
                            至少需要 8 個字
                        </li>
                        <li class="require-item">
                            <span :class="{ check: hasLower }">
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
                            <span :class="{ check: hasNumber }">
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

                <button type="submit" class="btn btn-main w-100" :disabled="!canSubmit">
                    建立帳戶
                </button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 從 query 拿到 email
const email = ref(route.query.email || localStorage.getItem('userEmail') || '')

// 表單欄位
const fullName = ref('')
const password = ref('')
const showPassword = ref(false)

// 密碼規則檢查
const hasLen = computed(() => password.value.length >= 8)
const hasLower = computed(() => /[a-z]/.test(password.value))
const hasNumber = computed(() => /\d/.test(password.value))

// 密碼強度
const strength = computed(() => {
    const passed = [hasLen.value, hasLower.value, hasNumber.value].filter(v => v).length
    if (passed === 3) return { label: '高', color: '#fbc02d', percent: 1 }
    if (passed === 2) return { label: '中等', color: '#ffb300', percent: 0.7 }
    if (passed === 1) return { label: '弱', color: '#e57373', percent: 0.4 }
    return { label: '', color: '#eee', percent: 0 }
})

// 送出按鈕啟用條件
const canSubmit = computed(() =>
    fullName.value.trim() !== '' && hasLen.value && hasLower.value && hasNumber.value
)

function togglePassword() {
    showPassword.value = !showPassword.value
}

function onSubmit() {
    localStorage.setItem('userFullName', fullName.value.trim())
    localStorage.setItem('userEmail', email.value)
    router.push('/search')
}
</script>

<style scoped>
.profile-bg {
    min-height: 80vh;
    background: #fafafb;
    display: flex;
    justify-content: center;
    align-items: center;
}

.profile-card {
    background: #fff;
    border-radius: 18px;
    box-shadow: 0 2px 18px 4px rgba(0, 0, 0, 0.10);
    padding: 38px 32px 28px 32px;
    max-width: 430px;
    width: 100%;
    margin: 32px auto;
    text-align: center;
    position: relative;
}

.profile-img {
    width: 50px;
    height: 50px;
    margin-bottom: 6px;
}

.custom-input {
    border-radius: 10px;
    border: 2px solid #bbb;
    font-size: 17px;
    padding: 12px 13px;
    margin-bottom: 0;
}

.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    height: 48px;
    border-radius: 12px;
    border: none;
    letter-spacing: 2px;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
}

.btn-main:disabled {
    background: #d5d5d5;
    color: #fff;
}

.btn-main:hover:enabled {
    filter: brightness(1.08);
    background: #f1cd78;
}

.eye-btn {
    position: absolute;
    top: 12px;
    right: 16px;
    background: transparent;
    border: none;
    padding: 0;
    cursor: pointer;
    z-index: 10;
    outline: none;
    display: flex;
    align-items: center;
}

.require-item {
    display: flex;
    align-items: center;
    gap: 6px;
    margin: 2px 0 2px 0;
    color: #333;
    font-size: 16px;
}

.check {
    margin-right: 2px;
}

.progress {
    background: #eee;
}

.progress-bar {
    transition: width 0.25s;
    height: 100%;
    border-radius: 3px;
}
</style>