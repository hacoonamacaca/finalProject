    <template>
        <div class="container text-center mt-5">
            <h2 class="mb-4">歡迎光臨！</h2>
            <div class="d-grid gap-3 col-6 mx-auto position-relative">
                <!-- 未登入：同時顯示 註冊 / 登入 -->
                <template v-if="!userFirstName">
                    <button class="btn btn-primary btn-lg" @click="handleRegister">註冊</button>
                    <button class="btn btn-outline-primary btn-lg" @click="step = 'loginEmail'">登入</button>
                </template>

                <!-- 已登入：顯示名字 + 下拉選單 -->
                <template v-else>
                    <div class="fs-4 dropdown-toggle user-dropdown" @click="toggleMenu">
                        <i class="bi bi-person-circle me-1"></i>{{ userFirstName }}
                    </div>
                    <ul v-if="showMenu" class="custom-dropdown text-start">
                        <li @click="goProfile">
                            <i class="bi bi-person me-2"></i>個人檔案
                        </li>
                        <li @click="logout">
                            <i class="bi bi-box-arrow-right me-2"></i>登出
                        </li>
                    </ul>
                </template>
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

    function handleRegister() {
    router.push('/registerBusiness')
    }

    function handleLoginEmail(email) {
    userEmail.value = email
    step.value = 'loginPassword'
    }

    function handlePasswordLogin({ email, password }) {
    // 假設這裡呼叫 API 成功，並取回 firstName
    const apiFirstName = '王小明'
    localStorage.setItem('userFirstName', apiFirstName)
    userFirstName.value = apiFirstName
    step.value = ''
    }

    function handleForgotSubmit(email) {
    step.value = ''
    showReset.value = true
    }

    function onResetPassword(newPwd) {
    showReset.value = false
    step.value = ''
    }
    </script>

    <style scoped>
    .user-dropdown {
    cursor: pointer;
    user-select: none;
    position: relative;
    }

    .custom-dropdown {
    position: absolute;
    top: 3rem;
    left: 50%;
    transform: translateX(-50%);
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 4px 0;
    min-width: 160px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
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
    margin-right: 8px;
    }
    </style>
