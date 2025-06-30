<script setup>
import { ref, computed, onMounted } from 'vue';
import OrderList from './OrderList.vue';
import OrderDetail from './OrderDetail.vue';

// 订单列表数据 (模拟数据)
const orders = ref([]);
const selectedOrder = ref(null);
const isDetailSidebarVisible = ref(false);

// 模拟从后端获取数据
const fetchOrders = () => {
  orders.value = [
    {
      id: '22905',
      customerName: '柏均 (麵室) 林',
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:36 PM',
      items: [
        { name: '豬肉腸陽光堡', quantity: 1, price: 84.00 },
        { name: '奶茶', quantity: 1, price: 49.00, note: '備註: 加冰塊' },
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:30 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:25 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:15 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:13 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:12 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:11 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:11 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:11 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:11 PM',
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
      shop: '呼尚買食品店',
      date: '6/25/2025',
      time: '12:11 PM',
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

// 计算属性：订单总数
const totalOrders = computed(() => orders.value.length);

// 處理選中訂單
const handleOrderSelected = (order) => {
  selectedOrder.value = order;
  isDetailSidebarVisible.value = true; // 顯示側邊欄
};

// 關閉訂單詳情側邊欄
const closeDetailSidebar = () => {
  isDetailSidebarVisible.value = false;
  selectedOrder.value = null;
};

// 模擬"重設"功能 (重置篩選/排序)
const resetFilters = () => {
  // 在這裡實現重置邏輯，例如重新載入原始訂單數據
  alert('重設功能待實現');
};
</script>

<template>
  <div class="container-fluid bg-light min-vh-100 py-3">
    <div class="row h-100">
      <!-- 左侧订单列表区域 -->
      <div class="col-12 col-xl-8 pe-xl-0 d-flex flex-column">
        <!-- 移除了卡片结构，内容直接显示 -->
        <div class="mb-4 px-4 pt-4"> <!-- 添加一些间距，使其不完全靠边 -->
          <div class="d-flex align-items-center justify-content-between mb-3">
            <h5 class="mb-0 text-muted">找到 {{ totalOrders }} 項結果</h5>
            <button class="btn btn-outline-secondary btn-sm rounded-pill px-3" @click="resetFilters">重設</button>
          </div>
        </div>
        <!-- 确保 OrderList 内部内容能充满可用高度并可滚动 -->
        <div class="flex-grow-1 overflow-auto bg-white shadow-sm rounded-4 mx-3 mb-3">
          <OrderList :orders="orders" @select-order="handleOrderSelected" />
        </div>
      </div>

      <!-- 右侧订单详情侧边栏 -->
      <div
        class="col-12 col-xl-4 bg-white shadow-lg border-start position-fixed end-0 h-100 detail-sidebar "
        :class="{ 'show': isDetailSidebarVisible }"
      >
        <OrderDetail :order="selectedOrder" @close-detail="closeDetailSidebar" />
      </div>

      <!-- 侧边栏叠加层 (点击空白处关闭) -->
      <div
        class="overlay"
        :class="{ 'show': isDetailSidebarVisible }"
        @click="closeDetailSidebar"
      ></div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

/* 订单详情侧边栏样式 */
.detail-sidebar {
  width: 100%;
  max-width: 500px; /* 控制侧边栏最大宽度 */
  transform: translateX(100%);
  transition: transform 0.3s ease-in-out;
  z-index: 1050; /* 比 overlay 高 */
  top: 0; /* 确保从顶部开始 */
  height: 100vh !important; /* 确保侧边栏充满整个视口高度 */
  overflow-y: auto; /* 允许侧边栏内容滚动 */
}

.detail-sidebar.show {
  transform: translateX(0);
}

/* 叠加层样式 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1040; /* 在侧边栏之下 */
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease-in-out, visibility 0.3s ease-in-out;
}

.overlay.show {
  opacity: 1;
  visibility: visible;
}

/* 移动端适配 */
@media (max-width: 1199.98px) { /* xl breakpoint */
  .detail-sidebar {
    position: fixed; /* 在移动端使用 fixed */
    max-width: 100%; /* 移动端全宽 */
  }
}
</style>
