<template>
    <div style="position:fixed;top:0;left:0;z-index:99999;color:red;">
        step: {{ step }}, email: {{ userEmail }}
    </div>

    <div class="user-dropdown-container">
        <a href="#" class="user-link" @click.prevent="toggleDropdown">
            {{ cUser }}
        </a>
        <div class="dropdown-menu" v-if="showDropdown">
            <ul>
                <li @click="step = 'register'">會員*</li>
                <li @click="navigateTo('store')">餐廳方*</li>
                <li @click="navigateTo('admin')">管理者*</li>
                <li @click="logout">登出</li>
            </ul>
        </div>
    </div>

    <!-- 註冊第一步 -->
    <RegisterModal
        :show="step === 'register'"
        @close="step = ''"
        @register="step = 'email'"
        @login="step = 'loginEmail'"
        @google-login="onGoogleLogin"
    />
    <!-- 第二步，Email 驗證步驟 -->
    <RegisterEmailModal
        :show="step === 'email'"
        @close="step = ''"
        @back="step = 'register'"
        @submit="handleRegisterEmail"
        />
<!-- 第三步，驗證信 modal -->
    <VerifyEmailModal
        :show="step === 'verifyEmail'"
        :email="userEmail"
        @close="step = ''"
        @back="step = 'email'"
        @send="handleSendVerification"  
        />
    <!-- 登入 Email Modal -->
    <LoginEmailModal
        :show="step === 'loginEmail'"
        @submit="handleLoginEmail"
        @close="step = ''"
        />
    <!-- 登入密碼 -->
    <LoginPasswordModal
        :show="step === 'loginPassword'"
        :email="userEmail"
        @login="handlePasswordLogin"
        @close="step = ''"
        @back="step = 'loginEmail'"
    />
    <!-- 忘記密碼 -->
    <ForgotPasswordModal :show="step === 'forgotPassword'" :email="userEmail" @close="step = ''"
        @back="step = 'loginPassword'" @submit="handleForgotSubmit" />
    <ForgotPasswordSentModal :show="step === 'forgotSent'" @close="step = ''" @back="step = 'loginPassword'"
        @backToLogin="step = 'loginEmail'" />
    <ResetPasswordSentModal :show="step === 'resetPasswordSent'" @close="step = ''" @back="step = 'loginPassword'" />
    <!-- or step = 'loginEmail' 依你的流程-->
    <ResetPasswordDialog v-if="showReset" @close="showReset = false" @submit="onResetPassword" />
</template>

<script setup>
console.log('LoginEmailModal.vue loaded!')
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router';
import RegisterModal from '@/components/Ivy/RegisterModal.vue'
import LoginEmailModal from '@/views/Ivy/LoginEmailModal.vue'
import RegisterEmailModal from '@/views/Ivy/RegisterEmailModal.vue'
import VerifyEmailModal from '@/views/Ivy/VerifyEmailModal.vue'
import LoginPasswordModal from '@/views/Ivy/LoginPasswordModal.vue'
import ForgotPasswordModal from '@/views/Ivy/ForgotPasswordModal.vue'
import ForgotPasswordSentModal from '@/views/Ivy/ForgotPasswordSentModal.vue'
import ResetPasswordSentModal from '@/views/Ivy/ResetPasswordSentModal.vue'
import ResetPasswordDialog from '@/views/Ivy/ResetPasswordDialog.vue'
//-----Ivy----------------------
const step = ref('')            // 控制哪個modal開
const userEmail = ref('')       // 存email
const userFullName = ref('')   // 登入後要顯示的名字
const showReset = ref(false)    // 顯示重設密碼 dialog
const userPhone = ref('')       // 存手機

onMounted(() => {
    document.addEventListener('click', handleClickOutside);

    userFullName.value = localStorage.getItem('userFullName') || '';
    userEmail.value = localStorage.getItem('userEmail') || '';
    userPhone.value = localStorage.getItem('userPhone') || '';
    isLoggedIn.value = !!userFullName.value
});

// 這邊只接收子組件 emit 上來的完整資料（更推薦這種寫法，登入 API 在 LoginPasswordModal.vue 內處理）
function handlePasswordLogin({ userFullName: name, userEmail: email, userPhone: phone }) {
  // 寫入 localStorage
    localStorage.setItem('userFullName', name)
    localStorage.setItem('userEmail', email)
    localStorage.setItem('userPhone', phone || '')
    userFullName.value = name
    userEmail.value = email
    userPhone.value = phone || ''
    isLoggedIn.value = true
    step.value = ''
    router.push('/search')
}

function logout() {
    localStorage.removeItem('userFullName')
    localStorage.removeItem('userEmail')
    localStorage.removeItem('userPhone')
    userFullName.value = ''
    userEmail.value = ''
    userPhone.value = ''
    isLoggedIn.value = false
    showDropdown.value = false  // 只留這行就好
    // router.push('/login')
}

// 假設Email已註冊，按繼續
function handleRegisterEmail(email) {
    userEmail.value = email
    step.value = 'verifyEmail'
}

// 登入流程（Email 輸入完後處理）
function handleLoginEmail(email) {
    userEmail.value = email
    step.value = 'loginPassword'
}

// 寄送驗證信
function handleSendVerification(email) {
    // email 可直接用 userEmail.value，也可以用 event 傳進來
    alert(`已寄出驗證信到 ${userEmail.value}`);
    step.value = '';
    router.push('/register-profile'); // 跳轉到驗證信已送出提示頁
}

function handleForgotSubmit(email) {
    alert('已寄送重設密碼信到 ' + email)
    step.value = ''
    router.push({
        path: '/resetPasswordEmail',
        query: (email)
    })
}

function onGoogleLogin() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

//------------------------------
const isLoggedIn = ref(false);
// cUser 直接用 computed，跟著 userFullName 和 isLoggedIn 動態變動
const cUser = computed(() =>
    isLoggedIn.value
    ? (userFullName.value ? userFullName.value : "目前使用者*")
    : "請選擇登入身分"
)

const showDropdown = ref(false);
const router = useRouter();

const toggleDropdown = () => {
    showDropdown.value = !showDropdown.value;
};

// 導航到對應頁面
const navigateTo = (path) => {
    router.push(`/${path}`);
    showDropdown.value = false;
};

//檢查初始登入狀態
// onMounted(() => {
//     document.addEventListener('click', handleClickOutside);
//     isLoggedIn.value = !!localStorage.getItem('token'); // 新增，根據 token 設定初始狀態
// });

// 登出邏輯
// const logout = () => {
// localStorage.removeItem('token');
// isLoggedIn.value = false; // 新增
// showDropdown.value = false;
// router.push('/login');
// };

// 點擊外部關閉下拉選單
const handleClickOutside = (event) => {
    if (!event.target.closest('.user-dropdown-container')) {
        showDropdown.value = false;
    }
};

// 監聽 isLoggedIn 變化，動態更新 cUser
watch(isLoggedIn, (newValue) => {
    cUser.value = newValue ? "目前使用者*" : "請選擇登入身分";
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});
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
