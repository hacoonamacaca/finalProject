<script setup>
import { ref } from 'vue'
import Login from '../views/Login.vue'
import ProfileModal from '@/views/ProfileModal.vue'
import defaultAvatar from '@/assets/images/123.jpg'

const isLoggedIn = ref(false)
const showLogin = ref(false)
const showProfile = ref(false)
const userName = ref('')
const avatarUrl = ref(defaultAvatar)

function onLoginSuccess({ name, avatar }) {
    isLoggedIn.value = true
    userName.value = name
    avatarUrl.value = avatar || defaultAvatar
    showLogin.value = false
}
function logout() {
    isLoggedIn.value = false
    userName.value = ''
    avatarUrl.value = defaultAvatar
}
function updateAvatar(newUrl) {
    avatarUrl.value = newUrl
    showProfile.value = false
}
</script>

<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom" style="height:56px;">
        <div class="container-fluid d-flex justify-content-between w-100 align-items-center">
            <div></div>
            <div class="d-flex align-items-center">
                <!-- 功能選單，永遠顯示，左邊是 📂 -->
                <div class="dropdown me-2">
                    <button class="dropdown-toggle action-plain-btn" type="button" id="quickActionsDropdown" data-bs-toggle="dropdown">
                    📂
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><router-link class="dropdown-item" to="/promotion">行銷活動</router-link></li>
                        <li><router-link class="dropdown-item" to="/product">我的商品</router-link></li>
                        <li><router-link class="dropdown-item" to="/coupon">我的領率</router-link></li>
                        <li><router-link class="dropdown-item" to="/advertise">廣告</router-link></li>
                        <li><router-link class="dropdown-item" to="/feedback">意見反饋</router-link></li>
                        <li><router-link class="dropdown-item" to="/expert">專家諮詢</router-link></li>
                    </ul>
                </div>
                <!-- 右邊登入按鈕或頭像/帳號 -->
                <button
                    v-if="!isLoggedIn"
                    class="btn btn-login"
                    @click="showLogin = true"
                >登入</button>
                <div v-else class="dropdown">
                    <a class="d-flex align-items-center dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <img :src="avatarUrl" alt="avatar" class="rounded-circle me-2" style="width:32px; height:32px; object-fit:cover;" />
                        <span>{{ userName }}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li>
                            <a class="dropdown-item" href="#" @click.prevent="showProfile = true">個人資料</a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#" @click.prevent="logout">登出</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- 彈窗 -->
        <Login v-if="showLogin" @close="showLogin = false" @login-success="onLoginSuccess" />
        <ProfileModal v-if="showProfile" :avatarUrl="avatarUrl" @update-avatar="updateAvatar" @close="showProfile = false"/>
    </nav>
</template>

<style scoped>
.action-plain-btn 
{
    background: transparent;
    border: none;
    outline: none;
    box-shadow: none;
    font-size: 1.25rem;   /* 可依喜好調整大小 */
    padding: 4px 8px;
    cursor: pointer;
}
.action-plain-btn:active,
.action-plain-btn:focus 
{
    background: transparent;
    border: none;
    box-shadow: none;
}
</style>