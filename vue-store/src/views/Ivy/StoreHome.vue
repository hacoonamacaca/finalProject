<template>
    <div class="container text-center mt-5">
        <h2 class="mb-4">歡迎光臨！</h2>

        <!-- 只要這裡就好 -->
        <StoreLoginModal
            v-if="showLoginModal"
            :show="showLoginModal"
            @close="closeLoginModal"
            @login="onLogin"
            @register="onRegister"
        />

        <LoginEmailModal
            :show="step === 'loginEmail'"
            :error-msg="loginEmailError"
            @close="step = ''"
            @back="goBackToLoginModal"
            @submit="handleLoginEmail"
        />
        <LoginPasswordModal
            :show="step === 'loginPassword'"
            :email="storeEmail"
            :error-msg="passwordErrorMsg"
            :loading="isLoading"
            @close="step = ''"
            @forgot="step = 'forgotPassword'"
            @back="step = 'loginEmail'"
            @login="handlePasswordLogin"
        />
        <ForgotPasswordModal
            :show="step === 'forgotPassword'"
            :email="storeEmail"
            @close="step = ''"
            @back="step = 'loginPassword'"
            @submit="handleForgotSubmit"
        />
        <ResetPasswordDialog
            v-if="showReset"
            @close="showReset = false"
            @submit="onResetPassword"
        />
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '@/plungins/axios.js'
import { useOwnerStore } from '@/stores/owner.js'

import StoreLoginModal from '@/components/Ivy/StoreLoginModal.vue'
import LoginEmailModal from '@/views/Ivy/LoginEmailModal.vue'
import LoginPasswordModal from '@/views/Ivy/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/Ivy/ForgotPasswordModal.vue'
import ResetPasswordDialog from '@/views/Ivy/ResetPasswordDialog.vue'

const router = useRouter()
const route = useRoute()
const step = ref('')
const storeEmail = ref('')
const storePhone = ref('')
const showReset = ref(false)
const loginEmailError = ref('')
const showLoginModal = ref(false)
const isLoading = ref(false)
const passwordErrorMsg = ref('')

// Pinia狀態
const userStore = useOwnerStore()
const storeFullName = computed(() => userStore.ownerFullName)

// 進頁面就依據 query 狀態顯示登入彈窗
onMounted(() => {
    userStore.syncFromStorage()
    storeEmail.value = localStorage.getItem('storeEmail')
    storePhone.value = localStorage.getItem('storePhone') || ''
    if (route.query.login === '1') {
        showLoginModal.value = true
    }
})

// 監聽網址 query 變化決定彈窗狀態
watch(() => route.query.login, (val) => {
    showLoginModal.value = val === '1'
})

// 點選「登入」彈窗，進入 email 步驟
function onLogin() {
    step.value = 'loginEmail'
    closeLoginModal()
}

// 點選「註冊」彈窗
function onRegister() {
    closeLoginModal()
    router.push('/registerBusiness')
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
    isLoading.value = true
    passwordErrorMsg.value = ''
    try {
        const res = await axios.post('/api/owner/login', { email, password })
        if (res.data.success) {
            userStore.setOwnerId(res.data.ownerId)
            userStore.setOwnerFullName(res.data.name)
            userStore.setOwnerEmail(res.data.email)
            storePhone.value = res.data.phone
            localStorage.setItem('storePhone', res.data.phone)
            step.value = ''
            router.push('/store/menu')
        } else {
            passwordErrorMsg.value = res.data.message || '帳號或密碼錯誤'
        }
    } catch (e) {
        passwordErrorMsg.value = '伺服器錯誤，請稍後再試'
    } finally {
        isLoading.value = false
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
    userStore.syncFromStorage()
}

function goBackToLoginModal() {
    step.value = ''
    // 重新顯示登入視窗
    openLoginModal()
}

function openLoginModal() {
    router.replace({ path: '/store', query: { login: '1' } })
}
function closeLoginModal() {
    showLoginModal.value = false
    router.replace({ path: '/store', query: {} })
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