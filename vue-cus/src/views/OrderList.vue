<script setup>
import { ref } from 'vue';
// 引入 RatingModal 組件
import RatingModal from '@/components/ReviewModal.vue';
// 引入 Bootstrap JS

// 引入 Bootstrap CSS 和 Icons (確保你的 main.js 或其他地方已全局引入，如果沒有，這裡也可以引入)


// 模擬訂單數據
const orders = ref([
  {
    id: 1,
    store: '店家名稱1',
    img: 'https://www.discoverhongkong.com/content/dam/dhk/intl/explore/dining/hong-kong-restaurants-by-the-sea/hue-960x720.jpg',
    price: 499,
    foods: [{
      name: '綠茶',
      quantity: 1,
      spec:'中杯,溫,無糖,六分糖',
      like: null,
      }, {
        name: '紅茶拿鐵',
        quantity: 2,
        spec:'中杯,溫,無糖,六分糖',
        like: null,
      }, {
        name: '叉燒飯',
        quantity: 3,
        like: null,
      }],
    time: '2025-06-24 18:30',
    rating: 0, // 初始未評分
    like: null,
  },
  {
    id: 2,
    img: 'https://www.discoverhongkong.com/content/dam/dhk/intl/explore/dining/hong-kong-restaurants-by-the-sea/hue-960x720.jpg',
    store: '店家名稱2',
    price: 699,
    foods: [{
      name: '綠茶',
      quantity: 1,
      spec:'中杯,溫,無糖,六分糖',
      like: null,
      }, {
        name: '紅茶拿鐵',
        quantity: 2,
        spec:'中杯,溫,無糖,六分糖',
        like: null,
      }, {
        name: '叉燒飯',
        quantity: 3,
        like: null,
      }],
    time: '2025-06-23 19:00',
    rating: 5,
    like: null,
  },
]);

// 臨時評分和模態框數據
// 新增：用於傳遞給模態框的實際評分

// 重新訂購功能
const reorder = (order) => {
  alert(`重新訂購：${order.title}`);
};


</script>

<template>
  <div>
    <h4 class="mb-4"><strong>歷史訂單</strong></h4>
     <div v-for="order in orders" :key="order.id" class="list-group-item">
      <div class="d-flex align-items-start"> 
        <img :src="order.img" alt="店家圖片" class="me-3 rounded" style="width: 60px; height: 60px; object-fit: cover;">
        <div class="flex-grow-1">
          <div class="d-flex w-100 justify-content-between">
            <h5 class="mb-1">{{ order.sotre }} 店家名稱</h5>
            <h4 class="text-danger">${{ order.price }}</h4>
          </div>
          <p class="mb-1"> 訂購時間: {{ order.time }} </p>
          
          <p v-for="food in order.foods" :key="food.name + order.id" class="mb-0"> 
            <span>{{food.name}} x{{food.quantity}} 
              <span v-if="food.spec">{{food.spec}}</span>
            </span>
          </p>
          
          <div class="d-flex justify-content-end align-items-center mt-2">
            <button class="btn btn-danger btn-sm" @click="reorder(order)">
              選擇想要重新訂購的項目
            </button>
          </div>
          <div class="d-flex justify-content-between align-items-center text-muted mt-3">
            <RatingModal :order="order"/>
          </div>
        </div>
      </div>
    </div>
  
  </div>
</template>

<style scoped>
.list-group-item {
  border-radius: 8px;
  margin-bottom: 10px;
}
.cursor-pointer {
  cursor: pointer;
}
</style>