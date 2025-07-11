<!-- 歷史訂單點進去後的詳細 -->

<script setup>
import { ref, computed } from 'vue';

// 引入 Bootstrap 5 CSS
import 'bootstrap/dist/css/bootstrap.min.css';
// 引入 Bootstrap 5 JS (可選，這裡主要用於佈局，JS 功能如彈窗等可能需要完整引入)
// import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// 訂單數據
const order = ref({
  restaurant: {
    name: '摩斯漢堡 Mos Burger (台北天母店)',
    address: '外送於 6月21日 週六 上午11:26',
    orderId: 'Order #hzvv-2525-cbf',
    
  },
  items: [
    { name: '1x 捌組大麥海洋珍珠堡附餐', price: 230, description: '冰拿鐵咖啡 [L] + 摩斯雞塊【5塊】(附無糖醬包-1包) 1. 雞塊', imageUrl: 'https://placehold.co/50x50/F8D800/FFFFFF?text=漢堡' },
  ],
  subtotal: 230,
  deliveryFee: 29, // 假設外送服務費
  platformFee: 4, // 假設平台費
  payment: {
    creditCard: 168,
    pandapay: 66,
  },
  rated: true, // 假設已評分
});

// 計算總計
const total = computed(() => {
  return order.value.subtotal + order.value.deliveryFee + order.value.platformFee;
});

// 重新訂購按鈕點擊事件
const reorder = () => {
  alert('您點擊了「重新訂購最新訂購項目」！');
  // 這裡可以加入重新訂購的邏輯
};

// 需要協助按鈕點擊事件
const needHelp = () => {
  alert('您點擊了「與我們協助」！');
  // 這裡可以加入尋求協助的邏輯
};
</script>

<template>
  <div class="container-fluid py-4" style="background-color: #f5f5f5; min-height: 100vh;">
    <!-- 導航欄 - 簡化，僅為示意 -->
  

    <div class="container">
      <div class="row">
        <!-- 左側內容區 -->
         <h2 class="mb-4 fw-bold">訂單詳情</h2>
        <div class="col-lg-8">
          

          <div class="card shadow-sm mb-4 rounded-lg">
            <div class="card-body">
              <h5 class="card-title fw-bold">訂單記錄 & 最新訂購</h5>
              <p class="text-muted small">
                <a href="#" class="text-decoration-none" style="color: #e20261;">訂單記錄</a> &gt; <a href="#" class="text-decoration-none" style="color: #e20261;">訂單詳情</a>
              </p>

              <div class="d-flex align-items-center mb-3">
                <div class="me-3">
                  <img src="https://placehold.co/70x70/FFC0CB/FFFFFF?text=店鋪" alt="餐廳圖標" class="rounded-circle border" style="width: 70px; height: 70px; object-fit: cover;">
                </div>
                <div>
                  <h5 class="mb-0 fw-bold">{{ order.restaurant.name }}</h5>
                  <p class="mb-0 text-muted small">外送於 {{ order.restaurant.address }}</p>
                  <p class="mb-0 text-muted small">{{ order.restaurant.orderId }}</p>
                </div>
              </div>

              <div class="mb-3">
                <div class="d-flex align-items-center mb-2">
                  <i class="bi bi-geo-alt-fill me-2" style="color: #e20261;"></i>
                  <span class="fw-bold">訂購於</span>
                </div>
                <p class="ms-4 mb-0 text-muted">{{ order.restaurant.name }}</p>
              </div>
            </div>
          </div>

          <div class="card shadow-sm mb-4 rounded-lg">
            <div class="card-body">
              <h5 class="card-title fw-bold">訂單</h5>
              <div v-for="(item, index) in order.items" :key="index" class="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div class="me-3">
                  <img :src="item.imageUrl" :alt="item.name" class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">
                </div>
                <div class="flex-grow-1">
                  <p class="mb-0 fw-bold">{{ item.name }}</p>
                  <p class="mb-0 text-muted small">{{ item.description }}</p>
                </div>
                <span class="fw-bold text-end">NT$ {{ item.price.toFixed(0) }}</span>
              </div>

              <div class="d-flex justify-content-between mb-2">
                <span>小計</span>
                <span class="fw-bold">NT$ {{ order.subtotal.toFixed(0) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>外送服務費</span>
                <span class="fw-bold">
                  NT$ {{ order.deliveryFee.toFixed(0) }}
                  <span class="badge bg-secondary ms-2" style="background-color: #f0f0f0 !important; color: #6c757d;">PRO</span>
                </span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>平台費</span>
                <span class="fw-bold">NT$ {{ order.platformFee.toFixed(0) }}</span>
              </div>
              <hr>
              <div class="d-flex justify-content-between mb-3">
                <h5 class="fw-bold mb-0">總計 (含稅)</h5>
                <h5 class="fw-bold mb-0">NT$ {{ total.toFixed(0) }}</h5>
              </div>
            </div>
          </div>

          <div class="card shadow-sm mb-4 rounded-lg">
            <div class="card-body">
              <h5 class="card-title fw-bold">付款方式</h5>
              <div class="d-flex justify-content-between mb-2">
                <span>信用卡</span>
                <span>NT$ {{ order.payment.creditCard.toFixed(0) }}</span>
              </div>
              <div class="d-flex justify-content-between">
                <span><img src="https://upload.wikimedia.org/wikipedia/commons/e/e0/PayPay_logo.svg" alt="pandapay" style="height: 18px;" class="me-1"> pandapay 餘額</span>
                <span>NT$ {{ order.payment.pandapay.toFixed(0) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右側內容區 -->
        <div class="col-lg-4">
          <div class="card shadow-sm mb-4 rounded-lg">
            <div class="card-body">
              <h5 class="card-title fw-bold">再次訂購</h5>
              <button @click="reorder" class="btn btn-block btn-lg mt-3 w-100 fw-bold rounded" style="background-color: #e20261; color: white;">
                重新訂購最新訂購項目
              </button>
            </div>
          </div>

          <div v-if="order.rated" class="card shadow-sm mb-4 rounded-lg">
            <div class="card-body">
              <p class="mb-0">你已評分 ⭐⭐⭐⭐⭐ import review_Modal</p>
            </div>
          </div>


        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 可以在這裡添加組件特有的樣式 */
.rounded-lg {
  border-radius: 0.75rem !important; /* 增加圓角 */
}

/* 確保按鈕在小螢幕下保持全寬 */
@media (max-width: 991.98px) {
  .btn-lg {
    font-size: 1rem; /* 調整按鈕字體大小 */
    padding: 0.75rem 1.25rem; /* 調整按鈕內邊距 */
  }
}
</style>

