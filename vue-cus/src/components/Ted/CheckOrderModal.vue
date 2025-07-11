<!-- components/OrderSummary.vue -->
<template>
  <div class="card shadow-lg p-4 rounded-4" style="max-width: 500px; width: 100%;" ref="orderSummaryCard">
    <div class="card-body">
      <h4 class="card-title text-center mb-4">您的訂單</h4>

     
      <!-- 餐廳營業時間提醒 -->
      <div class="alert alert-info d-flex align-items-center gap-2 mb-4" role="alert">
        <i class="bi bi-clock"></i>
        <div>
          本店今日營業時間：<strong>今日營業時間為...</strong>
        </div>
      </div>
      <!-- 預計取餐時間 -->
      <div class="mb-4">
        <label for="pickupTime" class="form-label fw-bold">預計取餐時間</label>
        <div class="input-group ">
          <!-- <input type="date" class="form-control rounded-start-pill text-center" :value="currentDate" readonly /> -->
            <button
              class="btn btn-outline-secondary "
              type="button"
              @click="adjustTime(-5)">－</button>

            <input
              type="time"
              class="form-control text-center"
              v-model="currentTime"/>

            <button
              class="btn  btn-outline-secondary"
              type="button"
              @click="adjustTime(5)">＋</button>
        </div>
      </div>

      <hr class="my-4">

      <!-- 您的訂單項目 -->
      <h5 class="mb-3">您的訂單</h5>
      <div v-for="item in orderItems" :key="item.id" class="d-flex align-items-center justify-content-between mb-3 py-2 border-bottom">
        <div>
          <div class="fw-bold">{{ item.name }}</div>
          <!-- 規格選項 -->
          <small class="text-muted d-block" style="font-size: 0.85rem;">
            {{ item.spec || '無選項' }}
          </small>
          <small class="text-muted">NT$ {{ item.price }}</small>
        </div>
        <div class="d-flex align-items-center gap-2">
          <!-- 數量控制，這裡可以替換成您之前的 QuantityControl 組件 -->
          <div class="d-flex align-items-center border   overflow-hidden flex-shrink-0 rounded-2">
            <button
              class="btn btn-sm px-2"
              @click="decreaseQuantity(item.id)"
              :disabled="item.quantity === 1"
              :class="{ 'btn-secondary text-white': item.quantity === 1, 'btn-light text-dark': item.quantity !== 1 }"
            >
              －
            </button>
            <div class="text-center" style="width: 35px;">{{ item.quantity }}</div>
            <button class="btn btn-sm px-2 btn-light text-dark" @click="increaseQuantity(item.id)">
              ＋
            </button>
          </div>
          <div class="fw-bold text-end" style="width: 80px;">NT$ {{ item.quantity * item.price }}</div>
          <button class="btn btn-sm btn-outline-danger rounded-circle p-0" style="width: 28px; height: 28px; font-size: 0.8rem;" @click="removeItem(item.id)">
            <i class="bi bi-trash"></i>
          </button>
        </div>
      </div>
    

      <!-- 小計 -->
      <div class="d-flex justify-content-between align-items-center mt-4">
        <span class="fw-bold">小計</span>
        <span class="fw-bold">NT$ {{ subtotal }}</span>
      </div>

      <!-- 總付款金額 -->
      <div class="d-flex justify-content-between align-items-center mt-3 py-3 border-top border-bottom">
        <h5 class="mb-0">總付款金額</h5>
        <h5 class="mb-0 text-primary">NT$ {{ totalPayment }}</h5>
      </div>

      <!-- 底部提示 -->
      <p class="text-muted text-center mt-4 small">
        您將在以下訂單選擇二、六月 24 日, 預約 12:30 取餐的外帶訂單。
        <br>取餐地址為...
      </p>
         <!-- 備註 -->
      <div class="mb-3">
        <label for="exampleFormControlTextarea1" class="form-label">備註</label>
        <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
      </div>
      <!-- 支付方式 -->
      <div class="d-flex justify-content-between align-items-center mt-3">
        <label class="fw-bold">付款方式：</label>
        <label>
          <input type="radio" name="payment" value="cash" v-model="paymentMethod" /> 現金支付
        </label>
        <label class="ms-3">
          <input type="radio" name="payment" value="credit" v-model="paymentMethod" /> 信用卡支付
        </label>

        
      </div>
      <!-- 底部按鈕 - 將原來的「繼續點餐」和「前往結帳」移除，因為 App.vue 會控制主要的購物車按鈕 -->
      <!-- <div class="d-flex gap-3 mt-4">
        <button class="btn btn-outline-primary py-2 fw-bold flex-grow-1 rounded-pill">繼續點餐</button>
        <button class="btn btn-warning py-2 fw-bold flex-grow-1 rounded-pill">前往結帳</button>
      </div> -->

      <!-- 新增一個「加入購物車」按鈕 -->
      <button class="btn btn-primary py-2 fw-bold w-100 mt-4 " @click="emitAddToCart">
        確定結帳 NT$ {{ totalPayment }}
      </button>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'; // 引入 defineExpose

// 定義發射的事件
const emits = defineEmits(['add-to-cart']);

// 引用 OrderSummary 的根元素
const orderSummaryCard = ref(null);
const currentTime = ref(getNowTime())
const paymentMethod = ref('cash') 

//取餐時間設定
function getNowTime() {
  const now = new Date()
  return now.toTimeString().slice(0, 5)
}

function adjustTime(minutes) {
  const [hour, minute] = currentTime.value.split(':').map(Number)
  const time = new Date()
  time.setHours(hour)
  time.setMinutes(minute + minutes)
  currentTime.value = time.toTimeString().slice(0, 5)
}





// 訂單項目數據 (響應式)
const orderItems = ref([
  { id: 1, name: '鹹蛋黃金辣味雞腿沙拉', price: 296, quantity: 1 },
  { id: 2, name: '陽光汁鮮蝦豬肉麵', price: 344, quantity: 2 },
  // 可以添加更多商品
]);

// 預計取餐時間 (模擬)
const currentDate = ref(new Date().toISOString().slice(0, 10)); // 今天的日期


// 計算小計
const subtotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0);
});

// 計算總付款金額 (目前與小計相同，如果未來有運費或折扣，可以在此調整)
const totalPayment = computed(() => {
  return subtotal.value; // 這裡可以加上運費、折扣等
});

// 增減數量的方法
const increaseQuantity = (id) => {
  const item = orderItems.value.find(i => i.id === id);
  if (item) {
    item.quantity++;
  }
};

const decreaseQuantity = (id) => {
  const item = orderItems.value.find(i => i.id === id);
  if (item && item.quantity > 1) {
    item.quantity--;
  }
};

// 移除訂單項目
const removeItem = (id) => {
  orderItems.value = orderItems.value.filter(item => item.id !== id);
};

// 發射加入購物車事件
const emitAddToCart = () => {
  if (orderItems.value.length > 0) {
    emits('add-to-cart', orderItems.value); // 將目前訂單中的所有商品發射出去
    // 清空當前訂單項目，模擬加入購物車後清空頁面上的選中商品
    orderItems.value = [];
  } else {
    alert('請先選擇餐點再加入購物車！');
  }
};

// 捲動到組件頂部的方法
const scrollToTop = () => {
  if (orderSummaryCard.value) {
    orderSummaryCard.value.scrollIntoView({ behavior: 'smooth' });
  }
};

// 將 scrollToTop 方法暴露給父組件
defineExpose({
  scrollToTop
});
</script>

<style scoped>
/* 這裡可以放置 OrderSummary 組件特有的樣式 */
.card {
  max-width: 500px;
}

/* 確保按鈕在禁用時顏色和鼠標樣式 */
.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

/* 針對內層數量控制按鈕的樣式微調 */




/* 確保輸入框圓角與按鈕組一致 */
.input-group .form-control:first-child {
    border-top-left-radius: var(--bs-border-radius-pill) !important;
    border-bottom-left-radius: var(--bs-border-radius-pill) !important;
}
.input-group .form-control:last-child {
    border-top-right-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
}
</style>