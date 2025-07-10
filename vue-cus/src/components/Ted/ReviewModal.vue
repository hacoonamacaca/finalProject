<template>
  <span class="text-muted">
    <!-- 顯示評分狀態 -->
    評分
    <span class="text-warning ms-2" @mouseleave="resetTempRating">
      <i
        v-for="star in 5"
        :key="star"
        class="bi bi-star-fill"
        :class="{
          'text-warning': star <= (order.rating || (selectedOrder === order ? tempRating : 0)),
          'text-muted': star > (order.rating || (selectedOrder === order ? tempRating : 0)),
          'pointer': !order.rating
        }"
        @click="confirmRating(order, star)"
        @mouseover="hoverRating(order, star)"
      ></i>
    </span>
  </span> 
  
  <!-- 模態框 -->
  <div class="modal fade" ref="ratingModal" tabindex="-1" aria-labelledby="ratingModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered"> <!-- 添加 modal-dialog-centered 使模態框垂直居中 -->
      <div class="modal-content">
        <div class="modal-header flex-column align-items-start"> <!-- 修改為 flex-column 使星數移到標題下方 -->
          <h5 class="modal-title mb-2" id="ratingModalLabel">您的評分是</h5>
          <div class="text-warning" @mouseleave="resetModalTempRating">
            <i
              v-for="star in 5"
              :key="star"
              class="bi bi-star-fill me-1 fs-5" 
              :class="{
                'text-warning': star <= (modalTempRating || currentOrderRating),
                'text-muted': star > (modalTempRating || currentOrderRating),
                'pointer': true
              }"
              @click="updateModalRating(star)"
              @mouseover="hoverModalRating(star)"
            ></i>
          </div>
          <button type="button" class="btn-close position-absolute top-0 end-0 m-2" data-bs-dismiss="modal" aria-label="Close"></button> <!-- 調整關閉按鈕位置 -->
        </div>
        <div class="modal-body">
          <div class="d-flex align-items-start">
            <img :src="order.img" alt="店家圖片" class="me-3 rounded" style="width: 60px; height: 60px; object-fit: cover;">
            <div class="flex-grow-1">
              <div class="d-flex w-100 justify-content-between align-items-center mb-2"> <!-- 添加 align-items-center 和 mb-2 -->
                <h5 class="mb-0">{{ order.sotre }} 店家名稱</h5>
                <h4 class="text-danger mb-0">${{ order.price }}</h4>
              </div>
              <p class="mb-2 text-muted">訂購時間: {{ order.time }}</p> <!-- 添加 text-muted 和 mb-2 -->
              <div v-for="food in order.foods" :key="food.name + order.id" class="d-flex align-items-center mb-2"> <!-- 使用 d-flex 確保按鈕對齊 -->
                <span class="flex-grow-1">{{ food.name }}</span>
                <div class="ms-3"> <!-- 使用 div 包裝按鈕並靠右 -->
                  <button
                    class="btn btn-sm btn-outline-primary me-2" 
                    :class="{ 'active': food.like === true }"
                    @click="setLike(food, true)"
                  >
                    <i class="bi bi-hand-thumbs-up-fill"></i>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger"
                    :class="{ 'active': food.like === false }"
                    @click="setLike(food, false)"
                  >
                    <i class="bi bi-hand-thumbs-down-fill"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer comment p-3"> <!-- 將 comment 移到 footer 並添加內距 -->
          <textarea class="form-control" id="comment" rows="3" placeholder="請輸入評論"></textarea>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button> <!-- 添加取消按鈕 -->
          <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="sendRating">確認</button> <!-- 改用 btn-primary -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive, watch } from 'vue';
import bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js';

// 將 order 包裝為 reactive 物件
const props = defineProps(['order']);
const order = reactive(props.order);

const selectedOrder = ref(null);
const modalMessage = ref('');
const currentOrderRating = ref(0);
const ratingModal = ref(null);
const tempRating = ref(0);
const modalTempRating = ref(0);

let modalInstance = null;

// 建立 Modal 實體
onMounted(() => {
  if (ratingModal.value) {
    modalInstance = new bootstrap.Modal(ratingModal.value);
    // 初始化 currentOrderRating 與 order.rating 同步
    currentOrderRating.value = order.rating || 0;
    // 初始化每個食品的 like 狀態
    order.foods.forEach(food => {
      if (food.like === undefined) {
        food.like = null;
      }
    });
  }
});

// 雙向監聽 order.rating 和 currentOrderRating
watch(currentOrderRating, (newRating) => {
  if (order.rating !== newRating) {
    order.rating = newRating;
  }
});
watch(() => order.rating, (newRating) => {
  if (currentOrderRating.value !== newRating) {
    currentOrderRating.value = newRating;
  }
});

// 主畫面滑鼠懸停時更新臨時評分
const hoverRating = (order, rating) => {
  if (!order.rating) {
    tempRating.value = rating;
    selectedOrder.value = order;
  }
};

// 主畫面滑鼠離開時重置臨時評分
const resetTempRating = () => {
  tempRating.value = 0;
  selectedOrder.value = null;
};

// 模態框滑鼠懸停時更新臨時評分
const hoverModalRating = (rating) => {
  modalTempRating.value = rating;
};

// 模態框滑鼠離開時重置臨時評分
const resetModalTempRating = () => {
  modalTempRating.value = 0;
};

// 模態框點擊星星更新評分
const updateModalRating = (rating) => {
  if (rating > 0) {
    currentOrderRating.value = rating;
    modalMessage.value = `訂單：${order.title} 已評分 ${rating} 星！`;
    modalTempRating.value = 0;
  }
};

// 主畫面點擊星星確認評分並顯示模態框
const confirmRating = (order, rating) => {
  if (!order.rating && rating > 0) {
    currentOrderRating.value = rating;
    modalMessage.value = `訂單：${order.title} 已評分 ${rating} 星！`;
    tempRating.value = 0;
    selectedOrder.value = null;
    if (modalInstance) {
      modalInstance.show();
    }
  }
};

// 處理按讚/倒讚，點擊已選中的狀態則取消
const setLike = (food, value) => {
  if (food.like === value) {
    food.like = null; // 再按一次取消
  } else {
    food.like = value; // 設置按讚（true）或倒讚（false）
  }
};

// 傳送評分並關閉模態框
const sendRating = () => {
  const comment = document.getElementById('comment').value;
  const foodLikes = order.foods.map(food => ({
    name: food.name,
    like: food.like
  }));
  console.log('評分已傳送：', { rating: currentOrderRating.value, foodLikes, comment });
  if (modalInstance) {
    modalInstance.hide();
  }
};
</script>

<style scoped>
.pointer {
  cursor: pointer !important;
}
</style>