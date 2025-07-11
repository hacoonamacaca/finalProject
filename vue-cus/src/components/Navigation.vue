<template>
  <header class="navbar">
    <a class="navbar-brand d-flex align-items-center gap-3" style="cursor: pointer" @click="$router.push('/search')">
      <img src="@/assets/logo.png" alt="Logo" height="80" />
      <span class="brand-title">金碗GoldenBowl</span>
    </a>
    <!-- 行動版專用的 location-btn -->
    <div class="location-btn-container mobile-only">
      <button class="location-btn" @click="showPopout = true">
        目前位置為： {{ address }}
        <i class="bi bi-geo-alt-fill ms-2" @click.stop="getCurrentLocationAndNavigate"></i>
      </button>
    </div>
    <!-- 桌機版專用的 location-btn -->
    <div class="location-btn-container desktop-only">
      <button class="location-btn" @click="showPopout = true">
        目前位置為： {{ address }}
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
        <a href="#" @click.prevent="toggleRestaurantMenu" :title="isRestaurant ? '餐廳' : '餐點'"
          class="nav-item d-flex align-items-center gap-2">
          <i :class="isRestaurant ? 'fas fa-store' : 'fas fa-utensils'"></i>
          <span>{{ isRestaurant ? '餐廳' : '餐點' }}</span>
        </a>

        <!-- 優惠通知鈴鐺 -->
        <div class="nav-item" style="position: relative;">
          <button class="btn position-relative" style="background: transparent; border: none;"
            @click="toggleNotification" title="優惠通知">
            <i class="bi bi-bell-fill text-white"></i>
            <span v-if="unreadCount > 0"
              class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
              {{ unreadCount }}
            </span>
          </button>
          <NotificationList :visible="isNotificationOpen" :notifications="notifications" @mark-as-read="markAsRead" />
        </div>

        <!-- 購物車按鈕 -->
        <div class="nav-item">
          <button class="btn position-relative" style="background: transparent; border: none;" @click="showCart"
            title="購物車">
            <i class="bi bi-cart4 text-white"></i>
            <!-- <span v-if="cartCount > 0"
              class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
              {{ cartCount }}
            </span> -->
          </button>
        </div>
      </div>
    </div>
  </header>

  <!-- 購物車模態框 -->
  <CartModal v-if="isCartVisible" :cartByRestaurant="cartByRestaurant" :totalAmount="totalAmount" @close="hideCart"
    @update-quantity="updateQuantity" @remove-item="removeItem" @checkout-restaurant="handleCheckoutRestaurant"
    @checkout-all="handleCheckoutAll" @clear-restaurant="clearRestaurant" />
  <section class="popout" v-if="showPopout">


    <div class="popout-content">
      <button class="close-btn" @click="showPopout = false">✕</button>
      <input type="text" placeholder="輸入您的地址" @focus="address = ''" v-model="address" />
      <button class="search-btn" @click="address.trim() ? searchAddress() : getCurrentLocationAndNavigate()">搜尋</button>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UserDropdown from '@/components/Jimmy/UserDropdown.vue';
import NotificationList from '@/components/Yifan/NotificationList.vue';
import CartModal from '@/components/KTlu/CartModal.vue';
import { useCartStore } from '@/stores/cart';

// 購物車 store
const cartStore = useCartStore();

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

// 購物車相關的計算屬性和方法
const cartCount = computed(() => cartStore.cartCount);
const cartByRestaurant = computed(() => cartStore.cartByRestaurant);
const totalAmount = computed(() => cartStore.totalAmount);
const isCartVisible = computed(() =>  cartStore.isCartVisible );

const showCart = () => cartStore.showCart();
const hideCart = () => cartStore.hideCart();
const updateQuantity = (itemId, newQuantity, restaurantId) => cartStore.updateQuantity(itemId, newQuantity, restaurantId);
const removeItem = (itemId, restaurantId) => cartStore.removeItem(itemId, restaurantId);
const clearRestaurant = (restaurantId) => cartStore.clearRestaurantCart(restaurantId);

const handleCheckoutRestaurant = (restaurantId) => {
  const orderData = cartStore.checkoutSingleRestaurant(restaurantId);
  if (orderData) {
    console.log('單一餐廳結帳：', orderData);
    cartStore.hideCart();
    // 可以導航到結帳頁面
    // router.push('/checkout', { state: { orderData } });
  }
};

const handleCheckoutAll = () => {
  const orders = cartStore.checkoutAllRestaurants();
  if (orders.length > 0) {
    console.log('全部餐廳結帳：', orders);
    cartStore.hideCart();
    // 可以導航到結帳頁面
    // router.push('/checkout', { state: { orders } });
  }
};

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
    showPopout.value = false;
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
  console.log("顯示")
  console.log( cartStore.isCartVisible)
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

.navbar {
  background-color: #ffba20;
  color: white;
  padding: 5px 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 3000;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 15px;
}

.location-btn {
  background-color: transparent;
  color: white;
  border: 1px solid #fff;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s ease;
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
  border-radius: 12px;
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
  border-radius: 8px;
  margin-bottom: 15px;
}

.search-btn {
  padding: 10px 20px;
  background: #ffba20;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  transition: background-color 0.3s ease;
}

.search-btn:hover {
  background: #e0a518;
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
  border-radius: 2px;
  transition: all 0.3s ease;
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
}

.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg) translate(7px, -7px);
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-links .auth-section a,
.nav-links .nav-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: color 0.3s ease;
}

.nav-links .nav-item i {
  font-size: 20px;
}

.nav-links .nav-item:hover,
.nav-links .auth-section a:hover {
  color: #ffe082;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
  }

  .nav-links {
    position: fixed;
    top: 0;
    right: 0;
    height: 100%;
    width: 250px;
    background-color: #ffba20;
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
    transform: translateX(100%);
    box-shadow: -4px 0 8px rgba(0, 0, 0, 0.2);
    z-index: 2000;
  }

  .nav-links.active {
    transform: translateX(0);
  }

  .nav-links .auth-section {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
    padding-bottom: 15px;
    margin-bottom: 15px;
    order: -1;
  }

  .nav-links .nav-items {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
    gap: 15px;
  }

  .nav-links .nav-item,
  .nav-links .auth-section a {
    padding: 10px 0;
    width: 100%;
    font-size: 18px;
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
    border-radius: 8px;
  }

  .navbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 15px;
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
