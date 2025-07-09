<!-- 歷史訂單 -->
<script setup>
import { ref ,onMounted} from 'vue';
import axios from '@/plungins/axios.js';
import RatingModal from '@/components/Ted/ReviewModal.vue';
// 如果你的 main.js 或其他地方沒有全局引入 Bootstrap CSS 和 Icons，你可以在這裡引入
// import 'bootstrap/dist/css/bootstrap.min.css';
// import 'bootstrap-icons/font/bootstrap-icons.css'; // 如果你使用了 Bootstrap Icons
// import 'bootstrap/dist/js/bootstrap.bundle.min.js'; // 如果需要 Bootstrap JS 功能




const orders=ref([])
// 模擬訂單數據
// orders.value.push(
//   {
//     id: 1,
//     store: '店家名稱1', // 修正 'sotre' 為 'store'
//     img: 'https://www.discoverhongkong.com/content/dam/dhk/intl/explore/dining/hong-kong-restaurants-by-the-sea/hue-960x720.jpg',
//     price: 499,
//     foods: [{
//       name: '綠茶',
//       quantity: 1,
//       spec: '中杯,溫,無糖,六分糖',
//       like: null,
//     }, {
//       name: '紅茶拿鐵',
//       quantity: 2,
//       spec: '中杯,溫,無糖,六分糖',
//       like: null,
//     }, {
//       name: '叉燒飯',
//       quantity: 3,
//       like: null,
//     }],
//     time: '2025-06-24 18:30',
//     rating: 0, // 初始未評分
//     like: null,
//   },
//   {
//     id: 2,
//     img: 'https://www.discoverhongkong.com/content/dam/dhk/intl/explore/dining/hong-kong-restaurants-by-the-sea/hue-960x720.jpg',
//     store: '店家名稱2', // 修正 'sotre' 為 'store'
//     price: 699,
//     foods: [{
//       name: '綠茶',
//       quantity: 1,
//       spec: '中杯,溫,無糖,六分糖',
//       like: null,
//     }, {
//       name: '紅茶拿鐵',
//       quantity: 2,
//       spec: '中杯,溫,無糖,六分糖',
//       like: null,
//     }, {
//       name: '叉燒飯',
//       quantity: 3,
//       like: null,
//     }],
//     time: '2025-06-23 19:00',
//     rating: 5,
//     like: null,
//   },
// );
// 如果有來自Pinia的參數

const id = ref(1)

onMounted(() => {
  orders.value.push(findorder(id))
  // 初始化訂單評分狀態
})

function findorder(id) {
  axios.get(`/api/orders/user/${id.value}`)
    .then(function (response) {

      console.log("orders")
      orders.value = response.data
      console.log(orders.value)
      // response.data.forEach(element => {
      //   console.log(element)
      //   orders.value.push(element);
      // });
      // console.log(`ordersValue:${orders.value}`)
  }).catch(function (error) {
    console.log(error);
  })

}

// 重新訂購功能
const reorder = (order) => {
  alert(`重新訂購：${order.store}`); // 修正 alert 內容
};
</script>

<template>
  <div class="order-history-container">
    <h4 class="mb-4 text-center">
      <strong>歷史訂單</strong>
    </h4>
    <div
      v-for="order in orders"
      :key="order.id"
      class="order-item-card d-flex align-items-start p-3 mb-3 rounded-lg shadow-sm"
    >
      <img
        :src="order.store.photo"
        alt="店家圖片"
        class="me-3 rounded-circle border border-light"
        style="width: 70px; height: 70px; object-fit: cover;"
      >
      <div class="flex-grow-1">
        <div class="d-flex w-100 justify-content-between align-items-center mb-2">
          <h5 class="mb-0 text-primary">
            {{ order.store.name }}<!--店家名稱-->
          </h5>
          <h4 class="mb-0 text-danger fw-bold">
            ${{ order.total }}<!--訂單總價-->
          </h4>
        </div>
        <p class="mb-2 text-muted small">
          訂購時間: {{ order.createTime }}
        </p>

        <div class="mb-3">
          <p v-for="detail in order.orderDetails" :key="detail.id" class="mb-1 fw-medium">
            <span class="text-dark">{{ detail.food.name }}  x  {{ detail.quantity }}</span>
            <!-- <span v-if="food.spec" class="text-secondary small"> ({{  }})</span> -->
          </p>
        </div>

        <div class="d-flex justify-content-end align-items-center mt-3">
          <button class="btn btn-outline-danger btn-sm rounded-pill px-3" @click="reorder(order)">
            選擇想要重新訂購的項目
          </button>
        </div>

        <div class="mt-3 pt-3 border-top d-flex justify-content-between align-items-center">
          <RatingModal :order="order" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 外部容器，保持與 RestaurantTemplate 中 tab-menu-container 相似的寬度限制 */
.order-history-container {
  max-width: 600px; /* 與 tab-menu-container 保持一致的 max-width */
  margin: 2rem auto; /* 水平居中，上下留一些空間 */
  padding: 0 1rem; /* 左右內邊距，防止內容貼邊 */
}

/* 訂單卡片樣式 */
.order-item-card {
  background-color: #fff; /* 使用白色背景 */
  border: 1px solid #e0e0e0; /* 淺灰色邊框 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* 柔和的陰影 */
  transition: transform 0.2s ease, box-shadow 0.2s ease; /* 添加過渡效果 */
}

.order-item-card:hover {
  transform: translateY(-3px); /* 鼠標懸停時輕微上移 */
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12); /* 懸停時陰影加深 */
}

/* 圓角類，Bootstrap 預設的 rounded-lg 已經很不錯 */
.rounded-lg {
  border-radius: 0.5rem !important; /* 確保圓角較明顯 */
}

/* 圖片圓形 */
.rounded-circle {
  border-radius: 50% !important;
}

/* 重新訂購按鈕樣式 */
.btn-outline-danger {
  color: var(--bs-danger);
  border-color: var(--bs-danger);
}

.btn-outline-danger:hover {
  background-color: var(--bs-danger);
  color: #fff;
}

/* 覆蓋部分 Bootstrap 文本顏色 */
.text-primary {
  color: #333 !important; /* 可以根據你的主題調整主要文本顏色 */
}
</style>