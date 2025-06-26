<template>
    <div class="container text-center mt-5">
        <h2 class="mb-4">歡迎光臨！</h2>
        <div class="d-grid gap-3 col-6 mx-auto">
            <button class="btn btn-primary btn-lg" @click="handleRegister">註冊</button>
            <button class="btn btn-outline-primary btn-lg" @click="step = 'loginEmail'">登入</button>
        </div>
        <!-- 登入 Email Modal -->
        <LoginEmailModal 
            :show="step === 'loginEmail'" 
            @close="step = ''" 
            @submit="handleLoginEmail"
            @register="handleRegister"
        />
        <!-- 登入密碼 Modal -->
        <LoginPasswordModal 
            :show="step === 'loginPassword'" 
            :email="userEmail" 
            @close="step = ''"
            @forgot="handleForgot" 
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
        <ResetPasswordDialog
            v-if="showReset"
            @close="showReset = false"
            @submit="onResetPassword"
        />
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LoginEmailModal from '@/views/LoginEmailModal.vue'
import LoginPasswordModal from '@/views/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/ForgotPasswordModal.vue'
import ResetPasswordDialog from '@/views/ResetPasswordDialog.vue'

const step = ref('')
const userEmail = ref('')
// const showReset = ref(false)
const router = useRouter()

function handleRegister() {
    router.push('/registerBusiness')
}

function handleLoginEmail(email) {
    userEmail.value = email
    const exists = true
    if (exists) {
        step.value = 'loginPassword'
    } else {
        alert('此 email 尚未註冊，請先註冊')
    }
}

function handlePasswordLogin({ email, password }) {
    alert(`登入成功：${email} / ${password}`)
    step.value = ''
}

function handleForgot() {
    step.value = 'forgotPassword'
}

function handleForgotSubmit(email) {
    // alert('已寄送重設密碼信到 ' + email)
    // step.value = ''
    // showReset.value = true         // 這裡彈出重設密碼 Dialog
    alert('已寄送重設密碼信到 ' + email)
    step.value = ''
    router.push({
        path: '/resetPasswordEmail',
        query:(email)
    })
}

function onResetPassword(newPwd) {
    // 1. 呼叫 API 更新密碼
    //    axios.post('/api/reset-password', { email: userEmail, password: newPwd })

    // 2. 關閉 dialog
    showReset.value = false

    // 3. 跳到登入頁，並且把 email、newPwd 放到 query（或存到 store，讓登入元件自動帶入）
    router.replace({
        path: '/login',
        query: {
        email: route.query.email,  // 你的 userEmail
        password: newPwd
        }
    })
}
</script>
