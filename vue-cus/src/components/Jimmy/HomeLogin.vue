<template>
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
    <RegisterModal :show="step === 'register'" @close="step = ''" @register="step = 'email'"
        @login="step = 'loginEmail'" />
    <!-- 第二步，Email 驗證步驟 -->
    <RegisterEmailModal :show="step === 'email'" @close="step = ''" @back="step = 'register'"
        @submit="handleRegisterEmail" />
    <!-- 第三步，驗證信 modal -->
    <VerifyEmailModal :show="step === 'verifyEmail'" :email="userEmail" @close="step = ''" @back="step = 'email'"
        @send="handleSendVerification" />
    <!-- 登入 Email Modal -->
    <LoginEmailModal :show="step === 'loginEmail'" @close="step = ''" @submit="handleLoginEmail"
        @register="step = 'register'" />
    <!-- 登入密碼 -->
    <LoginPasswordModal :show="step === 'loginPassword'" :email="userEmail" @close="step = ''"
        @forgot="step = 'forgotPassword'" @back="step = 'loginEmail'" @login="handlePasswordLogin" />
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
import { ref, onMounted, onUnmounted, watch } from 'vue';
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
onMounted(() => {
    userFullName.value = localStorage.getItem('userFullName') || ''
})

function logout() {
    localStorage.removeItem('userFullName')
    userFullName.value = ''
    showMenu.value = false
}

// 假設Email已註冊，按繼續
function handleRegisterEmail(email) {
    userEmail.value = email
    step.value = 'verifyEmail'
}

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
    alert(`已寄出驗證信到 ${userEmail.value}`)
    step.value = ''
    router.push('/verify-pending')
}

function handlePasswordLogin({ email, password }) {
    alert(`登入成功：${email} / ${password}`)
    let name = localStorage.getItem('userFullName')
    if (!name) {
        name = email // 用 email 當暫時顯示名稱
        localStorage.setItem('userFullName', name)
    }
    localStorage.setItem('userEmail', email)
    userFullName.value = name
    step.value = ''
    router.push('/search')
}

function handleForgotSubmit(email) {
    alert('已寄送重設密碼信到 ' + email)
    step.value = ''
    router.push({
        path: '/resetPasswordEmail',
        query: (email)
    })
}

//------------------------------
const isLoggedIn = ref(true);
const cUser = ref(isLoggedIn.value ? "目前使用者*" : "請選擇登入身分"); // 修改初始值

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

onMounted(() => {
    document.addEventListener('click', handleClickOutside);
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
