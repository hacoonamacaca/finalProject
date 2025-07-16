<template>
    <div class="user-dropdown-container">
        <a href="#" class="user-link" @click.prevent="toggleDropdown">
            {{ isLoggedIn ? (userFullName || '目前使用者*') : '請選擇登入身分' }}
        </a>
        <div class="dropdown-menu" v-if="showDropdown">
            <ul>
                <li @click="step = 'register'">會員*</li>
                <li @click="toStore">餐廳方*</li>
                <li @click="navigateTo('admin')">管理者*</li>
                <li @click="navigateTo('Search')">Search*</li>
                <li @click="logout">登出</li>
            </ul>
        </div>
    </div>
    <!-- modal群組不變 -->
    <RegisterModal
        :show="step === 'register'"
        @close="step = ''"
        @register="step = 'email'"
        @login="step = 'loginEmail'"
        @google-login="onGoogleLogin"
    />
    <RegisterEmailModal
        :show="step === 'email'"
        @close="step = ''"
        @back="step = 'register'"
        @submit="handleRegisterEmail"
    />
    <VerifyEmailModal
        :show="step === 'verifyEmail'"
        :email="userEmail"
        @close="step = ''"
        @back="step = 'email'"
        @send="handleSendVerification"
    />
    <LoginEmailModal
        :show="step === 'loginEmail'"
        @submit="handleLoginEmail"
        @close="step = ''"
        @back="step = 'register'"
    />
    <LoginPasswordModal
        :show="step === 'loginPassword'"
        :email="userEmail"
        @login="handlePasswordLogin"
        @close="step = ''"
        @back="step = 'loginEmail'"
        @forgot="step = 'forgotPassword'"
    />
    <ForgotPasswordModal
        :show="step === 'forgotPassword'"
        :email="userEmail"
        @close="step = ''"
        @back="step = 'loginPassword'"
        @submit="handleForgotSubmit"
    />
    <ForgotPasswordSentModal
        :show="step === 'forgotSent'"
        @close="step = ''"
        @back="step = 'loginPassword'"
        @backToLogin="step = 'loginEmail'"
    />
    <ResetPasswordSentModal
        :show="step === 'resetPasswordSent'"
        @close="step = ''"
        @back="step = 'loginPassword'"
    />
    <ResetPasswordDialog
        v-if="showReset"
        :email="route.query.email"
        :token="route.query.token"
        @close="showReset = false"
        @submit="onResetPassword"
    />
    <ResetSuccessModal 
        v-if="showSuccess"
        @close="closeSuccess"
    />

</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user.js'

import RegisterModal from '@/components/Ivy/RegisterModal.vue'
import LoginEmailModal from '@/views/Ivy/LoginEmailModal.vue'
import RegisterEmailModal from '@/views/Ivy/RegisterEmailModal.vue'
import VerifyEmailModal from '@/views/Ivy/VerifyEmailModal.vue'
import LoginPasswordModal from '@/views/Ivy/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/Ivy/ForgotPasswordModal.vue'
import ForgotPasswordSentModal from '@/views/Ivy/ForgotPasswordSentModal.vue'
import ResetPasswordSentModal from '@/views/Ivy/ResetPasswordSentModal.vue'
import ResetPasswordDialog from '@/views/Ivy/ResetPasswordDialog.vue'
import ResetSuccessModal from '@/components/Ivy/ResetSuccessModal.vue'

const step = ref('')
const showReset = ref(false)
const showSuccess = ref(false)
const showDropdown = ref(false)
const router = useRouter()
const route = useRoute()
const userStore = useUserStore() // Pinia!

// 取得狀態
const userFullName = computed(() => userStore.fullName)
const userEmail = computed(() => userStore.email)
const userPhone = computed(() => userStore.phone)
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 彈窗流程
function handlePasswordLogin({ userFullName, userEmail, userPhone }) {
    userStore.setLogin({ fullName: userFullName, email: userEmail, phone: userPhone })
    step.value = ''
    router.push('/search')
}
function handleRegisterEmail(email) {
    userStore.setEmail(email)
    step.value = 'verifyEmail'
}
function handleLoginEmail(email) {
    userStore.setEmail(email)
    step.value = 'loginPassword'
}
function handleSendVerification(email) {
    alert(`已寄出驗證信到 ${email}`)
    step.value = ''
    router.push({ path: '/register-profile', query: { email } })
}
function handleForgotSubmit(email) {
    step.value = ''
    router.push('/') // 這裡**不要帶 query**
}
function logout() {
    userStore.logout()
    showDropdown.value = false
}
function toStore() { router.push('/store') }
function navigateTo(path) {
    router.push(`/${path}`)
    showDropdown.value = false
}
function toggleDropdown() {
    showDropdown.value = !showDropdown.value
}
function onGoogleLogin() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}
function onResetPassword() {
    showReset.value = false
    showSuccess.value = true // 顯示「密碼已更新」訊息
    // 清掉 query string，避免重複再次觸發
    router.replace({ path: '/', query: {} })
    userStore.syncFromStorage()
}
function closeSuccess() {
    showSuccess.value = false
    step.value = 'loginEmail' // 或你要打開登入頁
}
// 點擊外部關閉下拉選單
function handleClickOutside(event) {
    if (!event.target.closest('.user-dropdown-container')) {
        showDropdown.value = false
    }
}

onMounted(() => {
    // 1. 點擊外部關閉下拉選單
    document.addEventListener('click', handleClickOutside)

    // 2. 偵測 query string 觸發重設密碼 dialog
    if (route.query.reset === '1' && route.query.email && route.query.token) {
        showReset.value = true
        userStore.setEmail(route.query.email)
        // 可選：清除 query 以免重複
        // router.replace({ path: '/', query: {} })
    }
})
onUnmounted(() => { document.removeEventListener('click', handleClickOutside) })
</script>

<style scoped>
/*--------- Ivy -------------*/
.user-dropdown {
    cursor: pointer;
    user-select: none;
    position: relative;
    user-select: none;
}

.custom-dropdown {
    display: block;
    position: absolute;
    top: 22%;
    left: 50%;
    transform: translateX(-50%);
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 4px 0;
    min-width: 160px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    z-index: 1000;
}

.custom-dropdown li {
    list-style: none;
    padding: 8px 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    font-size: 0.95rem;
    color: #333;
}

.custom-dropdown li:hover {
    background: #f5f5f5;
}

.custom-dropdown li i {
    font-size: 1.1rem;
    color: #666;
}

/*--------------------------*/

.user-dropdown-container {
    position: relative;
    display: inline-block;
}

.user-link {
    color: white;
    text-decoration: none;
    font-size: 16px;
    padding: 8px 12px;
    cursor: pointer;
}

.user-link:hover {
    background-color: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
}

.dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    /* 確保 z-index 足夠高 */
    min-width: 160px;
    margin-top: 5px;
    display: block;
    /* 添加此行，確保顯示 */
}

.dropdown-menu ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.dropdown-menu li {
    padding: 12px 16px;
    font-size: 14px;
    color: #333;
    cursor: pointer;
    transition: background-color 0.2s;
}

.dropdown-menu li:hover {
    background-color: #f5f5f5;
}

.dropdown-menu li:last-child {
    border-top: 1px solid #ddd;
    color: #ffba20;
}
</style>
