<template>
  <div class="user-dropdown-container">
    <a href="#" class="user-link" @click.prevent="toggleDropdown">
      使用者*
    </a>
    <div class="dropdown-menu" v-if="showDropdown">
      <ul>
        <li @click="navigateTo('profile')">個人資料</li>
        <li @click="navigateTo('VoucherWallet')">優惠券</li>
        <li @click="navigateTo('subscription')">訂閱</li>
        <li @click="navigateTo('favorites')">收藏管理</li>
        <li @click="navigateTo('orderList')">歷史訂單</li>
        <li @click="navigateTo('/')">回首頁*</li>
        <li @click="navigateTo('secure/login')">test-login</li>
        <li @click="logout">登出</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

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

// 登出邏輯
const logout = () => {
  localStorage.removeItem('token');
  showDropdown.value = false;
  router.push('/login');
};

// 點擊外部關閉下拉選單
const handleClickOutside = (event) => {
  if (!event.target.closest('.user-dropdown-container')) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
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
