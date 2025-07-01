<template>
    <div class="container text-center mt-5">
            <h2 class="mb-4">歡迎光臨！</h2>
            <div class="d-grid gap-3 col-6 mx-auto position-relative">
                <!-- 沒有 firstName:顯示註冊二選一按鈕 -->
                <button
                    v-if="!userFirstName"
                    class="btn btn-primary btn-lg"
                    @click="showChoice = true"
                    >
                    註冊
                    </button>
            
                    <!-- 有 firstName:顯示名字 -->
                    <div v-else class="dropdown-wrapper">
                        <button
                            class="dropdown-btn"
                            :class="{ open: showMenu }"
                            @click="toggleMenu">
                            <i class="bi bi-person-circle me-1"></i>{{ userFirstName }}
                            <i class="bi ms-1"
                            :class="showMenu ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                        </button>
                        
                        <!-- 自訂下拉選單 -->
                        <ul v-if="showMenu" class="custom-dropdown text-start">
                            <li @click="goProfile">
                                <i class="bi bi-person me-2"></i>個人檔案
                            </li>
                            <li @click="logout">
                                <i class="bi bi-box-arrow-right me-2"></i>登出
                            </li>
                        </ul>
            </div>
    </div>
            
    <!-- 登入 Email Modal -->
    <LoginEmailModal
        :show="step === 'loginEmail'"
        @back="step = ''"
        @close="step = ''"
        @submit="handleLoginEmail"
    />
    <!-- 登入密碼 Modal -->
    <LoginPasswordModal
        :show="step === 'loginPassword'"
        :email="userEmail"
        @close="step = ''"
        @forgot="step = 'forgotPassword'"
        @back="step = 'loginEmail'"
        @login="handlePasswordLogin"
    />
    <!-- 忘記密碼 Modal -->
    <ForgotPasswordModal
        :show="step === 'forgotPassword'"
        :email="userEmail"
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

    <!-- --- 新樣式：歡迎二選一 Modal --- -->
    <div v-if="showChoice" class="choice-bg">
        <div class="choice-modal">
            <button class="choice-close" @click="showChoice = false">
                <i class="bi bi-x-lg"></i>
            </button>
            <h3 class="mb-1">歡迎！</h3>
            <p class="text-secondary mb-4">註冊或登入</p>
            <button class="btn btn-pink w-100 mb-2" @click="goLoginEmail">
                登入
            </button>
            <button class="btn btn-outline-dark w-100" @click="goRegisterBusiness">
                註冊
            </button>
        </div>
        </div>
    </div>
</template>
            
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
            
import LoginEmailModal from '@/views/LoginEmailModal.vue'
import LoginPasswordModal from '@/views/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/ForgotPasswordModal.vue'
import ResetPasswordDialog from '@/views/ResetPasswordDialog.vue'
            
const router = useRouter()
const step = ref('')
const userEmail = ref('')
const userFirstName = ref('')
const showMenu = ref(false)
const showReset = ref(false)
const showChoice = ref(false)
            
onMounted(() => {
    userFirstName.value = localStorage.getItem('userFirstName') || ''
})
            
function toggleMenu() {
    showMenu.value = !showMenu.value
}
            
function goProfile() {
    router.push('/profile')
    showMenu.value = false
}
            
function logout() {
    localStorage.removeItem('userFirstName')
    userFirstName.value = ''
    showMenu.value = false
}
            
// 二選一：跳到商家註冊
function goRegisterBusiness() {
    showChoice.value = false
    router.push('/registerBusiness')
}
            
// 二選一：開啟登入Email Modal
function goLoginEmail() {
    showChoice.value = false
    step.value = 'loginEmail'
}
            
// 登入 email
function handleLoginEmail(email) {
    userEmail.value = email
    step.value = 'loginPassword'
}
            
// 登入密碼
function handlePasswordLogin({ email, password }) {
    // 假設這裡呼叫 API 成功，並取回 firstName
    alert(`登入成功：${email} / ${password}`)
    const apiFirstName = '王小明'
    localStorage.setItem('userFirstName', apiFirstName)
    userFirstName.value = apiFirstName
    step.value = ''
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
    // 假設 API call 完成後
    router.replace({ path: '/', })
    // 再重新讀一次名字（通常你會從後端拿回名字再存）
    userFirstName.value = localStorage.getItem('userFirstName') || ''
}
</script>
            
<style scoped>
/* 下拉 wrapper 需要 position: relative 才能讓 menu 絕對定位 */
.dropdown-wrapper {
    display: inline-block;
    position: relative;
}
/* button 的初始樣式:透明+無邊框+padding */
.dropdown-btn {
    background: transparent;
    border: none;
    font-size: 1rem;
    line-height: 1.5;
    cursor: pointer;
    padding: 0.4rem 0.8rem;
}
/* open狀態:白底+上圓角+陰影 */
.dropdown-btn.open {
    background: #fff;
    border-radius: 0.5rem 0.5rem 0 0;
    /* box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); */
}
/* 選單本體 */
.custom-dropdown {
    position: absolute;
    top: 100%;      /* 緊貼在 button 下方 */
    right: 0;       /* ← 改成靠右對齊 */
    left: auto;     /* ← 關閉左側對齊 */
    width: auto;
    margin-top: -1px; /* 貼齊圓角 */
    background: #fff;
    border-radius: 0 0 0.5rem 0.5rem;
    /* box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); */
    list-style: none;
    padding: 0.4rem 0;
    z-index: 1000;
    font-size: large;
}
/* 選單項目 */
.custom-dropdown li {
    display: flex;
    justify-content: flex-start; /* 文字靠左 */
    align-items: center;
    padding: 0.4rem 1rem;
    cursor: pointer;
    font-size: 0.95rem;
}
/* 小 icon margin */
.custom-dropdown li .bi {
    margin-left: 0.5rem;
    flex-shrink: 0;
}

.custom-dropdown li:hover {
    background: #f8f9fa;
}

/* 背景遮罩 */
.choice-bg {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1050;
}
/* 彈窗本體 */
.choice-modal {
    position: relative;
    width: 320px;
    background: #fff;
    border-radius: 12px;
    padding: 1.5rem;
    text-align: center;
    box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}
/* 關閉鈕 */
.choice-close {
    position: absolute;
    top: 0.75rem;
    right: 0.75rem;
    background: none;
    border: none;
    font-size: 1.2rem;
    color: #666;
    cursor: pointer;
}
/* 粉紅登入按鈕 */
.btn-pink {
    background: #ffc94d;
    color: #fff;
    border: none;
    font-weight: bold;
}
.btn-pink:hover {
    background: #f1cd78;
}
/* 註冊按鈕 */
.btn-outline-dark {
    color: #333;
    border-color: #333;
}
.btn-outline-dark:hover {
    background: #f0f0f0;
}
</style>
            