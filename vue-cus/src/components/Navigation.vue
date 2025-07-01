<template>
  <header class="navbar">
    <a class="navbar-brand d-flex align-items-center gap-3" style="cursor: pointer" @click="$router.push('/search')">
  <img src="@/assets/logo.png" alt="Logo" height="80" />
  <span class="brand-title">金碗GoldenBowl Foolog</span>
</a>


  <!-- 行動版專用 -->
  <div class="location-btn-container mobile-only">
    <button class="location-btn" @click="showPopout = true">
      目前位置為：{{ address }}
      <i class="bi bi-geo-alt-fill ms-2" @click.stop="getCurrentLocationAndNavigate"></i>
    </button>
  </div>

  <!-- 桌機版專用 -->
  <div class="location-btn-container desktop-only">
    <button class="location-btn" @click="showPopout = true">
      目前位置為：{{ address }}
      <i class="bi bi-geo-alt-fill ms-2" @click.stop="getCurrentLocationAndNavigate"></i>
    </button>
  </div>

    <button class="hamburger" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </button>
    <div class="nav-links" :class="{ active: isMenuOpen }">
      <!-- auth-section 在行動版置頂 -->
      <div class="auth-section">
        <a href="#" @click="getLogin" v-if="!isLoggedIn">登入</a>
        <UserDropdown v-if="isLoggedIn" />
      </div>
      <!-- 其他導航項 -->
      <div class="nav-items">

        <!-- 餐廳/餐點按鈕 -->
        <a
          href="#"
          @click.prevent="toggleRestaurantMenu"
          :title="isRestaurant ? '餐廳' : '餐點'"
          class="text-white d-flex align-items-center gap-2 fs-5"
        >
          <i :class="isRestaurant ? 'fas fa-store' : 'fas fa-utensils'"></i>
        </a>

<!-- 優惠通知鈴鐺 -->
        <div style="position: relative;">
          <button
            class="btn position-relative"
            style="background: transparent; border: none;"
            @click="toggleNotification"
            title="優惠通知"
          >
            <i class="bi bi-bell-fill text-white fs-5"></i>
            <span v-if="unreadCount > 0"
              class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
              {{ unreadCount }}
            </span>
          </button>

          <!-- ✅ 通知清單元件 -->
          <NotificationList
            :visible="isNotificationOpen"
            :notifications="notifications"
            @mark-as-read="markAsRead"
          />
        </div>

        <!-- 購物車按鈕 -->
        <button
          class="btn position-relative"
          style="background: transparent; border: none;"
          @click="goToCart"
          title="購物車"
        >
          <i class="bi bi-cart4 text-white fs-5"></i>
          <span v-if="cartCount > 0"
                class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
            {{ cartCount }}
          </span>
        </button>
      
      </div>
    </div>
  </header>
  <section class="popout" v-if="showPopout">
    <div class="popout-content">
      <button class="close-btn" @click="showPopout = false">✕</button>
      <input type="text" placeholder="輸入您的地址" @focus="address = ''" v-model="address" />
      <button class="search-btn" @click="searchAddress">搜尋</button>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UserDropdown from '@/components/Jimmy/UserDropdown.vue';
import NotificationList from '@/components/Yifan/NotificationList.vue'

const isLoggedIn = ref(true); // 根據實際登入狀態設定
const isMenuOpen = ref(false);
const showPopout = ref(false);
const isRestaurant = ref(true);
const route = useRoute();
const router = useRouter();
const address = ref(route.query.address || '');
const coordinates = ref(null);
const loading = ref(false);
const error = ref('');

// 控制漢堡選單
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

// 餐廳/餐點切換
const toggleRestaurantMenu = () => {
  isRestaurant.value = !isRestaurant.value;
  console.log("目前頁面餐廳為是/餐點為否:" + isRestaurant.value);
};
// 優惠通知邏輯
const isNotificationOpen = ref(false)
const toggleNotification = () => isNotificationOpen.value = !isNotificationOpen.value

const notifications = ref([
  { id: 1, title: '🎁 全站85折限時優惠', date: '2025-06-30', is_read: false },
  { id: 2, title: '🍔 餐點類優惠券即將到期', date: '2025-06-29', is_read: false },
  { id: 3, title: '🎉 註冊送折扣券', date: '2025-06-28', is_read: true }
])

const unreadCount = computed(() => notifications.value.filter(n => !n.is_read).length)
const markAsRead = (item) => { item.is_read = true }

// 搜尋地址
const searchAddress = async () => {
  const success = await getCoordinates();
  if (success) {
    showPopout.value = false;
    router.push({
      path: '/search',
      query: { address: address.value }
    });
  }
};

// 獲取當前位置並導航
const getCurrentLocationAndNavigate = async () => {
  const success = await getCurrentLocation();
  if (success) {
    router.push({
      path: '/search',
      query: { address: address.value }
    });
  }
};

// 格式化地址
const formatAddress = (input) => {
  if (!input.trim()) return input;
  const fullAddressRegex = /^(\S+?)([縣市])(.+?)(區|鄉|鎮|市)(.*?)(\d+號)/;
  const simpleAddressRegex = /^(.+?)(\d+號)$/;
  let match = input.match(fullAddressRegex);
  if (match) {
    return `${match[1]}${match[2]}${match[3]}${match[4]}${match[5].trim()}${match[6]}`;
  }
  match = input.match(simpleAddressRegex);
  if (match) {
    return `${match[1].trim()}${match[2]}`;
  }
  return input;
};

// 查詢座標
const getCoordinates = async () => {
  if (!address.value.trim()) {
    error.value = '請輸入地址';
    coordinates.value = null;
    return false;
  }
  address.value = formatAddress(address.value);
  loading.value = true;
  error.value = '';
  coordinates.value = null;
  try {
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address.value)}`;
    const response = await fetch(url, {
      headers: { 'User-Agent': 'Jimmy/tokin81@yahoo.com.tw' }
    });
    const data = await response.json();
    if (data.length > 0) {
      coordinates.value = { lat: data[0].lat, lon: data[0].lon };
      return true;
    } else {
      error.value = '無法找到該地址的座標';
      return false;
    }
  } catch (err) {
    error.value = '查詢失敗，請稍後再試';
    console.error('API 錯誤:', err);
    return false;
  } finally {
    loading.value = false;
  }
};

// 獲取當前位置
const getCurrentLocation = async () => {
  if (!navigator.geolocation) {
    alert('您的瀏覽器不支援定位功能');
    return false;
  }
  try {
    const position = await new Promise((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(resolve, reject);
    });
    const { latitude, longitude } = position.coords;
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&addressdetails=1`
    );
    const data = await response.json();
    if (data && data.display_name) {
      address.value = formatTaiwanAddress(data.address);
      return true;
    } else {
      alert('無法解析地址，請稍後再試');
      return false;
    }
  } catch (error) {
    console.error('定位失敗:', error);
    alert('無法獲取位置，請檢查權限或稍後再試');
    return false;
  }
};

// 格式化台灣地址
const formatTaiwanAddress = (addressData) => {
  if (!addressData) return '';
  const country = addressData.country || '臺灣';
  const city = addressData.city || addressData.county || '';
  const district = addressData.suburb || addressData.town || addressData.city_district || '';
  const village = addressData.neighbourhood || addressData.village || '';
  const road = addressData.road || '';
  let houseNumber = addressData.house_number || '';
  if (houseNumber.includes('之')) {
    houseNumber = houseNumber.split('之')[1] || houseNumber;
  }
  return [country, city, district, village, road, houseNumber].filter(part => part).join('');
};

// 監聽路由變化
watch(() => route.query.address, (newAddress) => {
  address.value = newAddress || '';
});

onMounted(() => {
  address.value = route.query.address || '';
});

// 模擬登入函數
const getLogin = () => {
  isLoggedIn.value = true; // 模擬登入
};
</script>

<style scoped>
.brand-title {
  color: #5c3203;
  font-weight: bold;
  font-size: 1.5rem;
}

.notification-panel {
  top: 60px; /* 根據你的 navbar 高度調整 */
  right: 0px; /* 讓箭頭正對鈴鐺 */
}
.arrow-up {
  right: 25px; /* 根據鈴鐺位置微調 */
}

.navbar {
  background-color: #ffba20;
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 3000;
}

.navbar .logo {
  font-size: 26px;
  font-weight: bold;
}

.navbar .nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar .nav-links a {
  display: block;
  color: white;
  font-size: 18px;
  margin: 10px 0;
  width: 100%;
  text-align: left;
  text-decoration: none;
  font-size: 16px;
}

.location-btn {
  background-color: transparent;
  color: white;
  border: 1px solid #fff;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.location-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.popout {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.popout-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  position: relative;
  text-align: center;
}

.popout-content input {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 15px;
}

.search-btn {
  padding: 10px 20px;
  background: #ffba20;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
}

.search-btn:hover {
  background: #ffba20;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
}

.close-btn:hover {
  color: #ffba20;
}

.hamburger {
  display: none;
  flex-direction: column;
  justify-content: space-around;
  width: 30px;
  height: 25px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 3000;
}

.hamburger span {
  width: 100%;
  height: 3px;
  background: white;
  transition: all 0.3s ease;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
  }

  .nav-links {
    position: absolute;
    border-radius: 10px;
    top: 100%;
    right: 0;
    height: 300px;
    width: 150px;
    background-color: #ffba20;
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
    opacity: 0;
    visibility: hidden;
    display: none;
    z-index: 2000;
  }

  .nav-links.active {
    opacity: 1;
    visibility: visible;
    display: flex;
  }

  .nav-links .auth-section {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
    padding-bottom: 10px;
    margin-bottom: 10px;
    order: -1;
    /* 確保置頂 */
  }

  .nav-links .auth-section .user-dropdown {
    width: 100%;
    text-align: left;
  }

  .nav-links .nav-items {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }

  .nav-links .nav-items {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }

  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: flex;
    width: 100%;
    margin-top: 10px;
    justify-content: flex-start;
  }

  .location-btn-container.mobile-only .location-btn {
    width: 100%;
    text-align: left;
    justify-content: space-between;
  }

  .navbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 15px;
  }

  .navbar .logo {
    width: 100%;
    margin-bottom: 10px;
  }

  .hamburger {
    position: absolute;
    top: 15px;
    right: 15px;
  }
}

@media (min-width: 769px) {
  .nav-links {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 20px;
  }

  .nav-links .auth-section {
    order: 1;
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .nav-links .nav-items {
    order: 0;
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .mobile-only {
    display: none;
  }

  .desktop-only {
    display: flex;
    align-items: center;
  }
}
</style>