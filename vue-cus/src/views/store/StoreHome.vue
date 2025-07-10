<template>
    <div class="container text-center mt-5">
        <h2 class="mb-4">歡迎光臨！</h2>
        <!-- 已登入：顯示業者下拉 -->
        <div v-if="storeFullName" class="d-grid gap-3 col-6 mx-auto position-relative">
            <div class="dropdown-wrapper">
                <button class="dropdown-btn" :class="{ open: showMenu }" @click="toggleMenu">
                    <i class="bi bi-person-circle me-1"></i>{{ storeFullName }}
                    <i class="bi ms-1" :class="showMenu ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                </button>
                <ul v-if="showMenu" class="custom-dropdown text-start">
                    <li @click="goEditStoreUser">
                        <i class="bi bi-person me-2"></i>餐廳業者資料
                    </li>
                    <li @click="goEditStore">
                        <i class="bi bi-shop me-2"></i>餐廳資料
                    </li>
                    <li @click="logout">
                        <i class="bi bi-box-arrow-right me-2"></i>登出
                    </li>
                </ul>
            </div>
        </div>

        <!-- 未登入自動顯示：業者登入/註冊彈窗 -->
        <StoreLoginModal
            v-if="!storeFullName && showLoginModal"
            :show="showLoginModal"
            @close="showLoginModal = false"
            @login="onLogin"
            @register="onRegister"
        />

        <!-- 登入 Email Modal -->
        <LoginEmailModal
            :show="step === 'loginEmail'"
            :error-msg="loginEmailError"
            @close="step = ''"
            @submit="handleLoginEmail"
        />
        <!-- 登入密碼 Modal -->
        <LoginPasswordModal
            :show="step === 'loginPassword'"
            :email="storeEmail"
            @close="step = ''"
            @forgot="step = 'forgotPassword'"
            @back="step = 'loginEmail'"
            @login="handlePasswordLogin"
        />
        <!-- 忘記密碼 Modal -->
        <ForgotPasswordModal
            :show="step === 'forgotPassword'"
            :email="storeEmail"
            @close="step = ''"
            @back="step = 'loginPassword'"
            @submit="handleForgotSubmit"
        />
        <!-- 重設密碼 Dialog -->
        <ResetPasswordDialog
            v-if="showReset"
            @close="showReset = false"
            @submit="onResetPassword"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import StoreLoginModal from '@/components/Store/StoreLoginModal.vue'
import LoginEmailModal from '@/views/store/LoginEmailModal.vue'
import LoginPasswordModal from '@/views/store/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/store/ForgotPasswordModal.vue'
import ResetPasswordDialog from '@/views/store/ResetPasswordDialog.vue'

const router = useRouter()
const step = ref('')
const storeEmail = ref('')
const storeFullName = ref('')
const storePhone = ref('')
const showMenu = ref(false)
const showReset = ref(false)
const loginEmailError = ref('')
const showLoginModal = ref(true) // 一進頁面就彈出

onMounted(() => {
    storeFullName.value = localStorage.getItem('storeFullName') || ''
    storeEmail.value = localStorage.getItem('storeEmail') || ''
    storePhone.value = localStorage.getItem('storePhone') || ''
    if (storeFullName.value) showLoginModal.value = false
})

// 點選「登入」彈窗
function onLogin() {
    step.value = 'loginEmail'
    showLoginModal.value = false
}
// 點選「註冊」彈窗
function onRegister() {
    router.push('/registerBusiness')
    showLoginModal.value = false
}

// 業者下拉選單
function toggleMenu() { showMenu.value = !showMenu.value }
function goEditStoreUser() { router.push('/editStoreUser'); showMenu.value = false }
function goEditStore() { router.push('/editStore'); showMenu.value = false }
function logout() {
    localStorage.removeItem('storeFullName')
    localStorage.removeItem('storeName')
    localStorage.removeItem('storeEmail')
    localStorage.removeItem('storePhone')
    storeFullName.value = ''
    showMenu.value = false
    showLoginModal.value = true // 登出後馬上彈出
}

// 登入 email 驗證
async function handleLoginEmail(email) {
    storeEmail.value = email
    loginEmailError.value = ''
    try {
        const res = await axios.post('/api/owner/check-email', { email })
        if (res.data.exists) {
            step.value = 'loginPassword'
        } else {
            loginEmailError.value = '查無此帳號，請確認Email或註冊新帳號'
        }
    } catch (e) {
        loginEmailError.value = '伺服器錯誤，請稍後再試'
    }
}

// 登入密碼
async function handlePasswordLogin({ email, password }) {
    try {
        const res = await axios.post('/api/owner/login', { email, password })
        if (res.data.success) {
            localStorage.setItem('storeFullName', res.data.name)
            localStorage.setItem('storeEmail', res.data.email)
            localStorage.setItem('storePhone', res.data.phone)
            storeFullName.value = res.data.name
            storeEmail.value = res.data.email
            storePhone.value = res.data.phone
            step.value = ''
            router.push('/store') // 回到自己這頁
        } else {
            alert(res.data.message || '帳號或密碼錯誤')
        }
    } catch (e) {
        alert('伺服器錯誤，請稍後再試')
    }
}

// 忘記密碼
function handleForgotSubmit(email) {
    alert('已寄送重設密碼信到 ' + email)
    step.value = ''
    router.push({
        path: '/resetPasswordEmail',
        query: (email)
    })
}
function onResetPassword(newPwd) {
    showReset.value = false
    router.replace({ path: '/store' })
    storeFullName.value = localStorage.getItem('storeFullName') || ''
}
</script>

<style scoped>
/* 下拉 wrapper */
.dropdown-wrapper {
    display: inline-block;
    position: relative;
}
.dropdown-btn {
    background: transparent;
    border: none;
    font-size: 1rem;
    line-height: 1.5;
    cursor: pointer;
    padding: 0.4rem 0.8rem;
}
.dropdown-btn.open {
    background: #fff;
    border-radius: 0.5rem 0.5rem 0 0;
}
.custom-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    left: auto;
    width: auto;
    margin-top: -1px;
    background: #fff;
    border-radius: 0 0 0.5rem 0.5rem;
    list-style: none;
    padding: 0.4rem 0;
    z-index: 1000;
    font-size: large;
}
.custom-dropdown li {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding: 0.4rem 1rem;
    cursor: pointer;
    font-size: 0.95rem;
}
.custom-dropdown li .bi {
    margin-left: 0.5rem;
    flex-shrink: 0;
}
.custom-dropdown li:hover {
    background: #f8f9fa;
}
</style>