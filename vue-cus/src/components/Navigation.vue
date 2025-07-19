<template>
  <header class="navbar">
    <a class="navbar-brand d-flex align-items-center gap-3" style="cursor: pointer" @click="$router.push('/search')">
      <img src="@/assets/logo.png" alt="Logo" height="80" />
      <span class="brand-title">金碗GoldenBowl</span>
    </a>
    <div class="location-btn-container mobile-only">
      <button class="location-btn" @click="showPopout = true">
        目前位置為： {{ locationStore.address }}
        <i class="bi bi-geo-alt-fill ms-2" @click.stop="getCurrentLocationAndNavigate"></i>
      </button>
    </div>
    <div class="location-btn-container desktop-only">
      <button class="location-btn" @click="showPopout = true">
        目前位置為： {{ locationStore.address }}
        <i class="bi bi-geo-alt-fill ms-2" @click.stop="getCurrentLocationAndNavigate"></i>
      </button>
    </div>
    <button class="hamburger" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </button>
    <div class="nav-links" :class="{ active: isMenuOpen }">
      <div class="auth-section">
        <a href="#" @click="getLogin" v-if="!isLoggedIn">登入</a>
        <UserDropdown v-if="isLoggedIn" />
      </div>
      <div class="nav-items">
        <a v-if="route.path === '/search'" href="#" @click.prevent="restaurantDisplayStore.toggleDisplayMode()"
          :title="restaurantDisplayStore.showAllRestaurants ? '顯示已收藏' : '顯示全部'"
          class="nav-item d-flex align-items-center gap-2">
          <i :class="restaurantDisplayStore.showAllRestaurants ? 'fas fa-heart' : 'fas fa-store'"></i>
          <span>{{ restaurantDisplayStore.showAllRestaurants ? '已收藏' : '全部' }}</span>
        </a>

        <div class="nav-item" style="position: relative;">
        <!-- 小鈴鐺按鈕 -->
        <button class="btn position-relative" style="background: transparent; border: none;"
          @click.stop="toggleNotification" title="優惠通知">
          <i class="bi bi-bell-fill text-white"></i>
          <!-- 🔴 紅色徽章 -->
          <span v-if="unreadCount > 0"
            class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
            {{ unreadCount }}
          </span>
        </button>

        <!-- ✅ 優惠通知彈窗 -->
        <div
          v-if="isNotificationOpen"
          class="notification-dropdown"
          style="position: absolute; top: 100%; right: 0; z-index: 1050;"
        >
          <NotificationList
            :notifications="notifications"
            @mark-as-read="handleMarkAsRead"
            @close-list="isNotificationOpen = false"
            @mark-all-as-read="markAllNotificationsAsRead"
          />
        </div>

        <!-- ✅ 透明遮罩：點擊外部就會關閉彈窗 -->
        <div
          v-if="isNotificationOpen"
          class="notification-overlay"
          @click="isNotificationOpen = false"
        ></div>
      </div>

        <div class="nav-item" style="position: relative;">
          <button class="btn position-relative" style="background: transparent; border: none;" @click="showCart"
            title="購物車">
            <i class="bi bi-cart4 text-white"></i>
            <!-- 🛒 紅色徽章 -->
            <span
              v-if="cartCount > 0"
              class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
              {{ cartCount }}
            </span>
          </button>
        </div>
      </div>
    </div>
  </header>

  
  <!-- 購物車模態框 -->
  <CartModal v-if="isCartVisible" :cartByRestaurant="cartByRestaurant" :totalAmount="totalAmount" @close="hideCart"
    @update-quantity="updateQuantity" @remove-item="removeItem" @checkout-restaurant="handleCheckoutRestaurant"
    @clear-restaurant="clearRestaurant" />
    <!-- 預備結帳畫面  ted--> 
  <CheckOrderModal :isVisible="isCheckOrderVisible" :orderItems="currentCheckoutItems" :restId="Number(restId)" 
    @add-to-cart="handleConfirmCheckout" @close="hideCheckOrderModal" />

  <section class="popout" v-if="showPopout">
    <div class="popout-content">
      <button class="close-btn" @click="showPopout = false">✕</button>
      <input type="text" placeholder="輸入您的地址" @focus="locationStore.setAddress('')" v-model="locationStore.address" />
      <button class="search-btn" @click="locationStore.address.trim() ? searchAddress() : getCurrentLocationAndNavigate()">搜尋</button>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UserDropdown from '@/components/Jimmy/UserDropdown.vue';
import NotificationList from '@/components/Yifan/NotificationList.vue'; // 確保路徑正確
import CheckOrderModal from '@/components/Ted/CheckOrderModal.vue';

import CartModal from '@/components/KTlu/CartModal.vue';
import { useCartStore } from '@/stores/cart';
import { useLocationStore } from '@/stores/location'; // <-- 導入新的 location store
import { useRestaurantDisplayStore } from '@/stores/restaurantDisplay';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/user.js'; 
import axios from '@/plungins/axios.js';
// 這裡不需要 defineProps，因為 Navigation.vue 不是子組件，它沒有接收 props。
// 如果這個文件是子組件，並且需要接收 props，才需要 defineProps。

// 購物車 store
const cartStore = useCartStore();
// 位置 store
const locationStore = useLocationStore(); // <-- 實例化 location store
const restaurantDisplayStore = useRestaurantDisplayStore();

const isLoggedIn = ref(true); // 根據實際登入狀態設定
const isMenuOpen = ref(false);
const showPopout = ref(false);
const route = useRoute();
const router = useRouter();

// 購物車相關的計算屬性和方法 (保持不變)
const cartCount = computed(() => cartStore.cartCount);
const cartByRestaurant = computed(() => cartStore.cartByRestaurant);
const totalAmount = computed(() => cartStore.totalAmount);
const isCartVisible = computed(() => cartStore.isCartVisible);

const userStore = useUserStore(); // 實例化 userStore
const userId = ref(null); // 用於存儲從 Pinia 獲取的用戶 ID



const showDropdown = ref(false);
const showCart = () => cartStore.showCart();
const hideCart = () => cartStore.hideCart();
const updateQuantity = (itemId, newQuantity, restaurantId) => cartStore.updateQuantity(itemId, newQuantity, restaurantId);
const removeItem = (itemId, restaurantId) => cartStore.removeItem(itemId, restaurantId);
const clearRestaurant = (restaurantId) => cartStore.clearRestaurantCart(restaurantId);
const getRestaurantCart = (restaurantId) => cartStore.getRestaurantCart(restaurantId);
// 訂單確認模態框相關狀態 (新增) ted
const isCheckOrderVisible = ref(false);
const currentCheckoutItems = ref([]); // 用於儲存要傳遞給 CheckOrderModal 的商品
const restId=ref(1);
// 儲存準備結帳的訂單
const getCheckOrder =()=>restId;





// 更新的版本 ted準備CheckOrderModal
const handleCheckoutRestaurant = (restaurantId) => {

  const restaurantCart = cartStore.cartByRestaurant[restaurantId];
  if (restaurantCart && restaurantCart.items.length > 0) {
    currentCheckoutItems.value = JSON.parse(JSON.stringify(restaurantCart.items)); // 深拷貝一份商品數據
    hideCart(); // 隱藏購物車模態框
    isCheckOrderVisible.value = true; // 顯示訂單確認模態框
    restId.value = (restaurantId);

  } else {
    Swal.fire({
      icon: 'warning', // 警告圖示，也可以是 'error', 'success', 'info', 'question'
      title: '無法結帳', // 標題
      text: '該餐廳購物車是空的，無法結帳！', // 內容文字
      confirmButtonText: '確定', // 確認按鈕的文字
      customClass: {
        confirmButton: 'my-swal-confirm-button' // 可以為按鈕添加自定義 CSS 類別
      }
    });
  }
};
// ted 新增訂單
const handleConfirmCheckout = (restaruantId,orderData) => {
  // 結帳送出訂單
  if (!userId.value) { userId.value = 4 }
  //如果沒辦法取得userId.value暫時給值 4
  const body = {
    user :{
    id: 5 // 假設您的 Pinia store 中有 userId 屬性
    // id: userId.value // 假設您的 Pinia store 中有 userId 屬性
     // 假設您的 Pinia store 中有 promotionId 屬性
    },
    
  }

  // 將 body 的屬性複製到 existingObject (修改 existingObject)
  // Object.assign(target, source1, source2, ...);
  Object.assign( getRestaurantCart(restaruantId), orderData,body);
  
  isCheckOrderVisible.value = false;
  
  
  console.log('送出前的訂單內容',getRestaurantCart(restaruantId) );
  
  const order =cartStore.checkoutSingleRestaurant(restaruantId)
  console.log('送出的訂單內容', order);
  
  // 寫上ajax
axios.post('/api/orders', order).then((response) => {
    // 請求成功的處理邏輯
    console.log('訂單已成功送出', response.data);
    Swal.fire({
      icon: 'success',
      title: '訂單已送出！',
      text: `您的訂單已成功送出。`,
      confirmButtonText: '確定',
      customClass: {
        confirmButton: 'my-swal-confirm-button'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        console.log(`用戶確認了訂單送出，餐廳ID為: ${restaruantId}`);
      }
    });
  }).catch((error) => {
    // 請求失敗的處理邏輯
    console.error('訂單送出失敗', error);
  })








  console.log('ajax使用',order)
 
  Swal.fire({
    icon: 'success', // 成功圖示
    title: '訂單已送出！', // 標題
    text: `您的訂單已成功送出。`, // 內容文字，可包含餐廳ID
    confirmButtonText: '確定', // 確認按鈕的文字
    customClass: {
      confirmButton: 'my-swal-confirm-button' // 可以為按鈕添加自定義 CSS 類別
    }
  }).then((result) => {
    // 如果需要，可以在用戶點擊「確定」按鈕後執行其他邏輯
    if (result.isConfirmed) {
      console.log(`用戶確認了訂單送出，餐廳ID為: ${restaruantId}`);
      // 例如：導航到訂單歷史頁面，或者關閉模態框等
      // router.push('/orders');
    }
  });
};


// ted
const hideCheckOrderModal = () => {
  isCheckOrderVisible.value = false;
  currentCheckoutItems.value = []; // 清空數據
  restId.value ={};
};



// 控制漢堡選單 (保持不變)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

// 餐廳/餐點切換 (保持不變)
// const toggleRestaurantMenu = () => {
//   isRestaurant.value = !isRestaurant.value;
//   console.log("目前頁面餐廳為是/餐點為否:" + isRestaurant.value);
// };

// 優惠通知邏輯 (保持不變)
const isNotificationOpen = ref(false)
const toggleNotification = () => {
  isNotificationOpen.value = !isNotificationOpen.value;
  if (isNotificationOpen.value) {
    loadNotifications(); // 當打開通知列表時，重新載入通知以獲取最新狀態
  }
}

const notifications = ref([])

// 計算未讀通知數量
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length) // 注意這裡的屬性名是 isRead，不是 is_read

// 處理子組件發出的標記為已讀事件
const handleMarkAsRead = async (notificationItem) => {
  if (!notificationItem.isRead) {
    try {
      // 假設你的 API 端點是 /notifications/{id}/read
      await axios.patch(`/notifications/${notificationItem.id}/read`);
      // 成功後，更新前端的 notifications 陣列
      const index = notifications.value.findIndex(n => n.id === notificationItem.id);
      if (index !== -1) {
        notifications.value[index].isRead = true;
      }
      // 因為 unreadCount 是 computed 屬性，它會自動更新
    } catch (error) {
      console.error('標記通知為已讀失敗', error);
    }
  }
};

// 標記所有通知為已讀
const markAllNotificationsAsRead = async () => {
  try {
    // 假設你的 API 端點是 /notifications/mark-all-as-read
    await axios.post('/notifications/mark-all-as-read', { userId: userId.value }); // 傳遞用戶ID
    // 成功後，更新前端的 notifications 陣列，將所有通知標記為已讀
    notifications.value.forEach(n => { n.isRead = true; });
    // unreadCount 會自動更新為 0
  } catch (error) {
    console.error('標記所有通知為已讀失敗', error);
  }
};

// 載入通知
const loadNotifications = async () => {
  // 確保 userId 有值才發送請求
  if (!userId.value) {
    console.warn('UserId is not available for loading notifications.');
    return;
  }
  try {
    console.log(`取得通知 API：/notifications/user/${userId.value}`);
    // 假設你的 API 端點是 /notifications/user/{userId}
    const res = await axios.get(`/notifications/user/${userId.value}`);
    console.log('後端通知資料', res.data);
    // 假設後端返回的數據結構是 { id, title, createdTime, promotion, isRead }
    notifications.value = res.data.map(n => ({
      ...n,
      isRead: n.isRead // 確保屬性名是 isRead
    }));
  } catch (error) {
    console.error('載入通知失敗', error);
    notifications.value = []; // 載入失敗時清空通知
  }
};

onMounted(() => {
  if (userId.value) {
    loadNotifications()
  }
})
watch(userId, (newVal) => {
  if (newVal) {
    loadNotifications()
  }
})

// 格式化日期時間
const formatDate = (isoString) => {
  if (!isoString) return ''
  const date = new Date(isoString)
  return date.toLocaleDateString('zh-TW') + ' ' + date.toLocaleTimeString('zh-TW')
}

// 搜尋地址 (使用 locationStore 的方法)
const searchAddress = async () => {
  const success = await locationStore.getCoordinates(); // 調用 store 中的 getCoordinates
  if (success) {
    showPopout.value = false;
    router.push({
      path: '/search',
      query: { address: locationStore.address } // 從 store 獲取地址
    });
  }
};

// 獲取當前位置並導航 (使用 locationStore 的方法)
const getCurrentLocationAndNavigate = async () => {
  const success = await locationStore.getCurrentLocation(); // 調用 store 中的 getCurrentLocation
  if (success) {
    showPopout.value = false;
    router.push({
      path: '/search',
      query: { address: locationStore.address } // 從 store 獲取地址
    });
  }
};

// 點擊外部關閉下拉選單
const handleClickOutside = (event) => {
    // 檢查點擊是否在用戶下拉選單或通知列表的元素之外
    const userDropdownContainer = document.querySelector('.user-dropdown-container');
    const notificationButton = document.querySelector('.nav-item .btn'); // 通知鈴鐺按鈕
    const notificationListElement = document.querySelector('.notification-list'); // 通知列表本身

    const isClickInsideUserDropdown = userDropdownContainer && userDropdownContainer.contains(event.target);
    const isClickInsideNotificationButton = notificationButton && notificationButton.contains(event.target);
    const isClickInsideNotificationList = notificationListElement && notificationListElement.contains(event.target);

    if (!isClickInsideUserDropdown) {
        showDropdown.value = false;
    }
    // 只有當點擊不在通知按鈕或通知列表內部時才關閉通知列表
    if (!isClickInsideNotificationButton && !isClickInsideNotificationList);
};

// --- Lifecycle Hooks ---
onMounted(() => {
    document.addEventListener('click', handleClickOutside);
    userId.value = userStore.userId; // 假設您的 Pinia store 中有 userId 屬性
    // 這裡不再需要特別從路由設定地址，因為 locationStore 在初始化時會從 localStorage 讀取
    // 只有當路由的 address 參數存在且與 store 中的地址不同時，才更新 store
    if (route.query.address && route.query.address !== locationStore.address) {
        locationStore.setAddress(route.query.address);
    }
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});

// 監聽路由變化，並同步到 locationStore
// 只有當路由參數提供了一個非空的新地址時才更新 store
// 這樣可以避免在導航到不帶地址參數的頁面時，清除 store 中已有的地址
watch(() => route.query.address, (newAddress) => {
    if (newAddress && newAddress !== locationStore.address) {
        locationStore.setAddress(newAddress);
    }
});



// 模擬登入函數 (保持不變)
const getLogin = () => {
  isLoggedIn.value = true; // 模擬登入
  // 模擬登入成功後，重新載入通知
  userId.value = userStore.userId; // 確保獲取到用戶ID
  loadNotifications();
};
</script>

<style scoped>
/* 您的 CSS 樣式保持不變 */
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
  z-index: 1000;
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

.notification-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1000; /* 要低於彈窗（1050） */
  background: transparent;
}

.badge {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 12px;
}
</style>