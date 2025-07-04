<script setup>
import { ref, computed, onMounted } from 'vue';
import PageHeader from '../components/common/PageHeader.vue'; //導入頁面標頭公版
import SlideOutPanel from '../components/common/SlideOutPanel.vue'; //導入側邊欄公版
import OrderList from '../components/order/OrderList.vue';
import OrderDetail from '../components/order/OrderDetail.vue';

// 訂單列表資料 (模擬資料)
const orders = ref([]);
const selectedOrder = ref(null);
const isDetailSidebarVisible = ref(false); // 控制側邊欄可見性

// 模擬從後端獲得資料
const fetchOrders = () => {
  orders.value = [
    {
      id: '22905',
      customerName: '柏均 (麵室) 林',
      method: '現金',
      date: '6/25/2025',
      time: '12:36 PM',
      status:'待確認',
      items: [
        { name: '豬肉腸陽光堡', quantity: 1, price: 84.00 },
        { name: '奶茶', quantity: 1, price: 49.00, note: '加冰塊' },
      ],
      timeline: [
        { time: '12:05 PM', description: '顧客訂購' },
        { time: '12:06 PM', description: '商店接受了訂單' },
        { time: '12:06 PM', description: '銷售整合系統已接受訂單' },
        { time: '12:21 PM', description: '外送員出發' },
        { time: '12:36 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '063D4',
      customerName: 'Wayne L',
      method: '現金',
      date: '6/25/2025',
      time: '12:30 PM',
      status:'待確認',
      items: [
        { name: '法式吐司', quantity: 2, price: 55.00 },
        { name: '美式咖啡', quantity: 1, price: 40.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:30 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '9FCA5',
      customerName: 'Yisen C',
      method: '現金',
      date: '6/25/2025',
      time: '12:25 PM',
      status:'待確認',
      items: [
        { name: '招牌三明治', quantity: 1, price: 70.00 },
        { name: '柳橙汁', quantity: 1, price: 35.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:25 PM', 'description': '訂單已送達顧客手中' },
      ],
    },
    {
      id: '7457C',
      customerName: '紅婷 黃',
      method: '現金',
      date: '6/25/2025',
      time: '12:15 PM',
      status:'待確認',
      items: [
        { name: '炸雞塊', quantity: 1, price: 60.00 },
        { name: '可樂', quantity: 1, price: 25.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:15 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '58B0F',
      customerName: '楊 關',
      method: '現金',
      date: '6/25/2025',
      time: '12:13 PM',
      status:'待確認',
      items: [
        { name: '牛肉麵', quantity: 1, price: 120.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:13 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: 'A8477',
      customerName: '吳 昱',
      method: '現金',
      date: '6/25/2025',
      time: '12:12 PM',
      status:'待確認',
      items: [
        { name: '滷肉飯', quantity: 1, price: 80.00 },
        { name: '貢丸湯', quantity: 1, price: 30.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:12 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '4AE7C',
      customerName: '樂威 康',
      method: '現金',
      date: '6/25/2025',
      time: '12:11 PM',
      status:'待確認',
      items: [
        { name: '鍋貼', quantity: 10, price: 7.00 },
        { name: '酸辣湯', quantity: 1, price: 45.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:11 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '4AE78',
      customerName: '樂威 康8',
      method: '現金',
      date: '6/25/2025',
      time: '12:11 PM',
      status:'待確認',
      items: [
        { name: '鍋貼', quantity: 10, price: 7.00 },
        { name: '酸辣湯', quantity: 1, price: 45.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:11 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '4AE79',
      customerName: '樂威 康9',
      method: '現金',
      date: '6/25/2025',
      time: '12:11 PM',
      status:'待確認',
      items: [
        { name: '鍋貼', quantity: 10, price: 7.00 },
        { name: '酸辣湯', quantity: 1, price: 45.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:11 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '4AE80',
      customerName: '樂威 康80',
      method: '現金',
      date: '6/25/2025',
      time: '12:11 PM',
      status:'待確認',
      items: [
        { name: '鍋貼', quantity: 10, price: 7.00 },
        { name: '酸辣湯', quantity: 1, price: 45.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:11 PM', description: '訂單已送達顧客手中' },
      ],
    },
    {
      id: '4AE81',
      customerName: '樂威 康81',
      method: '現金',
      date: '6/25/2025',
      time: '12:11 PM',
      status:'待確認',
      items: [
        { name: '鍋貼', quantity: 10, price: 7.00 },
        { name: '酸辣湯', quantity: 1, price: 45.00 },
      ],
      timeline: [
        { time: '12:00 PM', description: '顧客訂購' },
        { time: '12:05 PM', description: '商店接受了訂單' },
        { time: '12:11 PM', description: '訂單已送達顧客手中' },
      ],
    },
  ];
};

onMounted(() => {
  fetchOrders();
});

// 計算屬性：訂單總數
const totalOrders = computed(() => orders.value.length);

// 處理選中訂單
const handleOrderSelected = (order) => {
  selectedOrder.value = order;
  isDetailSidebarVisible.value = true; // 顯示側邊欄
};

// 關閉訂單詳情側邊欄
const closeDetailSidebar = () => {
  isDetailSidebarVisible.value = false;
  // selectedOrder 會在面板關閉後再清空，體驗更好
  setTimeout(() => {
    if (!isDetailSidebarVisible.value) {
        selectedOrder.value = null;
    }
  }, 300); // 300ms 是我們動畫的時間
};

// 模擬"重設"功能 (重置篩選/排序)
const resetFilters = () => {
  // 在這裡實現重置邏輯，例如重新載入原始訂單數據
  alert('重設功能待實現');
};
</script>

<template>
<!-- 移除舊有外層佈局 div，只保留頁面自身需要的結構 -->
  <!-- 為整個頁面組件提供一個根元素，用 d-flex 和 h-100 來撐開佈局 -->
  <div class="d-flex flex-column h-100">
    <PageHeader title="訂單管理">
      <template #right-side>
        <div class="d-flex align-items-center">
          <h5 class="mb-0 text-muted me-3">找到 {{ totalOrders }} 項結果</h5>
          <button class="btn btn-outline-secondary btn-sm rounded-pill px-3" @click="resetFilters">重設</button>
        </div>
      </template>
    </PageHeader>

    <!-- 左側訂單列表 -->
    <div class="order-list-panel">
      <OrderList 
        :orders="orders" 
        @select-order="handleOrderSelected" 
      />
    </div>

    <!-- 3. 使用新的 SlideOutPanel 組件 -->
    <SlideOutPanel 
      v-model:isOpen="isDetailSidebarVisible" 
      title="訂單詳情"
      @close="closeDetailSidebar"
    >
      <!-- 面板的主體內容放在預設插槽中 -->
      <OrderDetail 
        v-if="selectedOrder" 
        :order="selectedOrder" 
      />
      <!--  注意：OrderDetail 內部的關閉按鈕，我們不再需要監聽它的 @close-detail 事件了，
            因為 SlideOutPanel 自己的關閉按鈕和遮罩點擊會處理關閉邏輯。
            當然，保留它也沒問題，可以提供多一種關閉方式。
      -->
    </SlideOutPanel>    
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

/* 頁面根元素，設定 relative 來作為定位父層 */
.order-manager-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative; /* 重點：作為定位父層 */
}

/* 內容區塊 */
.content-wrapper {
  flex-grow: 1; /* 佔滿剩餘的垂直空間 */
  position: relative; /* 加上 relative，以防萬一內部有需要定位的元素 */
  display: flex; /* 為了讓 .order-list-panel 撐開，還是需要 */
}

/* 訂單列表面板 */
.order-list-panel {
  flex-grow: 1;   
  overflow-y: auto; /* 讓列表自己滾動 */
  background-color: white;
  box-shadow: 0 .125rem .25rem rgba(0,0,0,.075);
  border-radius: .25rem;
}

/* 行動端適應 */
@media (max-width: 1199.98px) { /* xl breakpoint */
  .detail-sidebar {
    position: fixed; /* 在行動端使用 fixed */
    max-width: 100%; /* 行動端全寬 */
  }
}
</style>
