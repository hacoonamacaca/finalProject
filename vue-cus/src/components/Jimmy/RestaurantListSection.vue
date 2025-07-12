<template>
<section class="restaurant-list">
  <div class="restaurant-card" v-for="restaurant in restaurants" :key="restaurant.id">
            <div class="image-container">
                <img :src="restaurant.photo || 'https://via.placeholder.com/280x160'" :alt="restaurant.name"
                    @click="navigateToRestaurant(restaurant.id)" style="cursor: pointer;" />
                <i class="favorite-icon bi"
                    :class="{ 'bi-heart-fill': restaurant.isFavorited, 'bi-heart': !restaurant.isFavorited }"
                    @click.stop="toggleFavorite(restaurant)">
                </i>
            </div>
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
import { ref, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import Comment from '@/components/Jimmy/Comment.vue';
import { useUserStore } from '@/stores/user'; 
import axios from '@/plungins/axios.js'; 

const userStore = useUserStore();
const currentUserId = computed(() => userStore.userId); // 獲取當前用戶ID

const props = defineProps({
  restaurants: {
    type: Array,
    required: true,
    default: () => [],
  },
});

const emit = defineEmits(['update:favoriteStatus']); // **新增：宣告一個發射事件**
const router = useRouter();

// 控制評論模態框顯示的狀態
const showComment = ref(false);
const selectedStoreId = ref(null); // 用於儲存當前點擊的餐廳 ID

const navigateToRestaurant = (id) => {
  router.push(`/restaurant/${id}`);
};

// 打開評論模態框的函數
const openComment = (storeId) => {
console.log("User:"+userStore.userId);
  selectedStoreId.value = storeId;
  showComment.value = true;
};

// --- 新增收藏功能相關方法 ---
const toggleFavorite = async (restaurant) => {
    if (!currentUserId.value) {
        alert('請先登入才能收藏餐廳！');
        return;
    }

    const storeId = restaurant.id;
    const userId = currentUserId.value;
    let newIsFavorited; // 用於儲存新的收藏狀態

    console.log(`RestaurantListSection.vue: 點擊收藏按鈕，當前狀態 restaurant.isFavorited: ${restaurant.isFavorited}`); // 新增

    try {
        let response;
        if (restaurant.isFavorited) {
            response = await axios.delete(`/api/stores/${storeId}/favorite/${userId}`);
            if (response.data.success) {
                newIsFavorited = false; // 新狀態為 false
                // alert('已取消收藏！');
            } else {
                alert('取消收藏失敗！');
                return; // 如果失敗，不繼續更新狀態
            }
        } else {
            response = await axios.post(`/api/stores/${storeId}/favorite/${userId}`);
            if (response.data.success) {
                newIsFavorited = true; // 新狀態為 true
                // alert('已成功收藏！');
            } else {
                alert('收藏失敗！');
                return; // 如果失敗，不繼續更新狀態
            }
        }

        console.log("收藏操作結果:", response.data);

        // **重要修改：發射事件，通知父組件更新數據**
        emit('update:favoriteStatus', {
            storeId: storeId,
            isFavorited: newIsFavorited
        });
        console.log(`RestaurantListSection.vue: 發射 update:favoriteStatus 事件，storeId: ${storeId}, isFavorited: ${newIsFavorited}`); // 新增

    } catch (error) {
        console.error('收藏/取消收藏操作出錯:', error);
        alert('收藏/取消收藏操作發生錯誤！');
    }
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
    position: relative; /* 新增：讓子元素可以相對定位 */
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

/* 愛心圖示樣式 */
.favorite-icon {
    position: absolute;
    top: 10px;
    right: 10px;
    color: white; /* 預設白色 */
    text-shadow: 0 0 2px rgb(255, 255, 255); /* 增加陰影，讓圖標更明顯 */
    cursor: pointer;
    /* transition: color 0.1s; */
    z-index: 10; /* 確保在圖片上方 */
}

.favorite-icon.bi-heart-fill {
    color: #c50f2a; /* 實心愛心顏色，例如粉紅色 */
}

.favorite-icon:hover {
    transform: scale(1.1); /* 懸停放大效果 */
}
  </style>