<template>
    <div class="page-wrapper">
        <!-- Header: 在 Flex 容器中，它是一個獨立的區塊 -->
        <header class="navbar d-flex justify-content-between align-items-center px-4 py-2 shadow-sm"
            style="background-color: #ffba20;">
            <a class="navbar-brand d-flex align-items-center gap-3" style="cursor: pointer"
                @click="$router.push('/vendor/dashboard')">
                <img :src="logoUrl" alt="Logo" height="80" />
                <span class="brand-title">商家管理中心</span>
            </a>
            <!-- 右側使用者資訊 -->
            <div class="d-flex align-items-center gap-3">
                <span class="text-white fw-semibold">
                    {{ currentUser ? currentUser.ownerFullName || currentUser.ownerEmail || '商家' : '使用者' }}，您好！
                </span>
                <!-- 純 Vue 控 dropdown -->
                <div ref="iconDropdownRef" class="position-relative">
                    <i
                        class="bi bi-person-circle text-white"
                        style="font-size: 2rem; cursor:pointer"
                        @click="onUserIconClick"
                    ></i>
                    <ul
                        v-if="isLoggedIn && showDropdown"
                        class="dropdown-menu dropdown-menu-end show"
                        style="position: absolute; right: 0; top: 110%;"
                    >
                        <li>
                            <a class="dropdown-item" href="#" @click.prevent="logout">登出</a>
                        </li>
                    </ul>
                </div>
            </div>
        </header>

        <div class="main-container">
            <!-- 側邊欄連結使用 router-link 並透過 "to" 屬性指定目標路徑-->

            <!-- Sidebar -->
            <nav class="sidebar">
                <div class="sidebar-sticky">
                    <!-- 商家資訊 -->
                    <div class="sidebar-section">
                        <h6 class="section-title">管理你的商家資訊</h6>
                        <ul class="nav flex-column">
                            <li class="nav-item">
                                <router-link to="#" class="nav-link" active-class="active-link">
                                    <i class="fas fa-user-gear fa-fw me-2"></i> 商家資料
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <router-link to="/store/menu" class="nav-link" active-class="active-link">
                                    <i class="fas fa-utensils fa-fw me-2"></i> 菜單管理
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <router-link to="#" class="nav-link" active-class="active-link">
                                    <i class="fas fa-store fa-fw me-2"></i> 店鋪管理
                                </router-link>
                            </li>
                        </ul>
                    </div>

                    <!-- 主要功能 -->
                    <div class="sidebar-section">
                        <h6 class="section-title">主要功能設定</h6>
                        <ul class="nav flex-column mb-2">
                            <li class="nav-item">
                                <router-link to="/store/orders" class="nav-link" active-class="active-link">
                                    <i class="fas fa-file-invoice fa-fw me-2"></i> 訂單管理
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <router-link to="#" class="nav-link" active-class="active-link">
                                    <i class="fas fa-chair fa-fw me-2"></i> 訂位管理
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <router-link to="/store/hours" class="nav-link" active-class="active-link">
                                    <i class="fas fa-clock fa-fw me-2"></i> 營業時間
                                </router-link>
                            </li>
                            <li class="nav-item">
                                <router-link to="#" class="nav-link" active-class="active-link">
                                    <i class="fas fa-comments fa-fw me-2"></i> 評論回覆
                                </router-link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- 主內容區域 -->
            <main class="main-content p-4">
                <router-view />
            </main>
        </div>

        <!-- Footer -->
        <footer class="bg-warning text-white text-center p-3 flex-shrink-0">
            Footer
        </footer>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import logoUrl from '../assets/logo.png';

// 響應式資料
const iconDropdownRef = ref(null)
const showDropdown = ref(false)
const currentUser = ref(null)

// 計算屬性
const isLoggedIn = computed(() => !!currentUser.value?.ownerId)

// 方法
const loadUserData = () => {
    const ownerId = localStorage.getItem('ownerId')
    const ownerFullName = localStorage.getItem('storeFullName')
    const ownerEmail = localStorage.getItem('storeEmail')
    
    if (ownerId) {
        currentUser.value = {
            ownerId,
            ownerFullName,
            ownerEmail
        }
    }
}

const onUserIconClick = () => {
    console.log('點擊用戶圖示:', isLoggedIn.value) // 除錯用
    if (isLoggedIn.value) {
        showDropdown.value = !showDropdown.value
        console.log('showDropdown:', showDropdown.value) // 除錯用
    }
}

const handleClickOutside = (event) => {
    if (iconDropdownRef.value && !iconDropdownRef.value.contains(event.target)) {
        showDropdown.value = false
    }
}

const logout = () => {
    // 清除本地儲存的用戶資料
    localStorage.removeItem('ownerId')
    localStorage.removeItem('storeFullName')
    localStorage.removeItem('storeEmail')
    localStorage.removeItem('storeId')
    localStorage.removeItem('storeProfile')
    
    // 重設本地狀態
    currentUser.value = null
    showDropdown.value = false
    
    // 跳轉回 vue-cus 登入頁面
    const vueCustomerUrl = import.meta.env.VITE_VUE_CUS_URL || 'http://localhost:5173'
    window.location.href = `${vueCustomerUrl}/store`
}

// 生命週期
onMounted(() => {
    loadUserData()
    document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
})

</script>


<style scoped>
.brand-title {
    color: #5c3203;
    font-weight: bold;
    font-size: 1.5rem;
}

.dropdown-menu {
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    min-width: 120px;
    z-index: 1050;
}

.dropdown-item {
    padding: 8px 16px;
    color: #333;
    text-decoration: none;
    transition: background-color 0.2s;
}

.dropdown-item:hover {
    background-color: #f8f9fa;
}

/* 確保 Bootstrap Icons 正常顯示 */
.bi-person-circle:hover {
    opacity: 0.8;
    transform: scale(1.05);
    transition: all 0.2s ease;
}

.page-wrapper {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #fdfaf5;
}

.main-container {
    display: flex;
    flex-grow: 1;
    overflow: hidden;
}

.sidebar {
    flex-shrink: 0;
    width: 250px;
    overflow-y: auto;
    border-right: 1px solid #dee2e6;
    background-color: #f7f7f7;
    padding: 2rem;
    position: relative; /* << 新增：讓它建立一個堆疊上下文 */
    z-index: 20; /* << 新增：給它一個較高的層級，確保它在最上面 */
}

.sidebar-section {
    margin-bottom: 2rem;
}

.section-title {
    font-size: 16px;
    font-weight: bold;
    color: #212529;
    margin-bottom: 0.5rem;
    position: relative;
    padding-left: 1.25rem;
}

.section-title::before {
    content: '';
    display: inline-block;
    width: 6px;
    height: 6px;
    background-color: #212529;
    border-radius: 50%;
    position: absolute;
    left: 0;
    top: 6px;
}

.nav-link {
    display: flex;
    align-items: center;
    font-size: 15px;
    color: #6c757d;
    background-color: transparent;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    transition: all 0.3s ease;
    transform: translateX(0);
}

/* 圖示顏色 */
.nav-link i {
    color: #6c757d;
    width: 18px;
    text-align: center;
}

.nav-link:hover i {
    color: #eca300;
    width: 18px;
    text-align: center;
}

/* 當滑鼠移到按鈕時改變背景色和文字顏色 */
.nav-link:hover {
    background-color: #fcebc1;
    color: #eca300;
    transform: translateX(4px);
}

/* 移除 router-link 的預設藍框與藍字 */
.nav-link:focus,
.nav-link:active {
    outline: none !important;
    box-shadow: none !important;
    color: #3e2723 !important;
    background-color: #e8d4ae !important;
    text-decoration: none !important;
}

.main-content {
    flex-grow: 1;
    /* overflow-y: auto; */
    background-color: white;
    position: relative; /* << 新增：也建立堆疊上下文，成為子頁面絕對定位的基準 */
    z-index: 10; /* << 新增：層級比 sidebar 低，但比預設高 */
}



/* 確保 header 和 footer 不會被壓縮 */
header,
footer {
    flex-shrink: 0;
}
</style>