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
        <a href="#" @click="openRegisterModal" v-if="!isLoggedIn">登入</a>
        <UserDropdown v-if="isLoggedIn" />
      </div>
      <div class="nav-items">
        <a v-if="route.path === '/search'" href="#" @click.prevent="restaurantDisplayStore.toggleDisplayMode()"
          :title="restaurantDisplayStore.showAllRestaurants ? '顯示已收藏' : '顯示全部'"
          class="nav-item d-flex align-items-center gap-2">
          <i :class="restaurantDisplayStore.showAllRestaurants ? 'fas fa-heart' : 'fas fa-store'"></i>
          <span>{{ restaurantDisplayStore.showAllRestaurants ? '已收藏' : '全部' }}</span>
        </a>

        <!-- 優惠通知鈴鐺 -->
        <div class="nav-item" style="position: relative;">
          <button class="btn position-relative" style="background: transparent; border: none;"
            @click.stop="toggleNotification" title="優惠通知">
            <i class="bi bi-bell-fill text-white"></i>
            <span v-if="unreadCount > 0"
              class="badge bg-danger text-white position-absolute top-0 start-100 translate-middle rounded-pill">
              {{ unreadCount }}
            </span>
          </button>
          <NotificationList :visible="isNotificationOpen" :notifications="notifications" @mark-as-read="markAsRead" />
        </div>

        <div class="nav-item">
          <button class="btn position-relative" style="background: transparent; border: none;" @click="showCart"
            title="購物車">
            <i class="bi bi-cart4 text-white"></i>
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
  <CheckOrderModal v-if="isCheckOrderVisible"  :orderItems="currentCheckoutItems" :restId="Number(restId)" 
    @add-to-cart="handleConfirmCheckout" @close="hideCheckOrderModal" />

  <section class="popout" v-if="showPopout">
    <div class="popout-content">
      <button class="close-btn" @click="showPopout = false">✕</button>
      <input type="text" placeholder="輸入您的地址" @focus="locationStore.setAddress('')" v-model="locationStore.address" />
      <button class="search-btn"
        @click="locationStore.address.trim() ? searchAddress() : getCurrentLocationAndNavigate()">搜尋</button>
      <button class="search-btn"
        @click="locationStore.address.trim() ? searchAddress() : getCurrentLocationAndNavigate()">搜尋</button>
    </div>
  </section>

  <AuthModals ref="authModalsRef" />
</template>

<script setup>
import { ref, onMounted, watch, computed, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UserDropdown from '@/components/Jimmy/UserDropdown.vue';
import NotificationList from '@/components/Yifan/NotificationList.vue';
import CheckOrderModal from '@/components/Ted/CheckOrderModal.vue'; // 引入 CheckOrderModal ted
import AuthModals from '@/components/Ivy/AuthModals.vue'//登入的modal
import CartModal from '@/components/KTlu/CartModal.vue';
import { useCartStore } from '@/stores/cart';
import { useLocationStore } from '@/stores/location'; // <-- 導入新的 location store
import { useRestaurantDisplayStore } from '@/stores/restaurantDisplay';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/user.js';
import axios from '@/plungins/axios.js';

// 位置 store
const locationStore = useLocationStore(); // <-- 實例化 location store
const restaurantDisplayStore = useRestaurantDisplayStore();
//登入用的▼
const userStore = useUserStore(); // 實例化 userStore
const authModalsRef = ref(null); // 引用 AuthModals
const userId = ref(null); // 用於存儲從 Pinia 獲取的用戶 ID
const isLoggedIn = computed(() => userStore.isLogin);
// const isLoggedIn = ref(true); // 根據實際登入狀態設定

const isMenuOpen = ref(false);
const showPopout = ref(false);
const route = useRoute();
const router = useRouter();

// 購物車 store
const cartStore = useCartStore();
// 購物車相關的計算屬性和方法 (保持不變)
const cartCount = computed(() => cartStore.cartCount);
const cartByRestaurant = computed(() => cartStore.cartByRestaurant);
const totalAmount = computed(() => cartStore.totalAmount);
const isCartVisible = computed(() => cartStore.isCartVisible);


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
const restId = ref(1);
// 儲存準備結帳的訂單
const getCheckOrder = () => restId;





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



const handleConfirmCheckout = (restaruantId, orderData) => {
  // 結帳送出訂單
if (!isLoggedIn.value) { // 使用 isLoggedIn Computed 屬性判斷登入狀態
    Swal.fire({
      icon: 'warning',
      title: '您尚未登入',
      text: '是否要登入以完成訂單？',
      showCancelButton: true,
      confirmButtonText: '登入',
      cancelButtonText: '取消',
      customClass: {
        confirmButton: 'my-swal-confirm-button',
        cancelButton: 'my-swal-cancel-button' // 可以為取消按鈕添加自定義 CSS 類別
      }
    }).then((result) => {
      if (result.isConfirmed) {
        // 如果用戶點擊「登入」，則開啟登入畫面
        openRegisterModal(); // 呼叫已有的開啟登入模態框的函式
      }
      // 如果用戶點擊「取消」或關閉彈窗，則不執行後續結帳邏輯
    });
    return; // 未登入且未選擇登入，終止結帳流程
  }

  //如果沒辦法取得userId.value暫時給值 4
  const body = {
    user: {
      id: userId.value // 假設您的 Pinia store 中有 userId 屬性

    }
  }
  // 將 body 的屬性複製到 existingObject (修改 existingObject)
  // Object.assign(target, source1, source2, ...);
  Object.assign(getRestaurantCart(restaruantId), orderData, body);
  isCheckOrderVisible.value = false;

  const order = cartStore.checkoutSingleRestaurant(restaruantId)


  // 寫上ajax
  axios.post('/api/orders', order).then((response) => {
    // 請求成功的處理邏輯
    console.log('訂單已成功送出', response.data);
    const newOrder = response.data;
    //新增刷卡付款 
    if (orderData.method === 'credit') {
      // 👇 直接呼叫後端綠界 API（假設你後端是在 8080 port）
      console.log(JSON.stringify({
        orderId: newOrder.id,
        description: newOrder.content,
        amount: newOrder.total
      }));
      
      const foodList = order.orderDetails
      const foodNameList =foodList.map(foodList => 
      `${foodList.food.name} ${foodList.quantity} x ${foodList.price}元`).join('#');
      
     
     
      // -----------------------------------------
      // ------------------------------
      fetch('http://localhost:8080/api/payment/create', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    orderId: newOrder.id,
    description: newOrder.content,
    amount: newOrder.total,
    foodNameList:foodNameList,
    createTime: newOrder.createTime,
  })
})
.then(res => res.text())
.then(data => {
  console.log('取得的', data);


  const isEcpayForm = (
    typeof data === 'string' &&
    data.trim().startsWith('<form') &&
    data.includes('payment-stage.ecpay.com.tw')
  );

  if (isEcpayForm) {
    // ➜ 只在這裡 submit
    const wrapper = document.createElement('div');
    wrapper.innerHTML = data;
    document.body.appendChild(wrapper);
    const form = wrapper.querySelector('form');
    if (form) {
      form.setAttribute('target', '_blank');
      form.submit();
    }
    // internalOrderItems.value = [];
    // bsModal.hide();
  } else {
    // 不是綠界表單就只彈 Swal，return 阻止往下執行
    Swal.fire({
      icon: 'error',
      title: '付款失敗',
      text: typeof data === 'string' ? data : ((data && data.message) || '未知錯誤，請聯繫客服')
    });
    return; // <<==== 這裡很重要！加這行！
  }


})
.catch(err => {
  // ➜ fetch 自己的錯誤（斷線、連不到後端）
  console.log('金流錯誤:', err);
  Swal.fire({
    icon: 'error',
    title: '付款流程異常',
    text: '無法連線金流服務，請稍後再試'
  });
});




      // -----------------------------------------------------------------------
    }

  }).catch((error) => {
    // 請求失敗的處理邏輯
    console.error('訂單送出失敗:', error);
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
  restId.value = {};
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
}

const notifications = ref([
  { id: 1, title: '🎁 全站85折限時優惠', date: '2025-06-30', is_read: false },
  { id: 2, title: '🍔 餐點類優惠券即將到期', date: '2025-06-29', is_read: false },
  { id: 3, title: '🎉 註冊送折扣券', date: '2025-06-28', is_read: true }
])

const unreadCount = computed(() => notifications.value.filter(n => !n.is_read).length)
const markAsRead = (item) => { item.is_read = true }

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
  if (!event.target.closest('.user-dropdown-container') && !event.target.closest('.notification-list')) {
    showDropdown.value = false;
    isNotificationOpen.value = false; // 同時關閉通知列表
  }
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
const openRegisterModal = () => {
  showDropdown.value = false // 關閉下拉選單
  if (authModalsRef.value) {
    authModalsRef.value.step = 'register' // 直接修改 AuthModals 內部的 step
  }
}
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
</style>