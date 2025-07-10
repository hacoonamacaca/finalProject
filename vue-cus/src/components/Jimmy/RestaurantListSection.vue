<template>
<section class="restaurant-list">
    <div class="restaurant-card" v-for="restaurant in restaurants" :key="restaurant.id">
    <img
        :src="restaurant.image" :alt="restaurant.name"
        @click="navigateToRestaurant(restaurant.id)"
        style="cursor: pointer;"
    />
    <div class="info">
        <h3>{{ restaurant.name }}</h3>
        <p>
          {{ restaurant.categoryNames && restaurant.categoryNames.length > 0 ? restaurant.categoryNames.join(' / ') : '無類別' }}
          •
          {{ restaurant.isOpen ? '營業中' : '休息中' }}
        </p>
        <p>
        {{ restaurant.score }}★
          <span class="comment-trigger-text" @click="openComment(restaurant.id)" style="cursor: pointer;">
            ({{ restaurant.comments ? restaurant.comments.length : 0 }} 則評論) 
          </span>
        </p>
        <!-- <div class="tags">
        <span v-for="tag in restaurant.tags" :key="tag">{{ tag }}</span> 
        </div> -->
    </div>
    </div>
</section>

<Comment 
    v-if="showComment"
    :storeId="selectedStoreId"
    @close="showComment = false"
  />

</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Comment from '@/components/Jimmy/Comment.vue';

const props = defineProps({
  restaurants: {
    type: Array,
    required: true,
    default: () => [],
  },
});

const router = useRouter();

// 控制評論模態框顯示的狀態
const showComment = ref(false);
const selectedStoreId = ref(null); // 用於儲存當前點擊的餐廳 ID

const navigateToRestaurant = (id) => {
  router.push(`/restaurant/${id}`);
};

// 打開評論模態框的函數
const openComment = (storeId) => {
  selectedStoreId.value = storeId;
  showComment.value = true;
};

</script>
  
  <style scoped>
  /* 餐廳列表的 CSS */
  .restaurant-list {
    flex: 1;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
  
  .restaurant-card {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba;
    transition: transform 0.2s;
    height: 300px;
  }

  .restaurant-list .restaurant-card:hover {
    transform: translateY(-3px); /* 懸停效果 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .restaurant-card img {
    width: 100%;
    height: 160px;
    object-fit: cover;
  }
  
  .restaurant-card .info {
    padding: 15px;
  }
  
  .restaurant-card h3 {
    font-size: 18px;
    margin-bottom: 8px;
  }
  
  .restaurant-card p {
    color: #666;
    font-size: 14px;
    margin-bottom: 5px;
  }
  
  .restaurant-card .tags {
    display: flex;
    gap: 10px;
    margin-top: 10px;
  }
  
  .restaurant-card .tags span {
    background-color: #f0f0f0;
    padding: 5px 10px;
    border-radius: 12px;
    font-size: 12px;
  }

  /* 新增的評論觸發文字樣式 */
.comment-trigger-text {
  cursor: pointer;
  color: #007bff; /* 藍色鏈接 */
  text-decoration: underline;
  font-size: 13px; /* 調整字體大小 */
  display: block; /* 確保獨佔一行 */
  margin-top: 5px;
}

.comment-trigger-text:hover {
  color: #0056b3;
}
  </style>