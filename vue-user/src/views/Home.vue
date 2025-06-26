    <template>
        <div class="container text-center mt-5">
        <h2 class="mb-4">歡迎光臨！</h2>
        <div class="d-grid gap-3 col-6 mx-auto">
            <button class="btn btn-primary btn-lg" @click="step = 'register'">註冊</button>
            <button class="btn btn-outline-primary btn-lg" @click="step = 'loginEmail'">登入</button>
        </div>
    
        <!-- 註冊/登入第一步 -->
        <RegisterModal
            :show="step === 'register'"
            @close="step = ''"
            @register="step = 'email'"
            @login="step = 'loginEmail'"
        />
        <!-- Email 驗證步驟 -->
        <RegisterEmailModal
            :show="step === 'email'"
            @close="step = ''"
            @back="step = 'register'"
            @submit="handleRegisterEmail"
        />
        <!-- 登入 Email Modal -->
        <LoginEmailModal
            :show="step === 'loginEmail'"
            @close="step = ''"
            @submit="handleLoginEmail"
            @register="step = 'register'"
            />
        <!-- 驗證信 modal -->
        <VerifyEmailModal
            :show="step === 'verifyEmail'"
            :email="userEmail"
            @close="step = ''"
            @back="step = 'email'"
            @send="handleSendVerification"
        />
        <!-- 登入密碼 -->
        <LoginPasswordModal
            :show="step === 'loginPassword'"
            :email="userEmail"
            @close="step = ''"
            @forgot="step = 'forgotPassword'"
            @back="step = 'loginEmail'"
            @login="handlePasswordLogin"
        />
        <!-- 忘記密碼 -->
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
            @back="step = 'loginPassword'" /> <!-- or step = 'loginEmail' 依你的流程-->
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
    import RegisterModal from '@/components/RegisterModal.vue'
    import LoginEmailModal from '@/views/LoginEmailModal.vue'
    import RegisterEmailModal from '@/views/RegisterEmailModal.vue'
    import VerifyEmailModal from '@/views/VerifyEmailModal.vue'
    import LoginPasswordModal from '@/views/LoginPasswordModal.vue'
    import ForgotPasswordModal from '@/views/ForgotPasswordModal.vue'
    import ForgotPasswordSentModal from '@/views/ForgotPasswordSentModal.vue'
    import ResetPasswordSentModal from '@/views/ResetPasswordSentModal.vue'
    import ResetPasswordDialog from '@/views/ResetPasswordDialog.vue'
    
    const step = ref('')       // 當前流程階段：'', 'register', 'login', 'email', 'verifyEmail'
    const userEmail = ref('')  // 暫存註冊 email
    const router = useRouter()

    // 假設Email已註冊，按繼續
    function handleRegisterEmail(email) {
        userEmail.value = email
        step.value = 'verifyEmail'
    }

    // email 檢查流程（後端查詢）
    // async function handleRegisterEmail(email) {
    //     userEmail.value = email
    //     // 這裡可改成你自己的 API 路徑
    //     const res = await fetch('/api/check-email', {
    //     method: 'POST',
    //     headers: { 'Content-Type': 'application/json' },
    //     body: JSON.stringify({ email })
    //     }).then(r => r.json())
    
    // 登入流程（Email 輸入完後處理）
    function handleLoginEmail(email) {
        userEmail.value = email;
        // 這裡你未來會用 fetch 查詢後端
        // 這裡直接假設有註冊
        const exists = true; // 你之後用API結果

        if (exists) {
            step.value = 'loginPassword'
        } else {
            alert('此 email 尚未註冊，請先註冊');
            step.value = 'register'
        }
    }
    
    // 寄送驗證信
    function handleSendVerification() {
        // 可加發信 API
        alert('已寄出驗證信到 ' + userEmail.value)
        step.value = ''
        router.push('/verify-pending')
    }

    function handlePasswordLogin({ email, password }) {
        alert(`登入成功：${email} / ${password}`)
        step.value = ''
    }

    function handleForgotSubmit(email) {
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